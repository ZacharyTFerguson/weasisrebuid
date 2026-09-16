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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.gui.Insertable;
import org.weasis.core.ui.editor.image.dockable.MeasureTool;
import org.weasis.core.ui.model.graphic.imp.angle.AngleToolGraphic;
import org.weasis.core.ui.model.graphic.imp.line.LineGraphic;
import org.weasis.core.ui.model.graphic.imp.line.PolylineGraphic;

class MeasureToolBarHaveTest {

  @Test
  void toolbarButtonsMatchDocumentedTools() {
    MeasureToolBar bar = new MeasureToolBar();
    assertEquals("Measure", bar.getComponentName());
    assertEquals(Insertable.Type.TOOLBAR, bar.getType());
    assertEquals(MeasureToolBar.BUTTONS.length, bar.getComponent().getComponentCount());
    assertEquals("D", ((AbstractButton) bar.getComponent().getComponent(0)).getText());
    assertEquals("A", ((AbstractButton) bar.getComponent().getComponent(1)).getText());
    assertEquals("Y", ((AbstractButton) bar.getComponent().getComponent(2)).getText());
    assertEquals("G", ((AbstractButton) bar.getComponent().getComponent(3)).getText());
    assertEquals("B", ((AbstractButton) bar.getComponent().getComponent(4)).getText());
    assertEquals("measure-distance", bar.getComponent().getComponent(0).getName());
    assertEquals("measure-angle", bar.getComponent().getComponent(1).getName());
    assertEquals("measure-polyline", bar.getComponent().getComponent(2).getName());
    assertEquals("measure-rect", bar.getComponent().getComponent(3).getName());
    assertEquals("measure-textbox", bar.getComponent().getComponent(4).getName());
    assertTrue(bar.getComponent().getComponent(0) instanceof javax.swing.JToggleButton);
    assertTrue(bar.getComponent().getComponent(1) instanceof javax.swing.JToggleButton);
    assertTrue(bar.newGraphic() instanceof LineGraphic);
    assertTrue(((AbstractButton) bar.getComponent().getComponent(0)).isSelected());
    bar.setSelected("A");
    assertTrue(((AbstractButton) bar.getComponent().getComponent(1)).isSelected());
    assertTrue(bar.newGraphic() instanceof AngleToolGraphic);
    bar.setSelected(MeasureTool.POLYLINE);
    assertTrue(bar.newGraphic() instanceof PolylineGraphic);
  }

  @Test
  void clickDragReleaseDrawsDistanceOnView() {
    DefaultView2d<?> view = new DefaultView2d<>();
    MeasureToolBar bar = new MeasureToolBar();
    bar.bind(view);
    assertEquals(MouseActions.MEASURE, view.getMouseActions().getLeft());
    assertEquals(MeasureTool.DISTANCE, view.getMeasureTool());

    view.getEventManager().mousePressed(mouse(view, MouseEvent.MOUSE_PRESSED, 0, 0, 1));
    view.getEventManager().mouseDragged(mouse(view, MouseEvent.MOUSE_DRAGGED, 3, 4, 1));
    view.getEventManager().mouseReleased(mouse(view, MouseEvent.MOUSE_RELEASED, 3, 4, 1));

    assertEquals(1, view.getGraphicList().size());
    assertTrue(view.getGraphicList().getFirst() instanceof LineGraphic);
    LineGraphic line = (LineGraphic) view.getGraphicList().getFirst();
    assertEquals(5.0, line.getLength(), 1e-9);
    assertNotNull(line.getShape());
    assertTrue(line.getLabel()[0].contains("px"));
    assertNull(view.getDrawing());
  }

  @Test
  void angleNeedsSecondClickForVertexRay() {
    DefaultView2d<?> view = new DefaultView2d<>();
    view.setMeasureTool(MeasureTool.ANGLE);
    view.getMouseActions().setLeft(MouseActions.MEASURE);
    view.getEventManager().mousePressed(mouse(view, MouseEvent.MOUSE_PRESSED, 10, 0, 1));
    view.getEventManager().mouseDragged(mouse(view, MouseEvent.MOUSE_DRAGGED, 0, 0, 1));
    view.getEventManager().mouseReleased(mouse(view, MouseEvent.MOUSE_RELEASED, 0, 0, 1));
    assertNull(view.getDrawing());
    assertEquals(1, view.getGraphicList().size());
    AngleToolGraphic angle = (AngleToolGraphic) view.getGraphicList().getFirst();
    assertTrue(angle.getAngleDegrees() > 1.0);
    assertNotNull(angle.getShape());
    assertTrue(angle.getLabel()[0].contains("°"));
  }

  @Test
  void distanceStaysOnImagePixelsAfterZoomAndStillPaints() {
    DefaultView2d<?> view = sizedGrayView();
    MeasureToolBar bar = new MeasureToolBar();
    bar.bind(view);
    view.getEventManager().mousePressed(mouse(view, MouseEvent.MOUSE_PRESSED, 20, 20, 1));
    view.getEventManager().mouseDragged(mouse(view, MouseEvent.MOUSE_DRAGGED, 80, 20, 1));
    view.getEventManager().mouseReleased(mouse(view, MouseEvent.MOUSE_RELEASED, 80, 20, 1));

    LineGraphic line = (LineGraphic) view.getGraphicList().getFirst();
    assertEquals(10.0, line.getHandlePoint(0).x, 0.01);
    assertEquals(10.0, line.getHandlePoint(0).y, 0.01);
    assertEquals(40.0, line.getHandlePoint(1).x, 0.01);
    assertEquals(10.0, line.getHandlePoint(1).y, 0.01);
    assertEquals(30.0, line.getLength(), 0.01);
    assertNull(view.getDrawing());

    view.setZoom(1.0);
    assertEquals(10.0, line.getHandlePoint(0).x, 0.01);
    assertEquals(40.0, line.getHandlePoint(1).x, 0.01);
    assertNotNull(view.graphicAt(75, 60));
    assertTrue(paintsYellowOnSegment(view));
  }

  @Test
  void polylineClickDragReleasePaintsLength() {
    DefaultView2d<?> view = sizedGrayView();
    view.setMeasureTool(MeasureTool.POLYLINE);
    view.getMouseActions().setLeft(MouseActions.MEASURE);
    view.getEventManager().mousePressed(mouse(view, MouseEvent.MOUSE_PRESSED, 20, 20, 1));
    view.getEventManager().mouseDragged(mouse(view, MouseEvent.MOUSE_DRAGGED, 80, 20, 1));
    view.getEventManager().mouseReleased(mouse(view, MouseEvent.MOUSE_RELEASED, 80, 20, 1));
    assertTrue(view.getDrawing() instanceof PolylineGraphic);
    PolylineGraphic poly = (PolylineGraphic) view.getDrawing();
    assertTrue(poly.getLabel()[0].contains("px"));
    assertNotNull(poly.getShape());
    view.setZoom(1.0);
    assertTrue(paintsYellowOnSegment(view));
  }

  @Test
  void angleStaysAngleAfterViewerMeasureBind() {
    DefaultView2d<?> view = sizedGrayView();
    MeasureToolBar bar = new MeasureToolBar();
    bar.bind(view);
    bar.setSelected("A");
    bar.apply(view);
    ViewerToolBar.bindMeasureTool(view);
    assertEquals(MeasureTool.ANGLE, view.activeMeasureTool());
    view.getEventManager().mousePressed(mouse(view, MouseEvent.MOUSE_PRESSED, 20, 20, 1));
    view.getEventManager().mouseDragged(mouse(view, MouseEvent.MOUSE_DRAGGED, 20, 80, 1));
    view.getEventManager().mouseReleased(mouse(view, MouseEvent.MOUSE_RELEASED, 20, 80, 1));
    assertFalse(view.getGraphicList().getLast() instanceof LineGraphic);
    assertTrue(view.getGraphicList().getLast() instanceof AngleToolGraphic);
    AngleToolGraphic angle = (AngleToolGraphic) view.getGraphicList().getLast();
    assertTrue(angle.getAngleDegrees() > 1.0);
    assertTrue(angle.getLabel()[0].contains("°"));
  }

  @Test
  void drawGOverExistingLineCreatesRectangle() {
    DefaultView2d<?> view = sizedGrayView();
    MeasureToolBar bar = new MeasureToolBar();
    bar.bind(view);
    view.getEventManager().mousePressed(mouse(view, MouseEvent.MOUSE_PRESSED, 20, 20, 1));
    view.getEventManager().mouseDragged(mouse(view, MouseEvent.MOUSE_DRAGGED, 80, 20, 1));
    view.getEventManager().mouseReleased(mouse(view, MouseEvent.MOUSE_RELEASED, 80, 20, 1));
    bar.setSelected("G");
    bar.apply(view);
    view.getEventManager().mousePressed(mouse(view, MouseEvent.MOUSE_PRESSED, 30, 30, 1));
    view.getEventManager().mouseDragged(mouse(view, MouseEvent.MOUSE_DRAGGED, 90, 90, 1));
    view.getEventManager().mouseReleased(mouse(view, MouseEvent.MOUSE_RELEASED, 90, 90, 1));
    assertEquals(2, view.getGraphicList().size());
    assertTrue(
        view.getGraphicList().getLast()
            instanceof org.weasis.core.ui.model.graphic.imp.area.RectangleGraphic);
  }

  @Test
  void clickAOrGAfterDistanceDoesNotCreateLineGraphic() {
    DefaultView2d<?> view = sizedGrayView();
    MeasureToolBar bar = new MeasureToolBar();
    bar.bind(view);
    drag(view, 20, 20, 80, 20);
    assertTrue(view.getGraphicList().getFirst() instanceof LineGraphic);

    javax.swing.AbstractButton angle = toggle(bar, 1);
    MeasureToolBar.pressRelease(angle);
    assertTrue(angle.isSelected());
    assertEquals(MeasureTool.ANGLE, view.activeMeasureTool());
    ViewerToolBar.bindMeasureTool(view);
    assertEquals(MeasureTool.ANGLE, view.activeMeasureTool());
    drag(view, 20, 20, 20, 80);
    assertEquals(2, view.getGraphicList().size());
    assertFalse(view.getGraphicList().getLast() instanceof LineGraphic);
    assertTrue(view.getGraphicList().getLast() instanceof AngleToolGraphic);
    AngleToolGraphic drawn = (AngleToolGraphic) view.getGraphicList().getLast();
    assertTrue(drawn.getAngleDegrees() > 1.0);
    assertTrue(drawn.getLabel()[0].contains("°"));
    assertTrue(angle.isSelected());

    javax.swing.AbstractButton roi = toggle(bar, 3);
    MeasureToolBar.pressRelease(roi);
    assertTrue(roi.isSelected());
    ViewerToolBar.bindMeasureTool(view);
    assertEquals(MeasureTool.RECTANGLE, view.activeMeasureTool());
    drag(view, 30, 30, 90, 90);
    assertEquals(3, view.getGraphicList().size());
    assertFalse(view.getGraphicList().getLast() instanceof LineGraphic);
    assertTrue(
        view.getGraphicList().getLast()
            instanceof org.weasis.core.ui.model.graphic.imp.area.RectangleGraphic);
    assertTrue(roi.isSelected());
    assertFalse(labelsContain(view, "0.0 px"));
  }

  @Test
  void clickAAfterOpenPolylineDoesNotCreateLineGraphic() {
    DefaultView2d<?> view = sizedGrayView();
    MeasureToolBar bar = new MeasureToolBar();
    bar.bind(view);
    MeasureToolBar.pressRelease(toggle(bar, 2));
    drag(view, 20, 20, 80, 20);
    assertTrue(view.getDrawing() instanceof PolylineGraphic);
    MeasureToolBar.pressRelease(toggle(bar, 1));
    assertTrue(toggle(bar, 1).isSelected());
    drag(view, 20, 40, 20, 90);
    assertFalse(view.getGraphicList().getLast() instanceof LineGraphic);
    assertTrue(view.getGraphicList().getLast() instanceof AngleToolGraphic);
  }

  @Test
  void zeroLengthPressReleaseDoesNotLeaveGhostPxLabel() {
    DefaultView2d<?> view = sizedGrayView();
    MeasureToolBar bar = new MeasureToolBar();
    bar.bind(view);
    view.getEventManager().mousePressed(mouse(view, MouseEvent.MOUSE_PRESSED, 20, 20, 1));
    view.getEventManager().mouseReleased(mouse(view, MouseEvent.MOUSE_RELEASED, 20, 20, 1));
    assertTrue(view.getGraphicList().isEmpty());
    assertFalse(labelsContain(view, "0.0 px"));
  }

  @Test
  void viewPressAfterAKeepsASelectedAndDrawsAngleNotLine() {
    Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());
    DefaultView2d<?> view = sizedGrayView();
    MeasureToolBar bar = new MeasureToolBar();
    bar.bind(view);
    JFrame frame = new JFrame();
    try {
      showWithGlass(frame, bar, view);
      javax.swing.JToggleButton distance = findToggle(bar, "measure-distance");
      javax.swing.JToggleButton angle = findToggle(bar, "measure-angle");
      angle.setSelected(true);
      assertTrue(angle.isSelected());
      assertFalse(distance.isSelected());
      view.dispatchEvent(mouse(view, MouseEvent.MOUSE_PRESSED, 20, 20, 1));
      assertTrue(angle.isSelected());
      assertFalse(distance.isSelected());
      view.dispatchEvent(mouse(view, MouseEvent.MOUSE_DRAGGED, 20, 80, 1));
      view.dispatchEvent(mouse(view, MouseEvent.MOUSE_RELEASED, 20, 80, 1));
      assertTrue(angle.isSelected());
      assertFalse(distance.isSelected());
      assertEquals(MeasureTool.ANGLE, view.activeMeasureTool());
      assertFalse(view.getGraphicList().getLast() instanceof LineGraphic);
      assertTrue(view.getGraphicList().getLast() instanceof AngleToolGraphic);
      AngleToolGraphic drawn = (AngleToolGraphic) view.getGraphicList().getLast();
      assertTrue(drawn.getAngleDegrees() > 1.0);
      assertTrue(drawn.getLabel()[0].contains("°"));
    } finally {
      frame.dispose();
    }
  }

  @Test
  void viewPressAfterGKeepsGSelectedAndDrawsRectangleNotLine() {
    Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());
    DefaultView2d<?> view = sizedGrayView();
    MeasureToolBar bar = new MeasureToolBar();
    bar.bind(view);
    JFrame frame = new JFrame();
    try {
      showWithGlass(frame, bar, view);
      javax.swing.JToggleButton distance = findToggle(bar, "measure-distance");
      javax.swing.JToggleButton rect = findToggle(bar, "measure-rect");
      rect.setSelected(true);
      assertTrue(rect.isSelected());
      assertFalse(distance.isSelected());
      view.dispatchEvent(mouse(view, MouseEvent.MOUSE_PRESSED, 30, 30, 1));
      assertTrue(rect.isSelected());
      assertFalse(distance.isSelected());
      view.dispatchEvent(mouse(view, MouseEvent.MOUSE_DRAGGED, 90, 90, 1));
      view.dispatchEvent(mouse(view, MouseEvent.MOUSE_RELEASED, 90, 90, 1));
      assertTrue(rect.isSelected());
      assertFalse(distance.isSelected());
      assertFalse(view.getGraphicList().getLast() instanceof LineGraphic);
      assertTrue(
          view.getGraphicList().getLast()
              instanceof org.weasis.core.ui.model.graphic.imp.area.RectangleGraphic);
    } finally {
      frame.dispose();
    }
  }

  @Test
  void deleteOnViewClearsGraphicsWhenExplorerHidden() {
    Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());
    DefaultView2d<?> view = sizedGrayView();
    MeasureToolBar bar = new MeasureToolBar();
    bar.bind(view);
    JFrame frame = new JFrame();
    JPanel explorer = new JPanel();
    try {
      JPanel content = new JPanel(new BorderLayout());
      explorer.setName("Explorer");
      explorer.setPreferredSize(new Dimension(120, 200));
      content.add(bar, BorderLayout.NORTH);
      content.add(explorer, BorderLayout.WEST);
      content.add(view, BorderLayout.CENTER);
      frame.setContentPane(content);
      frame.pack();
      frame.setVisible(true);
      drag(view, 20, 20, 80, 20);
      assertEquals(1, view.getGraphicList().size());
      explorer.setVisible(false);
      frame.validate();
      ImageViewerEventManager.DrawStroke.deleteOutside(
          new KeyEvent(view, KeyEvent.KEY_PRESSED, 0L, 0, KeyEvent.VK_DELETE, '\0'));
      assertTrue(view.getGraphicList().isEmpty());
    } finally {
      frame.dispose();
    }
  }

  static void drag(DefaultView2d<?> view, int x0, int y0, int x1, int y1) {
    view.getEventManager().mousePressed(mouse(view, MouseEvent.MOUSE_PRESSED, x0, y0, 1));
    view.getEventManager().mouseDragged(mouse(view, MouseEvent.MOUSE_DRAGGED, x1, y1, 1));
    view.getEventManager().mouseReleased(mouse(view, MouseEvent.MOUSE_RELEASED, x1, y1, 1));
  }

  static javax.swing.AbstractButton toggle(MeasureToolBar bar, int index) {
    return (javax.swing.AbstractButton) bar.getComponent().getComponent(index);
  }

  static boolean labelsContain(DefaultView2d<?> view, String text) {
    for (org.weasis.core.ui.model.graphic.Graphic graphic : view.getGraphicList()) {
      String[] lines = graphic.getLabel();
      if (lines == null) {
        continue;
      }
      for (String line : lines) {
        if (text.equals(line)) {
          return true;
        }
      }
    }
    return false;
  }

  @Test
  void explorerDeleteClearsChestGraphics() {
    DefaultView2d<?> view = sizedGrayView();
    MeasureToolBar bar = new MeasureToolBar();
    bar.bind(view);
    view.getEventManager().mousePressed(mouse(view, MouseEvent.MOUSE_PRESSED, 20, 20, 1));
    view.getEventManager().mouseDragged(mouse(view, MouseEvent.MOUSE_DRAGGED, 80, 20, 1));
    view.getEventManager().mouseReleased(mouse(view, MouseEvent.MOUSE_RELEASED, 80, 20, 1));
    assertEquals(1, view.getGraphicList().size());
    ImageViewerEventManager.DrawStroke.rememberView(view);
    ImageViewerEventManager.DrawStroke.deleteOutside(
        new java.awt.event.KeyEvent(
            new javax.swing.JPanel(),
            java.awt.event.KeyEvent.KEY_PRESSED,
            0L,
            0,
            java.awt.event.KeyEvent.VK_DELETE,
            '\0'));
    assertTrue(view.getGraphicList().isEmpty());
  }

  @Test
  void unselectedAnglePaintsLightBorderBox() {
    MeasureToolBar bar = new MeasureToolBar();
    javax.swing.JToggleButton angle = findToggle(bar, "measure-angle");
    javax.swing.JToggleButton distance = findToggle(bar, "measure-distance");
    assertFalse(angle.isSelected());
    assertTrue(distance.isSelected());
    assertEquals(MeasureToolBar.HIT, angle.getPreferredSize());
    angle.setSize(MeasureToolBar.HIT);
    BufferedImage img =
        new BufferedImage(
            MeasureToolBar.HIT.width, MeasureToolBar.HIT.height, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = img.createGraphics();
    try {
      g.setColor(Color.BLACK);
      g.fillRect(0, 0, img.getWidth(), img.getHeight());
      angle.paint(g);
    } finally {
      g.dispose();
    }
    assertTrue(lightPixel(img, 0, 0));
    assertTrue(lightPixel(img, img.getWidth() - 1, 0));
    assertTrue(lightPixel(img, 0, img.getHeight() - 1));
  }

  @Test
  void mousePressOnAngleCenterSelectsANotDThenDrawsAngle() {
    Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());
    DefaultView2d<?> view = sizedGrayView();
    MeasureToolBar bar = new MeasureToolBar();
    bar.bind(view);
    JFrame frame = new JFrame();
    try {
      JComponent glass = showWithGlass(frame, bar, view);
      javax.swing.JToggleButton distance = findToggle(bar, "measure-distance");
      javax.swing.JToggleButton angle = findToggle(bar, "measure-angle");
      assertTrue(distance.isSelected());
      assertFalse(angle.isSelected());
      clickGlass(glass, angle);
      assertTrue(angle.isSelected());
      assertFalse(distance.isSelected());
      assertEquals(MeasureTool.ANGLE, view.activeMeasureTool());
      drag(view, 20, 20, 20, 80);
      assertFalse(view.getGraphicList().getLast() instanceof LineGraphic);
      assertTrue(view.getGraphicList().getLast() instanceof AngleToolGraphic);
      AngleToolGraphic drawn = (AngleToolGraphic) view.getGraphicList().getLast();
      assertTrue(drawn.getAngleDegrees() > 1.0);
      assertTrue(drawn.getLabel()[0].contains("°"));
    } finally {
      frame.dispose();
    }
  }

  @Test
  void mousePressOnRectCenterSelectsGNotDThenDrawsRectangle() {
    Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());
    DefaultView2d<?> view = sizedGrayView();
    MeasureToolBar bar = new MeasureToolBar();
    bar.bind(view);
    JFrame frame = new JFrame();
    try {
      JComponent glass = showWithGlass(frame, bar, view);
      javax.swing.JToggleButton distance = findToggle(bar, "measure-distance");
      javax.swing.JToggleButton rect = findToggle(bar, "measure-rect");
      clickGlass(glass, rect);
      assertTrue(rect.isSelected());
      assertFalse(distance.isSelected());
      assertEquals(MeasureTool.RECTANGLE, view.activeMeasureTool());
      drag(view, 30, 30, 90, 90);
      assertFalse(view.getGraphicList().getLast() instanceof LineGraphic);
      assertTrue(
          view.getGraphicList().getLast()
              instanceof org.weasis.core.ui.model.graphic.imp.area.RectangleGraphic);
    } finally {
      frame.dispose();
    }
  }

  @Test
  void bindDrawSelectsRectAndKeepsItOnViewPress() {
    DefaultView2d<?> view = sizedGrayView();
    MeasureToolBar bar = new MeasureToolBar();
    bar.bind(view);
    view.getMouseActions().setLeft(MouseActions.DRAW);
    ViewerToolBar.bindMeasureTool(view);
    assertEquals(MeasureTool.RECTANGLE, view.activeMeasureTool());
    assertTrue(findToggle(bar, "measure-rect").isSelected());
    assertFalse(findToggle(bar, "measure-distance").isSelected());
    drag(view, 30, 30, 90, 90);
    assertFalse(view.getGraphicList().getLast() instanceof LineGraphic);
    assertTrue(
        view.getGraphicList().getLast()
            instanceof org.weasis.core.ui.model.graphic.imp.area.RectangleGraphic);
  }

  @Test
  void bindMeasureDoesNotOverwriteAngleOrRect() {
    DefaultView2d<?> view = sizedGrayView();
    MeasureToolBar bar = new MeasureToolBar();
    bar.bind(view);
    bar.setSelected("A");
    bar.apply(view);
    view.getMouseActions().setLeft(MouseActions.MEASURE);
    ViewerToolBar.bindMeasureTool(view);
    assertEquals(MeasureTool.ANGLE, view.activeMeasureTool());
    assertTrue(findToggle(bar, "measure-angle").isSelected());
    assertFalse(findToggle(bar, "measure-distance").isSelected());
    bar.setSelected("G");
    bar.apply(view);
    view.getMouseActions().setLeft(MouseActions.MEASURE);
    ViewerToolBar.bindMeasureTool(view);
    assertEquals(MeasureTool.RECTANGLE, view.activeMeasureTool());
    assertTrue(findToggle(bar, "measure-rect").isSelected());
    assertFalse(findToggle(bar, "measure-distance").isSelected());
  }

  @Test
  void pressBelowDistanceDoesNotReselectDWhenYSelected() {
    Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());
    DefaultView2d<?> view = sizedGrayView();
    MeasureToolBar bar = new MeasureToolBar();
    bar.bind(view);
    JFrame frame = new JFrame();
    try {
      showWithGlass(frame, bar, view);
      javax.swing.JToggleButton distance = findToggle(bar, "measure-distance");
      javax.swing.JToggleButton poly = findToggle(bar, "measure-polyline");
      MeasureToolBar.pressRelease(poly);
      assertTrue(poly.isSelected());
      assertFalse(distance.isSelected());
      Point screen = distance.getLocationOnScreen();
      screen.translate(Math.max(1, distance.getWidth() / 2), distance.getHeight() + 18);
      Point local = new Point(screen);
      javax.swing.SwingUtilities.convertPointFromScreen(local, bar);
      MouseEvent below =
          new MouseEvent(
              bar,
              MouseEvent.MOUSE_PRESSED,
              0L,
              MouseEvent.BUTTON1_DOWN_MASK,
              local.x,
              local.y,
              screen.x,
              screen.y,
              1,
              false,
              MouseEvent.BUTTON1);
      assertFalse(MeasureToolBar.armAt(below));
      assertTrue(poly.isSelected());
      assertFalse(distance.isSelected());
      assertEquals(MeasureTool.POLYLINE, view.activeMeasureTool());
    } finally {
      frame.dispose();
    }
  }

  static JComponent showWithGlass(JFrame frame, MeasureToolBar bar, DefaultView2d<?> view) {
    JPanel content = new JPanel(new BorderLayout());
    content.add(bar, BorderLayout.NORTH);
    content.add(view, BorderLayout.CENTER);
    frame.setContentPane(content);
    JPanel glass = new JPanel(null);
    glass.setOpaque(false);
    glass.setName("measure-glass");
    frame.setGlassPane(glass);
    MeasureToolBar.installGlass(glass);
    frame.pack();
    frame.setVisible(true);
    glass.setVisible(true);
    glass.setSize(frame.getRootPane().getSize());
    return glass;
  }

  static void clickGlass(JComponent glass, javax.swing.JToggleButton button) {
    Point onGlass = pointOnGlass(glass, button);
    pressOn(glass, onGlass);
  }

  static Point pointOnGlass(JComponent glass, javax.swing.JToggleButton button) {
    Point screen = button.getLocationOnScreen();
    screen.translate(Math.max(1, button.getWidth() / 2), Math.max(1, button.getHeight() / 2));
    Point onGlass = new Point(screen);
    javax.swing.SwingUtilities.convertPointFromScreen(onGlass, glass);
    return onGlass;
  }

  static void pressOn(JComponent glass, Point p) {
    Point screen = new Point(p);
    javax.swing.SwingUtilities.convertPointToScreen(screen, glass);
    glass.dispatchEvent(mouseOn(glass, MouseEvent.MOUSE_PRESSED, p, screen, true));
    glass.dispatchEvent(mouseOn(glass, MouseEvent.MOUSE_RELEASED, p, screen, false));
    glass.dispatchEvent(mouseOn(glass, MouseEvent.MOUSE_CLICKED, p, screen, false));
  }

  static MouseEvent mouseOn(JComponent glass, int id, Point local, Point screen, boolean down) {
    int mods = down ? MouseEvent.BUTTON1_DOWN_MASK : 0;
    return new MouseEvent(
        glass, id, 0L, mods, local.x, local.y, screen.x, screen.y, 1, false, MouseEvent.BUTTON1);
  }

  static boolean lightPixel(BufferedImage img, int x, int y) {
    int rgb = img.getRGB(x, y);
    int r = (rgb >> 16) & 255;
    int green = (rgb >> 8) & 255;
    int b = rgb & 255;
    return r > 140 && green > 140 && b > 140;
  }

  static javax.swing.JToggleButton findToggle(java.awt.Container root, String name) {
    javax.swing.JToggleButton found = lookupToggle(root, name);
    if (found == null) {
      throw new AssertionError("missing toggle " + name);
    }
    return found;
  }

  static javax.swing.JToggleButton lookupToggle(java.awt.Container root, String name) {
    for (java.awt.Component c : root.getComponents()) {
      if (c instanceof javax.swing.JToggleButton toggle && name.equals(toggle.getName())) {
        return toggle;
      }
      if (c instanceof java.awt.Container nested && !(c instanceof javax.swing.AbstractButton)) {
        javax.swing.JToggleButton hit = lookupToggle(nested, name);
        if (hit != null) {
          return hit;
        }
      }
    }
    return null;
  }

  static DefaultView2d<?> sizedGrayView() {
    DefaultView2d<?> view = new DefaultView2d<>();
    view.setSize(200, 200);
    view.setSourceImage(new BufferedImage(100, 100, BufferedImage.TYPE_BYTE_GRAY));
    view.setZoom(2.0);
    return view;
  }

  static boolean paintsYellowOnSegment(DefaultView2d<?> view) {
    BufferedImage page = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = page.createGraphics();
    try {
      view.paintView(g, true);
    } finally {
      g.dispose();
    }
    return yellowAt(page, 60, 60) && yellowAt(page, 75, 60) && yellowAt(page, 90, 60);
  }

  static boolean yellowAt(BufferedImage page, int x, int y) {
    for (int dy = -1; dy <= 1; dy++) {
      for (int dx = -1; dx <= 1; dx++) {
        if (isYellow(page, x + dx, y + dy)) {
          return true;
        }
      }
    }
    return false;
  }

  static boolean isYellow(BufferedImage page, int x, int y) {
    if (x < 0 || y < 0 || x >= page.getWidth() || y >= page.getHeight()) {
      return false;
    }
    int rgb = page.getRGB(x, y);
    int r = (rgb >> 16) & 255;
    int green = (rgb >> 8) & 255;
    int b = rgb & 255;
    return r > 200 && green > 200 && b < 80;
  }

  static MouseEvent mouse(DefaultView2d<?> view, int id, int x, int y, int clicks) {
    int mods = id == MouseEvent.MOUSE_RELEASED ? 0 : InputEvent.BUTTON1_DOWN_MASK;
    return new MouseEvent(view, id, 0L, mods, x, y, clicks, false, MouseEvent.BUTTON1);
  }
}
