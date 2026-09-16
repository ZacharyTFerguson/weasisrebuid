/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.service.UICore;
import org.weasis.core.ui.editor.image.DefaultView2d;
import org.weasis.core.ui.editor.image.ImageViewerEventManager;
import org.weasis.core.ui.editor.image.MeasureToolBar;
import org.weasis.core.ui.editor.image.dockable.MeasureTool;
import org.weasis.core.ui.model.graphic.imp.angle.AngleToolGraphic;
import org.weasis.core.ui.model.graphic.imp.area.RectangleGraphic;
import org.weasis.core.ui.model.graphic.imp.line.LineGraphic;

class View2dGridMeasureHaveTest {

  @Test
  void glassAAndGDrawOnBottomLeftChestNotEmptyBrThenDeleteClearsAll() {
    Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());
    View2dContainer container = new View2dContainer();
    container.setLayoutCount(4);
    View2d bl = container.getLayoutViews().get(2);
    View2d br = container.getLayoutViews().get(3);
    chest(bl);
    JFrame frame = new JFrame();
    UICore core = UICore.getInstance();
    var previous = core.getSelectedViewerPlugin();
    core.setSelectedViewerPlugin(container);
    try {
      JComponent glass = showGrid(frame, container);
      MeasureToolBar bar = container.getMeasureToolBar();
      JToggleButton distance = bar.toggle("D");
      JToggleButton angle = bar.toggle("A");
      JToggleButton rect = bar.toggle("G");
      clickGlass(glass, angle);
      assertTrue(angle.isSelected());
      assertFalse(distance.isSelected());
      assertEquals(MeasureTool.ANGLE, bl.activeMeasureTool());
      dragGlass(glass, bl, 20, 20, 20, 80);
      assertEquals(1, bl.getGraphicList().size());
      assertFalse(bl.getGraphicList().getLast() instanceof LineGraphic);
      assertTrue(bl.getGraphicList().getLast() instanceof AngleToolGraphic);
      AngleToolGraphic drawn = (AngleToolGraphic) bl.getGraphicList().getLast();
      assertTrue(drawn.getAngleDegrees() > 1.0);
      assertTrue(drawn.getLabel()[0].contains("°"));
      assertTrue(br.getGraphicList().isEmpty());
      clickGlass(glass, rect);
      assertTrue(rect.isSelected());
      assertFalse(distance.isSelected());
      dragGlass(glass, bl, 30, 30, 90, 90);
      assertEquals(2, bl.getGraphicList().size());
      assertFalse(bl.getGraphicList().getLast() instanceof LineGraphic);
      assertTrue(bl.getGraphicList().getLast() instanceof RectangleGraphic);
      assertTrue(br.getGraphicList().isEmpty());
      ImageViewerEventManager.DrawStroke.rememberView(bl);
      ImageViewerEventManager.DrawStroke.deleteOutside(
          new KeyEvent(new JPanel(), KeyEvent.KEY_PRESSED, 0L, 0, KeyEvent.VK_DELETE, '\0'));
      for (View2d cell : container.getLayoutViews()) {
        assertTrue(cell.getGraphicList().isEmpty());
      }
    } finally {
      core.setSelectedViewerPlugin(previous);
      frame.dispose();
    }
  }

  @Test
  void distanceThenExplorerReflowThenLiveAngleOnBlNotEmptyBrThenDelete() {
    Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());
    View2dContainer container = new View2dContainer();
    container.setLayoutCount(4);
    View2d bl = container.getLayoutViews().get(2);
    View2d br = container.getLayoutViews().get(3);
    chest(bl);
    JFrame frame = new JFrame();
    UICore core = UICore.getInstance();
    var previous = core.getSelectedViewerPlugin();
    core.setSelectedViewerPlugin(container);
    try {
      JComponent glass = showGrid(frame, container);
      MeasureToolBar bar = container.getMeasureToolBar();
      JToggleButton distance = bar.toggle("D");
      JToggleButton angle = bar.toggle("A");
      clickGlass(glass, distance);
      dragGlass(glass, bl, 20, 20, 80, 20);
      assertEquals(1, bl.getGraphicList().size());
      assertTrue(bl.getGraphicList().getLast() instanceof LineGraphic);
      assertTrue(br.getGraphicList().isEmpty());
      reflowExplorerHide(frame, container);
      clickGlass(glass, angle);
      assertTrue(angle.isSelected());
      assertFalse(distance.isSelected());
      assertEquals(MeasureTool.ANGLE, bl.activeMeasureTool());
      dragGlass(glass, bl, 20, 20, 20, 80);
      assertEquals(2, bl.getGraphicList().size());
      assertFalse(bl.getGraphicList().getLast() instanceof LineGraphic);
      assertTrue(bl.getGraphicList().getLast() instanceof AngleToolGraphic);
      assertTrue(br.getGraphicList().isEmpty());
      LineGraphic stray = new LineGraphic();
      stray.setHandlePoint(0, new Point2D.Double(0, 0));
      stray.setHandlePoint(1, new Point2D.Double(4, 0));
      br.addGraphic(stray);
      ImageViewerEventManager.DrawStroke.rememberView(bl);
      ImageViewerEventManager.DrawStroke.deleteOutside(
          new KeyEvent(new JPanel(), KeyEvent.KEY_PRESSED, 0L, 0, KeyEvent.VK_DELETE, '\0'));
      for (View2d cell : container.getLayoutViews()) {
        assertTrue(cell.getGraphicList().isEmpty());
      }
    } finally {
      core.setSelectedViewerPlugin(previous);
      frame.dispose();
    }
  }

  @Test
  void selectAThenViewPressKeepsAAndAngleThenHiddenExplorerDeleteClears() {
    Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());
    View2dContainer container = new View2dContainer();
    container.setLayoutCount(4);
    View2d bl = container.getLayoutViews().get(2);
    View2d br = container.getLayoutViews().get(3);
    chest(bl);
    JFrame frame = new JFrame();
    JPanel explorer = new JPanel();
    explorer.setName("Explorer");
    explorer.setPreferredSize(new Dimension(160, 400));
    UICore core = UICore.getInstance();
    var previous = core.getSelectedViewerPlugin();
    core.setSelectedViewerPlugin(container);
    try {
      showGridWithExplorer(frame, container, explorer);
      MeasureToolBar bar = container.getMeasureToolBar();
      JToggleButton distance = bar.toggle("D");
      JToggleButton angle = bar.toggle("A");
      JToggleButton rect = bar.toggle("G");
      angle.setSelected(true);
      bl.dispatchEvent(mouseOn(bl, MouseEvent.MOUSE_PRESSED, new Point(20, 20), true));
      assertTrue(angle.isSelected());
      assertFalse(distance.isSelected());
      bl.dispatchEvent(mouseOn(bl, MouseEvent.MOUSE_DRAGGED, new Point(20, 80), true));
      bl.dispatchEvent(mouseOn(bl, MouseEvent.MOUSE_RELEASED, new Point(20, 80), false));
      assertTrue(bl.getGraphicList().getLast() instanceof AngleToolGraphic);
      assertTrue(br.getGraphicList().isEmpty());
      rect.setSelected(true);
      bl.dispatchEvent(mouseOn(bl, MouseEvent.MOUSE_PRESSED, new Point(30, 30), true));
      assertTrue(rect.isSelected());
      assertFalse(distance.isSelected());
      bl.dispatchEvent(mouseOn(bl, MouseEvent.MOUSE_DRAGGED, new Point(90, 90), true));
      bl.dispatchEvent(mouseOn(bl, MouseEvent.MOUSE_RELEASED, new Point(90, 90), false));
      assertTrue(bl.getGraphicList().getLast() instanceof RectangleGraphic);
      explorer.setVisible(false);
      frame.validate();
      ImageViewerEventManager.DrawStroke.rememberView(bl);
      ImageViewerEventManager.DrawStroke.deleteOutside(
          new KeyEvent(bl, KeyEvent.KEY_PRESSED, 0L, 0, KeyEvent.VK_DELETE, '\0'));
      for (View2d cell : container.getLayoutViews()) {
        assertTrue(cell.getGraphicList().isEmpty());
      }
    } finally {
      core.setSelectedViewerPlugin(previous);
      frame.dispose();
    }
  }

  static JComponent showGridWithExplorer(JFrame frame, View2dContainer container, JPanel explorer) {
    container.setPreferredSize(new Dimension(400, 400));
    JPanel content = new JPanel(new BorderLayout());
    content.add(container.getMeasureToolBar(), BorderLayout.NORTH);
    content.add(explorer, BorderLayout.WEST);
    content.add(container, BorderLayout.CENTER);
    frame.setContentPane(content);
    JPanel glass = new JPanel(null);
    glass.setOpaque(false);
    glass.setName("measure-glass");
    frame.setGlassPane(glass);
    MeasureToolBar.installGlass(glass);
    frame.pack();
    frame.setSize(640, 520);
    frame.setVisible(true);
    glass.setVisible(true);
    glass.setSize(frame.getRootPane().getSize());
    container.doLayout();
    container.getViewGrid().doLayout();
    return glass;
  }

  static void reflowExplorerHide(JFrame frame, View2dContainer container) {
    frame.setSize(900, 720);
    container.setSize(880, 640);
    frame.validate();
    container.revalidate();
    container.doLayout();
    container.getViewGrid().doLayout();
    DefaultView2d.dropBoundsCache();
  }

  static void chest(View2d view) {
    view.setSize(200, 200);
    view.setSourceImage(new BufferedImage(100, 100, BufferedImage.TYPE_BYTE_GRAY));
    view.setZoom(2.0);
  }

  static JComponent showGrid(JFrame frame, View2dContainer container) {
    container.setPreferredSize(new Dimension(400, 400));
    JPanel content = new JPanel(new BorderLayout());
    content.add(container.getMeasureToolBar(), BorderLayout.NORTH);
    content.add(container, BorderLayout.CENTER);
    frame.setContentPane(content);
    JPanel glass = new JPanel(null);
    glass.setOpaque(false);
    glass.setName("measure-glass");
    frame.setGlassPane(glass);
    MeasureToolBar.installGlass(glass);
    frame.pack();
    frame.setSize(480, 520);
    frame.setVisible(true);
    glass.setVisible(true);
    glass.setSize(frame.getRootPane().getSize());
    container.doLayout();
    container.getViewGrid().doLayout();
    return glass;
  }

  static void clickGlass(JComponent glass, JToggleButton button) {
    Point screen = button.getLocationOnScreen();
    int dx = Math.max(1, button.getWidth() / 2);
    int dy = Math.max(1, button.getHeight() / 2);
    Point onGlass = pointOnGlass(glass, screen, dx, dy);
    glass.dispatchEvent(mouseOn(glass, MouseEvent.MOUSE_PRESSED, onGlass, true));
    glass.dispatchEvent(mouseOn(glass, MouseEvent.MOUSE_RELEASED, onGlass, false));
    glass.dispatchEvent(mouseOn(glass, MouseEvent.MOUSE_CLICKED, onGlass, false));
  }

  static void dragGlass(JComponent glass, View2d view, int x0, int y0, int x1, int y1) {
    Point a = pointOnGlass(glass, view.getLocationOnScreen(), x0, y0);
    Point b = pointOnGlass(glass, view.getLocationOnScreen(), x1, y1);
    glass.dispatchEvent(mouseOn(glass, MouseEvent.MOUSE_PRESSED, a, true));
    glass.dispatchEvent(mouseOn(glass, MouseEvent.MOUSE_DRAGGED, b, true));
    glass.dispatchEvent(mouseOn(glass, MouseEvent.MOUSE_RELEASED, b, false));
  }

  static Point pointOnGlass(JComponent glass, Point screen, int dx, int dy) {
    Point p = new Point(screen.x + Math.max(1, dx), screen.y + Math.max(1, dy));
    SwingUtilities.convertPointFromScreen(p, glass);
    return p;
  }

  static MouseEvent mouseOn(JComponent glass, int id, Point local, boolean down) {
    Point screen = new Point(local);
    SwingUtilities.convertPointToScreen(screen, glass);
    int mods = down ? MouseEvent.BUTTON1_DOWN_MASK : 0;
    return new MouseEvent(
        glass, id, 0L, mods, local.x, local.y, screen.x, screen.y, 1, false, MouseEvent.BUTTON1);
  }
}
