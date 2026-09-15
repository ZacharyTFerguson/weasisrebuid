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

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import javax.swing.KeyStroke;
import org.weasis.core.api.gui.util.ActionW;
import org.weasis.core.api.gui.util.ShortcutManager;
import org.weasis.core.ui.editor.image.dockable.MeasureTool;
import org.weasis.core.ui.model.graphic.DragGraphic;
import org.weasis.core.ui.model.graphic.Graphic;
import org.weasis.core.ui.model.graphic.imp.angle.AngleToolGraphic;
import org.weasis.core.ui.model.graphic.imp.area.PolygonGraphic;
import org.weasis.core.ui.model.graphic.imp.line.PolylineGraphic;

/** Mouse → pan / zoom / W/L / scroll / crosshair. Zoom is centered on the view, not the cursor. */
public class ImageViewerEventManager {

  private final DefaultView2d<?> view;
  private final ShortcutManager shortcuts = new ShortcutManager();
  private final DrawingsKeyListeners drawingsKeys;
  private final GraphicMouseHandler graphicMouse = new GraphicMouseHandler();
  private int lastX;
  private int lastY;
  private int drawHandle;

  public ImageViewerEventManager(DefaultView2d<?> view) {
    this.view = view;
    this.drawingsKeys = new DrawingsKeyListeners(view);
  }

  public DefaultView2d<?> getView() {
    return view;
  }

  public void mousePressed(MouseEvent e) {
    lastX = e.getX();
    lastY = e.getY();
    String action = buttonAction(e);
    if (MouseActions.CROSSHAIR.equals(MouseActions.normalize(action))) {
      view.setCrosshairFromView(e.getX(), e.getY());
      return;
    }
    if (view.getDrawing() == null
        && (view.graphicAt(e.getX(), e.getY()) != null || !drawingAction(action))) {
      if (graphicMouse.mousePressed(e, view)) {
        return;
      }
    }
    if (drawingAction(action)) {
      onDrawPressed(e);
    }
  }

  public void mouseDragged(MouseEvent e) {
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
    apply(action, dx, dy);
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
    ActionW action = shortcuts.getAction(KeyStroke.getKeyStroke(e.getKeyCode(), 0));
    if (action == ActionW.ANNOTATIONS) {
      view.cycleAnnotations();
    } else if (action == ActionW.KO) {
      view.toggleKeyImage();
    } else if (action == ActionW.CROSSHAIR) {
      view.getMouseActions().setLeft(MouseActions.CROSSHAIR);
    } else if (action == ActionW.NONE) {
      view.getMouseActions().setLeft(MouseActions.NONE);
    } else if (action == ActionW.MEASURE) {
      view.getMouseActions().setLeft(MouseActions.MEASURE);
    } else if (action == ActionW.DRAW) {
      view.getMouseActions().setLeft(MouseActions.DRAW);
    } else if (action == ActionW.RESET) {
      view.resetView("-a");
    }
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

  void onDrawPressed(MouseEvent e) {
    Point2D.Double p = new Point2D.Double(e.getX(), e.getY());
    Graphic current = view.getDrawing();
    if (e.getClickCount() > 1 && current != null) {
      view.setDrawing(null);
      drawHandle = 0;
      return;
    }
    if (current == null) {
      Graphic created = MeasureTool.create(view.getMeasureTool());
      if (!(created instanceof DragGraphic drag)) {
        return;
      }
      drag.setHandlePoint(0, p);
      drawHandle = 0;
      if (!isOpenPath(created)) {
        drag.setHandlePoint(1, p);
        drawHandle = 1;
      }
      view.addGraphic(created);
      view.setDrawing(created);
      return;
    }
    if (current instanceof DragGraphic drag && isOpenPath(current)) {
      drawHandle++;
      drag.setHandlePoint(drawHandle, p);
    } else if (current instanceof AngleToolGraphic angle) {
      angle.setHandlePoint(2, p);
      view.setDrawing(null);
      drawHandle = 0;
    }
  }

  void onDrawDragged(MouseEvent e) {
    Graphic current = view.getDrawing();
    if (current instanceof DragGraphic drag) {
      drag.setHandlePoint(drawHandle, new Point2D.Double(e.getX(), e.getY()));
    }
  }

  void onDrawReleased(MouseEvent e) {
    Graphic current = view.getDrawing();
    if (current == null) {
      return;
    }
    if (current instanceof DragGraphic drag) {
      drag.setHandlePoint(drawHandle, new Point2D.Double(e.getX(), e.getY()));
    }
    if (isOpenPath(current) || current instanceof AngleToolGraphic) {
      return;
    }
    view.setDrawing(null);
    drawHandle = 0;
  }

  static boolean isOpenPath(Graphic graphic) {
    return graphic instanceof PolylineGraphic || graphic instanceof PolygonGraphic;
  }
}
