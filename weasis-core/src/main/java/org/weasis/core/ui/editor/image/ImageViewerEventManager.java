/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.editor.image;

import java.awt.AWTEvent;
import java.awt.IllegalComponentStateException;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import org.weasis.core.api.gui.util.ActionW;
import org.weasis.core.api.gui.util.ShortcutManager;
import org.weasis.core.api.service.UICore;
import org.weasis.core.ui.editor.image.dockable.MeasureTool;
import org.weasis.core.ui.model.graphic.DragGraphic;
import org.weasis.core.ui.model.graphic.Graphic;
import org.weasis.core.ui.model.graphic.imp.angle.AngleToolGraphic;
import org.weasis.core.ui.model.graphic.imp.area.PolygonGraphic;
import org.weasis.core.ui.model.graphic.imp.area.RectangleGraphic;
import org.weasis.core.ui.model.graphic.imp.line.LineGraphic;
import org.weasis.core.ui.model.graphic.imp.line.PolylineGraphic;
import org.weasis.core.ui.util.PrintOptions;

/** Mouse → pan / zoom / W/L / scroll / crosshair. Zoom is centered on the view, not the cursor. */
public class ImageViewerEventManager {

  private final DefaultView2d<?> view;
  private final ShortcutManager shortcuts = new ShortcutManager();
  private final DrawingsKeyListeners drawingsKeys;
  private final GraphicMouseHandler graphicMouse = new GraphicMouseHandler();
  private int lastX;
  private int lastY;
  private int drawHandle;
  private boolean angleRayClicked;

  public ImageViewerEventManager(DefaultView2d<?> view) {
    this.view = view;
    this.drawingsKeys = new DrawingsKeyListeners(view);
    DrawStroke.arm();
  }

  public DefaultView2d<?> getView() {
    return view;
  }

  public void mousePressed(MouseEvent e) {
    lastX = e.getX();
    lastY = e.getY();
    if (view != null) {
      view.requestFocusInWindow();
    }
    String action = buttonAction(e);
    if (MouseActions.CROSSHAIR.equals(MouseActions.normalize(action))) {
      view.setCrosshairFromView(e.getX(), e.getY());
      return;
    }
    if (MouseActions.CONTEXT_MENU.equals(MouseActions.normalize(action))) {
      view.showContextMenu(e.getX(), e.getY());
      return;
    }
    if (view.getDrawing() == null && !drawingAction(action)) {
      if (graphicMouse.mousePressed(e, view)) {
        return;
      }
    }
    if (drawingAction(action)) {
      onDrawPressed(e);
    }
  }

  public void mouseDragged(MouseEvent e) {
    if (ViewTransferHandler.dragging() != null && !drawingAction(buttonAction(e))) {
      return;
    }
    int dx = e.getX() - lastX;
    int dy = e.getY() - lastY;
    lastX = e.getX();
    lastY = e.getY();
    String action = buttonAction(e);
    if (MouseActions.CROSSHAIR.equals(MouseActions.normalize(action))) {
      view.setCrosshairFromView(e.getX(), e.getY());
      return;
    }
    if (graphicMouse.isSelecting()) {
      graphicMouse.mouseDragged(e, view);
      return;
    }
    if (drawingAction(action)) {
      onDrawDragged(e);
      return;
    }
    int mods = e.getModifiersEx();
    int factor = 1;
    if ((mods & InputEvent.CTRL_DOWN_MASK) != 0) {
      factor = (mods & InputEvent.SHIFT_DOWN_MASK) != 0 ? 4 : 2;
    }
    apply(action, dx * factor, dy * factor);
  }

  public void mouseReleased(MouseEvent e) {
    lastX = e.getX();
    lastY = e.getY();
    if (graphicMouse.isSelecting()) {
      graphicMouse.mouseReleased(e, view);
      return;
    }
    if (drawingAction(view.getMouseActions().getLeft())) {
      onDrawReleased(e);
    }
  }

  public void mouseWheelMoved(MouseWheelEvent e) {
    String action = MouseActions.normalize(view.getMouseActions().getWheel());
    if (MouseActions.ZOOM.equals(action)) {
      view.increaseZoom(-e.getWheelRotation());
    } else if (MouseActions.isScroll(action)) {
      view.setFrameIndex(view.getFrameIndex() + e.getWheelRotation());
    }
  }

  public void keyPressed(KeyEvent e) {
    if (e == null || view == null) {
      return;
    }
    if (drawingsKeys.keyPressed(e)) {
      return;
    }
    if (handleNavigation(e)) {
      return;
    }
    int mods =
        e.getModifiersEx()
            & (InputEvent.CTRL_DOWN_MASK
                | InputEvent.ALT_DOWN_MASK
                | InputEvent.SHIFT_DOWN_MASK
                | InputEvent.META_DOWN_MASK);
    ActionW action = shortcuts.getAction(KeyStroke.getKeyStroke(e.getKeyCode(), mods));
    if (action == null) {
      return;
    }
    if (action == ActionW.ANNOTATIONS) {
      view.cycleAnnotations();
    } else if (action == ActionW.KO) {
      view.toggleKeyImage();
    } else if (action == ActionW.CINE) {
      view.toggleCine();
    } else if (action == ActionW.CONTEXTMENU) {
      view.showContextMenu(0, 0);
    } else if (action == ActionW.PRINT) {
      view.requestPrint(new PrintOptions());
    } else if (action == ActionW.RESET) {
      view.resetView("-a");
    } else if (mouseLeftAction(action)) {
      view.getMouseActions().setLeft(action.cmd());
    }
  }

  boolean handleNavigation(KeyEvent e) {
    int mods = e.getModifiersEx();
    boolean alt = (mods & InputEvent.ALT_DOWN_MASK) != 0;
    boolean shift = (mods & InputEvent.SHIFT_DOWN_MASK) != 0;
    boolean ctrl = (mods & InputEvent.CTRL_DOWN_MASK) != 0;
    int code = e.getKeyCode();
    if (alt && code == KeyEvent.VK_S && !ctrl) {
      view.toggleSegmentations();
      return true;
    }
    if (alt && !ctrl) {
      if (code == KeyEvent.VK_R) {
        view.setRotation(view.getRotation() + 90);
        return true;
      }
      if (code == KeyEvent.VK_L) {
        view.setRotation(view.getRotation() - 90);
        return true;
      }
      if (code == KeyEvent.VK_F) {
        view.toggleFlip();
        return true;
      }
    }
    if (ctrl && !alt) {
      if (code == KeyEvent.VK_SPACE) {
        view.cycleLeftMouseAction();
        return true;
      }
      if (code == KeyEvent.VK_ADD || code == KeyEvent.VK_PLUS || code == KeyEvent.VK_EQUALS) {
        view.increaseZoom(1);
        return true;
      }
      if (code == KeyEvent.VK_SUBTRACT || code == KeyEvent.VK_MINUS) {
        view.increaseZoom(-1);
        return true;
      }
      if (code == KeyEvent.VK_ENTER) {
        view.resetView("zoom");
        return true;
      }
    }
    if (alt && isArrow(code)) {
      int step = shift ? 10 : 5;
      double dx = 0;
      double dy = 0;
      if (code == KeyEvent.VK_LEFT) {
        dx = -step;
      } else if (code == KeyEvent.VK_RIGHT) {
        dx = step;
      } else if (code == KeyEvent.VK_UP) {
        dy = -step;
      } else {
        dy = step;
      }
      view.setPan(view.getPanX() + dx, view.getPanY() + dy);
      return true;
    }
    if (code == KeyEvent.VK_F11) {
      view.toggleFullScreen();
      return true;
    }
    if (!ctrl && !alt && code >= KeyEvent.VK_0 && code <= KeyEvent.VK_9) {
      view.applyPreset(code - KeyEvent.VK_0);
      return true;
    }
    if (ctrl) {
      return switch (code) {
        case KeyEvent.VK_LEFT -> {
          view.nextStudy(-1);
          yield true;
        }
        case KeyEvent.VK_RIGHT -> {
          view.nextStudy(1);
          yield true;
        }
        case KeyEvent.VK_PAGE_UP -> {
          view.firstStudy();
          yield true;
        }
        case KeyEvent.VK_PAGE_DOWN -> {
          view.lastStudy();
          yield true;
        }
        case KeyEvent.VK_UP -> {
          view.nextPatient(-1);
          yield true;
        }
        case KeyEvent.VK_DOWN -> {
          view.nextPatient(1);
          yield true;
        }
        case KeyEvent.VK_HOME -> {
          view.firstPatient();
          yield true;
        }
        case KeyEvent.VK_END -> {
          view.lastPatient();
          yield true;
        }
        default -> false;
      };
    }
    return switch (code) {
      case KeyEvent.VK_UP -> {
        view.nextFrame(shift ? -10 : -1);
        yield true;
      }
      case KeyEvent.VK_DOWN -> {
        view.nextFrame(shift ? 10 : 1);
        yield true;
      }
      case KeyEvent.VK_HOME -> {
        view.firstFrame();
        yield true;
      }
      case KeyEvent.VK_END -> {
        view.lastFrame();
        yield true;
      }
      case KeyEvent.VK_LEFT -> {
        view.nextSeries(-1);
        yield true;
      }
      case KeyEvent.VK_RIGHT -> {
        view.nextSeries(1);
        yield true;
      }
      case KeyEvent.VK_PAGE_UP -> {
        view.firstSeries();
        yield true;
      }
      case KeyEvent.VK_PAGE_DOWN -> {
        view.lastSeries();
        yield true;
      }
      default -> false;
    };
  }

  static boolean isArrow(int code) {
    return code == KeyEvent.VK_LEFT
        || code == KeyEvent.VK_RIGHT
        || code == KeyEvent.VK_UP
        || code == KeyEvent.VK_DOWN;
  }

  static boolean mouseLeftAction(ActionW action) {
    return action == ActionW.PAN
        || action == ActionW.WINLEVEL
        || action == ActionW.SCROLL_SERIES
        || action == ActionW.ZOOM
        || action == ActionW.ROTATION
        || action == ActionW.CROSSHAIR
        || action == ActionW.NONE
        || action == ActionW.MEASURE
        || action == ActionW.DRAW;
  }

  public void apply(String action, int dx, int dy) {
    String a = MouseActions.normalize(action);
    if (MouseActions.isScroll(a)) {
      view.setFrameIndex(view.getFrameIndex() + (dy > 0 ? 1 : -1));
      return;
    }
    switch (a) {
      case MouseActions.PAN -> view.setPan(view.getPanX() + dx, view.getPanY() + dy);
      case MouseActions.ZOOM -> view.increaseZoom(dy < 0 ? 1 : dy > 0 ? -1 : 0);
      case MouseActions.ROTATION -> view.setRotation(view.getRotation() + dx);
      case MouseActions.WINLEVEL -> applyWindowLevel(dx, dy);
      case MouseActions.CROSSHAIR ->
          view.setCrosshairFromView(view.getCrosshairX() + dx, view.getCrosshairY() + dy);
      case MouseActions.CONTEXT_MENU, MouseActions.NONE -> {
        // menu / idle
      }
      case MouseActions.DRAW, MouseActions.MEASURE -> {
        // handled in mousePressed / dragged / released
      }
      default -> {
        // unknown
      }
    }
  }

  protected void applyWindowLevel(int dx, int dy) {
    // DICOM View2d overrides
  }

  String buttonAction(MouseEvent e) {
    if ((e.getModifiersEx() & MouseEvent.BUTTON2_DOWN_MASK) != 0) {
      return view.getMouseActions().getMiddle();
    }
    if ((e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) != 0) {
      return view.getMouseActions().getRight();
    }
    return view.getMouseActions().getLeft();
  }

  static boolean drawingAction(String action) {
    String a = MouseActions.normalize(action);
    return MouseActions.MEASURE.equals(a) || MouseActions.DRAW.equals(a);
  }

  Point2D.Double imagePoint(MouseEvent e) {
    return clampImage(view.viewToImage(e.getX(), e.getY()));
  }

  Point2D.Double clampImage(Point2D.Double p) {
    if (p == null) {
      return new Point2D.Double();
    }
    BufferedImage src = view.getSourceImage();
    if (src == null) {
      return p;
    }
    p.x = Math.max(0, Math.min(src.getWidth() - 1.0, p.x));
    p.y = Math.max(0, Math.min(src.getHeight() - 1.0, p.y));
    return p;
  }

  void onDrawPressed(MouseEvent e) {
    Point2D.Double p = imagePoint(e);
    Graphic current = matchingDrawing();
    if (e.getClickCount() > 1 && current != null) {
      finishDrawing();
      return;
    }
    if (current == null) {
      startGraphic(p);
      return;
    }
    if (current instanceof DragGraphic drag && isOpenPath(current)) {
      drawHandle++;
      drag.setHandlePoint(drawHandle, p);
    } else if (current instanceof AngleToolGraphic angle) {
      angleRayClicked = true;
      angle.setHandlePoint(2, p);
      finishDrawing();
    }
  }

  Graphic matchingDrawing() {
    Graphic current = view.getDrawing();
    if (current != null && !MeasureTool.isType(current, view.activeMeasureTool())) {
      view.abandonDrawing();
      return null;
    }
    return current;
  }

  void startGraphic(Point2D.Double p) {
    Graphic created = MeasureTool.create(view.activeMeasureTool());
    if (!(created instanceof DragGraphic drag)) {
      return;
    }
    drag.setHandlePoint(0, p);
    drag.setHandlePoint(1, p);
    if (created instanceof AngleToolGraphic) {
      drag.setHandlePoint(2, p);
    }
    drawHandle = 1;
    angleRayClicked = false;
    view.addGraphic(created);
    view.setDrawing(created);
    DrawStroke.INSTANCE.drawingView = view;
  }

  void finishDrawing() {
    view.setDrawing(null);
    drawHandle = 0;
    DrawStroke.INSTANCE.drawingView = null;
  }

  void onDrawDragged(MouseEvent e) {
    Graphic current = view.getDrawing();
    if (current instanceof DragGraphic drag) {
      drag.setHandlePoint(drawHandle, imagePoint(e));
    }
    view.repaint();
  }

  void onDrawReleased(MouseEvent e) {
    Graphic current = view.getDrawing();
    if (current == null) {
      return;
    }
    if (current instanceof DragGraphic drag) {
      drag.setHandlePoint(drawHandle, imagePoint(e));
    }
    if (current instanceof AngleToolGraphic angle) {
      completeAngle(angle);
      return;
    }
    if (isOpenPath(current)) {
      dropIfDegenerate(current);
      view.repaint();
      return;
    }
    if (current instanceof RectangleGraphic rect) {
      rect.ensureArea(32, view.getSourceImage());
    }
    dropIfDegenerate(current);
    finishDrawing();
  }

  void completeAngle(AngleToolGraphic angle) {
    if (!angleRayClicked) {
      angle.setRightRay();
      Point2D.Double p = angle.getHandlePoint(2);
      if (p != null) {
        angle.setHandlePoint(2, clampImage(p));
      }
    }
    dropIfDegenerate(angle);
    finishDrawing();
    view.repaint();
  }

  void dropIfDegenerate(Graphic graphic) {
    if (degenerate(graphic)) {
      view.removeGraphic(graphic);
    }
  }

  public static boolean degenerate(Graphic graphic) {
    if (graphic instanceof LineGraphic line && !(graphic instanceof PolylineGraphic)) {
      return line.getLength() < 0.5;
    }
    if (graphic instanceof PolylineGraphic poly) {
      return PolylineGraphic.pathLength(poly.getPts()) < 0.5;
    }
    if (graphic instanceof AngleToolGraphic angle) {
      return angle.getAngleDegrees() < 0.5;
    }
    if (graphic instanceof RectangleGraphic rect) {
      return thinRect(rect);
    }
    return false;
  }

  static boolean thinRect(RectangleGraphic rect) {
    if (rect.getShape() == null) {
      return true;
    }
    var box = rect.getShape().getBounds2D();
    return box.getWidth() < 0.5 || box.getHeight() < 0.5;
  }

  static boolean isOpenPath(Graphic graphic) {
    return graphic instanceof PolylineGraphic || graphic instanceof PolygonGraphic;
  }

  /**
   * Headed glass / TransferHandler can swallow {@code mouseDragged} on View2d. Toolkit events still
   * reach here so D / A / Y / G paint on the chest, extra A/Y clicks land, and Delete removes a
   * selected graphic.
   */
  public static final class DrawStroke implements AWTEventListener {
    static final DrawStroke INSTANCE = new DrawStroke();
    private boolean armed;
    volatile DefaultView2d<?> drawingView;
    volatile DefaultView2d<?> lastView;

    public static void rememberView(DefaultView2d<?> view) {
      INSTANCE.lastView = view;
    }

    public static void deleteOutside(KeyEvent ke) {
      INSTANCE.onKey(ke);
    }

    static void arm() {
      INSTANCE.armOnce();
    }

    void armOnce() {
      if (armed) {
        return;
      }
      armed = true;
      listenPointer();
      listenDeletes();
    }

    void listenPointer() {
      Toolkit.getDefaultToolkit()
          .addAWTEventListener(
              this,
              AWTEvent.MOUSE_EVENT_MASK
                  | AWTEvent.MOUSE_MOTION_EVENT_MASK
                  | AWTEvent.KEY_EVENT_MASK);
    }

    void listenDeletes() {
      KeyboardFocusManager.getCurrentKeyboardFocusManager()
          .addKeyEventDispatcher(this::dispatchDelete);
    }

    @Override
    public void eventDispatched(AWTEvent event) {
      if (event instanceof MouseEvent me) {
        onMouse(me);
      } else if (event instanceof KeyEvent ke) {
        onKey(ke);
      }
    }

    void onMouse(MouseEvent me) {
      if (me.isConsumed()) {
        return;
      }
      if (MeasureToolBar.redispatch(me)) {
        return;
      }
      if (me.getID() == MouseEvent.MOUSE_PRESSED) {
        onPress(me);
        return;
      }
      DefaultView2d<?> drawing = drawingView;
      if (drawing == null || drawing.getDrawing() == null) {
        return;
      }
      if (me.getID() == MouseEvent.MOUSE_DRAGGED) {
        drawing.getEventManager().onDrawDragged(local(me, drawing));
      } else if (me.getID() == MouseEvent.MOUSE_RELEASED) {
        drawing.getEventManager().onDrawReleased(local(me, drawing));
      }
    }

    void onPress(MouseEvent me) {
      if (!SwingUtilities.isLeftMouseButton(me)) {
        return;
      }
      if (toolbarPress(me)) {
        return;
      }
      DefaultView2d<?> hit = viewAt(me);
      if (hit == null) {
        return;
      }
      lastView = hit;
      drawingView = hit;
      if (me.getComponent() == hit) {
        return;
      }
      hit.getEventManager().mousePressed(local(me, hit));
    }

    static boolean toolbarPress(MouseEvent me) {
      if (MeasureToolBar.armAt(me) || me.getComponent() instanceof AbstractButton) {
        return true;
      }
      return MeasureToolBar.barAtScreen(eventScreen(me)) != null;
    }

    public void onKey(KeyEvent ke) {
      if (ke.getID() != KeyEvent.KEY_PRESSED || typing(ke) || !deleteKey(ke)) {
        return;
      }
      deletePluginGraphics(seedFrom(ke));
    }

    boolean dispatchDelete(KeyEvent ke) {
      if (ke.getID() != KeyEvent.KEY_PRESSED || typing(ke) || !deleteKey(ke)) {
        return false;
      }
      deletePluginGraphics(seedFrom(ke));
      return false;
    }

    static boolean deleteKey(KeyEvent ke) {
      return ke.getKeyCode() == KeyEvent.VK_DELETE || ke.getKeyCode() == KeyEvent.VK_BACK_SPACE;
    }

    static boolean typing(KeyEvent ke) {
      return ke.getComponent() instanceof javax.swing.text.JTextComponent;
    }

    DefaultView2d<?> seedFrom(KeyEvent ke) {
      if (ke.getComponent() instanceof DefaultView2d<?> v) {
        return v;
      }
      return seedView();
    }

    DefaultView2d<?> seedView() {
      return lastView != null ? lastView : drawingView;
    }

    void deletePluginGraphics(DefaultView2d<?> seed) {
      ImageViewerPlugin<?> plugin = pluginOf(seed);
      if (plugin != null) {
        plugin.deleteAllGraphics();
      }
      sweepLive(plugin, seed);
    }

    void sweepLive(ImageViewerPlugin<?> plugin, DefaultView2d<?> seed) {
      int n = 0;
      for (DefaultView2d<?> v : DefaultView2d.LIVE) {
        if (cleared(plugin, seed, v)) {
          n++;
        }
      }
      if (n == 0 && seed != null) {
        clearView(seed);
      }
    }

    static boolean cleared(ImageViewerPlugin<?> plugin, DefaultView2d<?> seed, DefaultView2d<?> v) {
      if (!samePlugin(plugin, seed, v)) {
        return false;
      }
      clearView(v);
      return true;
    }

    static boolean samePlugin(
        ImageViewerPlugin<?> plugin, DefaultView2d<?> seed, DefaultView2d<?> v) {
      if (plugin == null) {
        return v == seed;
      }
      return plugin == ImageViewerPlugin.pluginAbove(v) || stamped(plugin, v);
    }

    static boolean stamped(ImageViewerPlugin<?> plugin, DefaultView2d<?> v) {
      return plugin.equals(v.getClientProperty(ImageViewerPlugin.class));
    }

    static ImageViewerPlugin<?> pluginOf(DefaultView2d<?> seed) {
      ImageViewerPlugin<?> above = ImageViewerPlugin.pluginAbove(seed);
      if (above != null) {
        return above;
      }
      return UICore.getInstance().getFocusedImagePlugin();
    }

    static void clearView(DefaultView2d<?> v) {
      v.abandonDrawing();
      v.selectAllGraphics();
      v.deleteSelectedGraphics();
    }

    static DefaultView2d<?> viewAt(MouseEvent me) {
      Point screen = eventScreen(me);
      DefaultView2d.dropBoundsCache();
      DefaultView2d<?> cell = pluginCanvas(me, screen);
      return cell != null ? cell : liveOrComponent(me, screen);
    }

    static DefaultView2d<?> pluginCanvas(MouseEvent me, Point screen) {
      ImageViewerPlugin<?> plugin = pluginNear(me);
      return plugin == null ? null : plugin.canvasAt(screen);
    }

    static ImageViewerPlugin<?> pluginNear(MouseEvent me) {
      ImageViewerPlugin<?> above = ImageViewerPlugin.pluginAbove(me.getComponent());
      return above != null ? above : pluginOf(INSTANCE.lastView);
    }

    static DefaultView2d<?> liveOrComponent(MouseEvent me, Point screen) {
      DefaultView2d<?> under = DefaultView2d.atScreen(screen);
      return under != null ? under : componentView(me);
    }

    static DefaultView2d<?> componentView(MouseEvent me) {
      if (me.getComponent() instanceof DefaultView2d<?> v
          && DefaultView2d.boxContains(DefaultView2d.screenBox(v), eventScreen(me))) {
        return v;
      }
      return screenView(me);
    }

    static Point eventScreen(MouseEvent me) {
      try {
        return me.getLocationOnScreen();
      } catch (IllegalComponentStateException e) {
        return new Point(me.getXOnScreen(), me.getYOnScreen());
      }
    }

    static DefaultView2d<?> screenView(MouseEvent me) {
      try {
        JComponent cell = ViewTransferHandler.screenView(me.getLocationOnScreen());
        return cell instanceof DefaultView2d<?> v ? v : null;
      } catch (IllegalComponentStateException e) {
        return null;
      }
    }

    static MouseEvent local(MouseEvent me, DefaultView2d<?> view) {
      Point p = pointOn(me, view);
      return new MouseEvent(
          view,
          me.getID(),
          me.getWhen(),
          me.getModifiersEx(),
          p.x,
          p.y,
          me.getClickCount(),
          false,
          me.getButton());
    }

    static Point pointOn(MouseEvent me, DefaultView2d<?> view) {
      if (me.getComponent() == view) {
        return me.getPoint();
      }
      try {
        Point p = new Point(me.getLocationOnScreen());
        SwingUtilities.convertPointFromScreen(p, view);
        return p;
      } catch (Exception e) {
        return convertOrPoint(me, view);
      }
    }

    static Point convertOrPoint(MouseEvent me, DefaultView2d<?> view) {
      if (me.getComponent() == null) {
        return me.getPoint();
      }
      return SwingUtilities.convertPoint(me.getComponent(), me.getPoint(), view);
    }
  }
}
