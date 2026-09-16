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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.GridLayout;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.gui.Insertable;
import org.weasis.core.api.image.PseudoColorOp;
import org.weasis.core.api.service.UICore;
import org.weasis.core.ui.model.graphic.imp.line.LineGraphic;
import org.weasis.dicom.viewer2d.mpr.MprContainer;

class ViewerChromeHaveTest {

  @Test
  void resetToolsApplyCommandTokens() {
    View2d view = new View2d();
    view.setZoom(2.0);
    view.setPan(4, 5);
    view.setRotation(90);
    view.setFlip(true);
    ResetTools bar = new ResetTools();
    assertEquals("Reset", bar.getComponentName());
    assertEquals(Insertable.Type.TOOLBAR, bar.getType());
    bar.bind(view);
    bar.apply(org.weasis.core.ui.editor.image.ResetTools.ZOOM);
    assertEquals(-200.0, view.getZoom(), 1e-9);
    assertEquals(4.0, view.getPanX(), 1e-9);
    bar.apply(org.weasis.core.ui.editor.image.ResetTools.ALL);
    assertEquals(0.0, view.getPanX(), 1e-9);
    assertEquals(0.0, view.getRotation(), 1e-9);
    assertFalse(view.isFlip());
  }

  @Test
  void lutToolBarSetsPseudoColorAndInvert() {
    View2d view = new View2d();
    LutToolBar bar = new LutToolBar();
    assertEquals("LUT", bar.getComponentName());
    bar.bind(view);
    assertEquals(PseudoColorOp.GRAY, view.getLut());
    bar.setLut("HotIron");
    assertEquals("HotIron", view.getLut());
    bar.toggleInvert();
    assertTrue(view.isInverseLut());
    bar.toggleInvert();
    assertFalse(view.isInverseLut());
  }

  @Test
  void basic3DToolBarOpensMprContainer() {
    UICore core = new UICore();
    Basic3DToolBar bar = new Basic3DToolBar();
    assertEquals("Basic 3D", bar.getComponentName());
    assertEquals(Insertable.Type.TOOLBAR, bar.getType());
    MprContainer opened = bar.openMpr(core);
    assertInstanceOf(MprContainer.class, opened);
    assertSame(opened, core.getSelectedViewerPlugin());
  }

  @Test
  void view2dContainerExposesViewerLutResetAndCineToolbars() {
    View2dContainer container = new View2dContainer();
    assertTrue(container.getToolBars().getComponentCount() >= 8);
    assertEquals("Viewer", container.getViewerToolBar().getComponentName());
    assertEquals("LUT", container.getLutToolBar().getComponentName());
    assertEquals(Insertable.Type.TOOLBAR, container.getViewerToolBar().getType());
    assertSame(container.getView2d(), container.getViewerToolBar().boundView());
    assertSame(container.getView2d(), container.getLutToolBar().boundView());
    assertTrue(
        container.getSeriesViewerUI().getToolBar().stream()
            .anyMatch(b -> "LUT".equals(b.getComponentName())));
    assertTrue(
        container.getSeriesViewerUI().getToolBar().stream()
            .anyMatch(b -> "Key Object".equals(b.getComponentName())));
    javax.swing.AbstractButton star =
        (javax.swing.AbstractButton) container.getKeyObjectToolBar().getComponent().getComponent(0);
    javax.swing.AbstractButton filter =
        (javax.swing.AbstractButton) container.getKeyObjectToolBar().getComponent().getComponent(1);
    assertEquals("Star", star.getText());
    assertEquals("Filter", filter.getText());
    assertSame(container.getView2d(), container.getKeyObjectToolBar().boundView());
    assertTrue(
        container.getSeriesViewerUI().getToolBar().stream()
            .anyMatch(b -> "Measure".equals(b.getComponentName())));
    javax.swing.AbstractButton distance =
        (javax.swing.AbstractButton) container.getMeasureToolBar().getComponent().getComponent(0);
    javax.swing.AbstractButton angle =
        (javax.swing.AbstractButton) container.getMeasureToolBar().getComponent().getComponent(1);
    javax.swing.AbstractButton polyline =
        (javax.swing.AbstractButton) container.getMeasureToolBar().getComponent().getComponent(2);
    assertEquals("D", distance.getText());
    assertEquals("A", angle.getText());
    assertEquals("Y", polyline.getText());
    assertEquals(
        "G",
        ((javax.swing.AbstractButton) container.getMeasureToolBar().getComponent().getComponent(3))
            .getText());
    assertEquals(
        "B",
        ((javax.swing.AbstractButton) container.getMeasureToolBar().getComponent().getComponent(4))
            .getText());
    assertSame(container.getView2d(), container.getMeasureToolBar().boundView());
    assertEquals(
        "Key Object", container.getSeriesViewerUI().getToolBar().get(1).getComponentName());
    assertEquals("Measure", container.getSeriesViewerUI().getToolBar().get(2).getComponentName());
  }

  @Test
  void measureDayButtonsDrawAndPaintLengthOnView2d() {
    View2dContainer container = new View2dContainer();
    View2d view = container.getView2d();
    view.setSize(200, 200);
    view.setSourceImage(new BufferedImage(100, 100, BufferedImage.TYPE_BYTE_GRAY));
    view.setZoom(2.0);
    javax.swing.AbstractButton d =
        (javax.swing.AbstractButton) container.getMeasureToolBar().getComponent().getComponent(0);
    org.weasis.core.ui.editor.image.MeasureToolBar.pressRelease(d);
    assertEquals("D", container.getMeasureToolBar().getSelected());
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_PRESSED, 20, 20));
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_DRAGGED, 80, 20));
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_RELEASED, 80, 20));
    assertEquals(1, view.getGraphicList().size());
    assertTrue(view.getGraphicList().getFirst() instanceof LineGraphic);
    LineGraphic line = (LineGraphic) view.getGraphicList().getFirst();
    assertEquals(30.0, line.getLength(), 0.01);
    assertTrue(line.getLabel()[0].contains("px"));
    BufferedImage page = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
    java.awt.Graphics2D g = page.createGraphics();
    try {
      view.paint(g);
    } finally {
      g.dispose();
    }
    assertTrue(yellowStrokeOnChest(page));
  }

  @Test
  void mouseLeftActionMeasurePaintsOnLayoutExtra() {
    View2dContainer container = new View2dContainer();
    container.setLayoutCount(2);
    View2d extra = container.getLayoutViews().get(1);
    extra.setSize(200, 200);
    extra.setSourceImage(new BufferedImage(100, 100, BufferedImage.TYPE_BYTE_GRAY));
    extra.setZoom(2.0);
    DicomView2dCommands cmd = new DicomView2dCommands(container.getView2d());
    assertEquals(
        org.weasis.core.ui.editor.image.MouseActions.MEASURE, cmd.mouseLeftAction("measure"));
    assertEquals(
        org.weasis.core.ui.editor.image.MouseActions.MEASURE, extra.getMouseActions().getLeft());
    extra.dispatchEvent(mouse(extra, MouseEvent.MOUSE_PRESSED, 20, 20));
    extra.dispatchEvent(mouse(extra, MouseEvent.MOUSE_DRAGGED, 80, 20));
    extra.dispatchEvent(mouse(extra, MouseEvent.MOUSE_RELEASED, 80, 20));
    assertEquals(1, extra.getGraphicList().size());
    assertTrue(extra.getGraphicList().getFirst() instanceof LineGraphic);
    BufferedImage page = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
    java.awt.Graphics2D g = page.createGraphics();
    try {
      extra.paint(g);
    } finally {
      g.dispose();
    }
    assertTrue(yellowStrokeOnChest(page));
  }

  @Test
  void mouseLeftActionDrawSelectsRectNotDistance() {
    View2dContainer container = new View2dContainer();
    View2d view = container.getView2d();
    DicomView2dCommands cmd = new DicomView2dCommands(view);
    javax.swing.AbstractButton distance =
        (javax.swing.AbstractButton) container.getMeasureToolBar().getComponent().getComponent(0);
    javax.swing.AbstractButton rect =
        (javax.swing.AbstractButton) container.getMeasureToolBar().getComponent().getComponent(3);
    assertTrue(distance.isSelected());
    assertEquals(org.weasis.core.ui.editor.image.MouseActions.DRAW, cmd.mouseLeftAction("draw"));
    assertTrue(rect.isSelected());
    assertFalse(distance.isSelected());
    assertEquals(
        org.weasis.core.ui.editor.image.dockable.MeasureTool.RECTANGLE, view.activeMeasureTool());
    assertEquals(
        org.weasis.core.ui.editor.image.MouseActions.DRAW, view.getMouseActions().getLeft());
    for (View2d cell : container.getLayoutViews()) {
      assertEquals(
          org.weasis.core.ui.editor.image.dockable.MeasureTool.RECTANGLE, cell.activeMeasureTool());
    }
  }

  @Test
  void mouseLeftActionMeasureKeepsAngleNotDistance() {
    View2dContainer container = chestContainer();
    View2d view = container.getView2d();
    clickMeasure(container, 1);
    assertEquals(
        org.weasis.core.ui.editor.image.dockable.MeasureTool.ANGLE, view.activeMeasureTool());
    DicomView2dCommands cmd = new DicomView2dCommands(view);
    assertEquals(
        org.weasis.core.ui.editor.image.MouseActions.MEASURE, cmd.mouseLeftAction("measure"));
    assertEquals(
        org.weasis.core.ui.editor.image.dockable.MeasureTool.ANGLE, view.activeMeasureTool());
    assertTrue(
        ((javax.swing.AbstractButton) container.getMeasureToolBar().getComponent().getComponent(1))
            .isSelected());
    assertFalse(
        ((javax.swing.AbstractButton) container.getMeasureToolBar().getComponent().getComponent(0))
            .isSelected());
  }

  @Test
  void distancePaintsYellowOnBestFitChestRaster() {
    View2dContainer container = new View2dContainer();
    View2d view = container.getView2d();
    view.setSize(400, 400);
    view.setSourceImage(new BufferedImage(2000, 2000, BufferedImage.TYPE_BYTE_GRAY));
    javax.swing.AbstractButton d =
        (javax.swing.AbstractButton) container.getMeasureToolBar().getComponent().getComponent(0);
    org.weasis.core.ui.editor.image.MeasureToolBar.pressRelease(d);
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_PRESSED, 40, 40));
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_DRAGGED, 240, 40));
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_RELEASED, 240, 40));
    assertEquals(1, view.getGraphicList().size());
    assertTrue(((LineGraphic) view.getGraphicList().getFirst()).getLength() > 1.0);
    assertTrue(view.getGraphicList().getFirst().getLabel()[0].contains("px"));
    BufferedImage page = new BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB);
    java.awt.Graphics2D g = page.createGraphics();
    try {
      view.paint(g);
    } finally {
      g.dispose();
    }
    assertTrue(yellowStrokeOnChest(page));
  }

  @Test
  void angleClickDragReleasePaintsDegreesOnChest() {
    View2dContainer container = chestContainer();
    View2d view = container.getView2d();
    clickMeasure(container, 1);
    assertTrue(
        ((javax.swing.AbstractButton) container.getMeasureToolBar().getComponent().getComponent(1))
            .isSelected());
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_PRESSED, 40, 40));
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_DRAGGED, 40, 160));
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_RELEASED, 40, 160));
    assertEquals(1, view.getGraphicList().size());
    assertFalse(view.getGraphicList().getFirst() instanceof LineGraphic);
    org.weasis.core.ui.model.graphic.imp.angle.AngleToolGraphic angle =
        (org.weasis.core.ui.model.graphic.imp.angle.AngleToolGraphic)
            view.getGraphicList().getFirst();
    assertTrue(angle.getAngleDegrees() > 1.0);
    assertTrue(angle.getLabel()[0].contains("°"));
    assertTrue(yellowStrokeOnChest(paint(view)));
  }

  @Test
  void polylineClickDragPaintsLengthOnChest() {
    View2dContainer container = chestContainer();
    View2d view = container.getView2d();
    clickMeasure(container, 2);
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_PRESSED, 40, 40));
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_DRAGGED, 240, 40));
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_RELEASED, 240, 40));
    assertEquals(1, view.getGraphicList().size());
    org.weasis.core.ui.model.graphic.imp.line.PolylineGraphic poly =
        (org.weasis.core.ui.model.graphic.imp.line.PolylineGraphic)
            view.getGraphicList().getFirst();
    assertTrue(poly.getLabel()[0].contains("px"));
    assertTrue(yellowStrokeOnChest(paint(view)));
  }

  @Test
  void rectangleGPaintsClosedRoiAndStatsOnChest() {
    View2dContainer container = chestContainer();
    View2d view = container.getView2d();
    clickMeasure(container, 3);
    assertTrue(
        ((javax.swing.AbstractButton) container.getMeasureToolBar().getComponent().getComponent(3))
            .isSelected());
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_PRESSED, 40, 40));
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_DRAGGED, 200, 160));
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_RELEASED, 200, 160));
    assertEquals(1, view.getGraphicList().size());
    assertFalse(view.getGraphicList().getFirst() instanceof LineGraphic);
    assertTrue(
        view.getGraphicList().getFirst()
            instanceof org.weasis.core.ui.model.graphic.imp.area.RectangleGraphic);
    org.weasis.core.ui.model.graphic.imp.area.RectangleGraphic roi =
        (org.weasis.core.ui.model.graphic.imp.area.RectangleGraphic)
            view.getGraphicList().getFirst();
    assertNotNull(roi.getShape());
    assertTrue(
        org.weasis.core.ui.editor.image.ImageRegionStatistics.compute(
                    view.getSourceImage(), roi.getShape(), 1.0, 0.0)
                .getSamples()
            > 0);
    BufferedImage page = paint(view);
    assertTrue(yellowStrokeOnChest(page));
  }

  @Test
  void clickAAndGAfterDistanceMustNotCreateLineGraphic() {
    View2dContainer container = chestContainer();
    View2d view = container.getView2d();
    clickMeasure(container, 0);
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_PRESSED, 40, 40));
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_DRAGGED, 240, 40));
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_RELEASED, 240, 40));
    assertTrue(view.getGraphicList().getFirst() instanceof LineGraphic);
    clickMeasure(container, 1);
    assertTrue(
        ((javax.swing.AbstractButton) container.getMeasureToolBar().getComponent().getComponent(1))
            .isSelected());
    ((javax.swing.AbstractButton) container.getViewerToolBar().getComponent(6)).doClick();
    assertEquals(
        org.weasis.core.ui.editor.image.dockable.MeasureTool.ANGLE, view.activeMeasureTool());
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_PRESSED, 40, 80));
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_DRAGGED, 40, 200));
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_RELEASED, 40, 200));
    assertFalse(view.getGraphicList().getLast() instanceof LineGraphic);
    assertTrue(
        view.getGraphicList().getLast()
            instanceof org.weasis.core.ui.model.graphic.imp.angle.AngleToolGraphic);
    clickMeasure(container, 3);
    assertTrue(
        ((javax.swing.AbstractButton) container.getMeasureToolBar().getComponent().getComponent(3))
            .isSelected());
    ((javax.swing.AbstractButton) container.getViewerToolBar().getComponent(6)).doClick();
    assertEquals(
        org.weasis.core.ui.editor.image.dockable.MeasureTool.RECTANGLE, view.activeMeasureTool());
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_PRESSED, 60, 60));
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_DRAGGED, 180, 160));
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_RELEASED, 180, 160));
    assertFalse(view.getGraphicList().getLast() instanceof LineGraphic);
    assertTrue(
        view.getGraphicList().getLast()
            instanceof org.weasis.core.ui.model.graphic.imp.area.RectangleGraphic);
  }

  @Test
  void selectThenDeleteRemovesPaintedGraphicFromChest() {
    View2dContainer container = chestContainer();
    View2d view = container.getView2d();
    clickMeasure(container, 0);
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_PRESSED, 40, 40));
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_DRAGGED, 240, 40));
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_RELEASED, 240, 40));
    view.getMouseActions().setLeft(org.weasis.core.ui.editor.image.MouseActions.NONE);
    view.dispatchEvent(mouse(view, MouseEvent.MOUSE_PRESSED, 120, 40));
    assertEquals(1, view.getSelectedGraphics().size());
    BufferedImage selected = paint(view);
    assertTrue(yellowStrokeOnChest(selected));
    assertTrue(whiteHandleOnChest(selected));
    javax.swing.JPanel explorer = new javax.swing.JPanel();
    org.weasis.core.ui.editor.image.ImageViewerEventManager.DrawStroke.rememberView(view);
    org.weasis.core.ui.editor.image.ImageViewerEventManager.DrawStroke.deleteOutside(
        new KeyEvent(explorer, KeyEvent.KEY_PRESSED, 0L, 0, KeyEvent.VK_DELETE, '\0'));
    assertTrue(view.getGraphicList().isEmpty());
    assertFalse(yellowStrokeOnChest(paint(view)));
    assertFalse(whiteHandleOnChest(paint(view)));
  }

  static View2dContainer chestContainer() {
    View2dContainer container = new View2dContainer();
    View2d view = container.getView2d();
    view.setSize(400, 400);
    view.setSourceImage(new BufferedImage(2000, 2000, BufferedImage.TYPE_BYTE_GRAY));
    return container;
  }

  static void clickMeasure(View2dContainer container, int index) {
    javax.swing.AbstractButton button =
        (javax.swing.AbstractButton)
            container.getMeasureToolBar().getComponent().getComponent(index);
    org.weasis.core.ui.editor.image.MeasureToolBar.pressRelease(button);
  }

  static BufferedImage paint(View2d view) {
    BufferedImage page = new BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB);
    java.awt.Graphics2D g = page.createGraphics();
    try {
      view.paint(g);
    } finally {
      g.dispose();
    }
    return page;
  }

  @Test
  void tabCyclesLayoutViewsWhenMoreThanOne() {
    View2dContainer container = new View2dContainer();
    container.setLayoutCount(3);
    View2d first = container.getView2d();
    first.getEventManager().keyPressed(tab(first, 0));
    assertEquals(1, container.getLayoutIndex());
    first.getEventManager().keyPressed(tab(first, InputEvent.SHIFT_DOWN_MASK));
    assertEquals(0, container.getLayoutIndex());
    first.getEventManager().keyPressed(tab(first, InputEvent.CTRL_DOWN_MASK));
    assertEquals(0, container.getLayoutIndex());
  }

  @Test
  void viewMenuResetClearsZoomPanRotation() {
    View2dContainer container = new View2dContainer();
    container.getView2d().setZoom(2.0);
    container.getView2d().setPan(4, 5);
    container.getView2d().setRotation(90);
    container.setLayoutCount(2);
    assertEquals(2, container.getLayoutCount());
    container.resetDisplay();
    assertEquals(0.0, container.getView2d().getPanX(), 1e-9);
    assertEquals(0.0, container.getView2d().getRotation(), 1e-9);
  }

  @Test
  void oneByTwoAndTwoByTwoSplitTheSelectedPlugin() {
    View2dContainer container = new View2dContainer();
    assertEquals(1, container.getViewGrid().getComponentCount());
    container.setLayoutCount(2);
    assertEquals(2, container.getViewGrid().getComponentCount());
    GridLayout oneByTwo = (GridLayout) container.getViewGrid().getLayout();
    assertEquals(1, oneByTwo.getRows());
    assertEquals(2, oneByTwo.getColumns());
    assertSame(container.getLayoutViews().get(0), container.getViewGrid().getComponent(0));
    assertSame(container.getLayoutViews().get(1), container.getViewGrid().getComponent(1));
    container.setLayoutCount(4);
    assertEquals(4, container.getViewGrid().getComponentCount());
    GridLayout twoByTwo = (GridLayout) container.getViewGrid().getLayout();
    assertEquals(2, twoByTwo.getRows());
    assertEquals(2, twoByTwo.getColumns());
    container.setLayoutCount(1);
    assertEquals(1, container.getViewGrid().getComponentCount());
  }

  @Test
  void editSelectAllSelectsDrawingsOnFocusedView() {
    View2dContainer container = new View2dContainer();
    LineGraphic line = new LineGraphic();
    line.setHandlePoint(0, new Point2D.Double(0, 0));
    line.setHandlePoint(1, new Point2D.Double(4, 0));
    container.getView2d().addGraphic(line);
    container.selectAllGraphics();
    assertTrue(Boolean.TRUE.equals(line.getSelected()));
    container.deselectAllGraphics();
    assertTrue(container.getView2d().getSelectedGraphics().isEmpty());
  }

  @Test
  void deleteAllGraphicsClearsEveryLayoutCellIncludingEmptyBr() {
    View2dContainer container = new View2dContainer();
    container.setLayoutCount(4);
    View2d bl = container.getLayoutViews().get(2);
    View2d br = container.getLayoutViews().get(3);
    LineGraphic onBl = new LineGraphic();
    onBl.setHandlePoint(0, new Point2D.Double(0, 0));
    onBl.setHandlePoint(1, new Point2D.Double(4, 0));
    bl.addGraphic(onBl);
    LineGraphic onBr = new LineGraphic();
    onBr.setHandlePoint(0, new Point2D.Double(0, 0));
    onBr.setHandlePoint(1, new Point2D.Double(4, 0));
    br.addGraphic(onBr);
    container.deleteAllGraphics();
    for (View2d cell : container.getLayoutViews()) {
      assertTrue(cell.getGraphicList().isEmpty());
    }
  }

  static KeyEvent tab(View2d view, int mods) {
    return new KeyEvent(view, KeyEvent.KEY_PRESSED, 0L, mods, KeyEvent.VK_TAB, '\t');
  }

  static MouseEvent mouse(View2d view, int id, int x, int y) {
    int mods = id == MouseEvent.MOUSE_RELEASED ? 0 : InputEvent.BUTTON1_DOWN_MASK;
    return new MouseEvent(view, id, 0L, mods, x, y, 1, false, MouseEvent.BUTTON1);
  }

  static boolean yellowStrokeOnChest(BufferedImage page) {
    int maxX = Math.min(page.getWidth() - 1, 360);
    int maxY = Math.min(page.getHeight() - 1, 200);
    for (int y = 10; y < maxY; y++) {
      for (int x = 10; x < maxX; x++) {
        if (yellowAt(page, x, y)) {
          return true;
        }
      }
    }
    return false;
  }

  static boolean yellowAt(BufferedImage page, int x, int y) {
    for (int dy = -2; dy <= 2; dy++) {
      for (int dx = -2; dx <= 2; dx++) {
        int px = x + dx;
        int py = y + dy;
        if (px < 0 || py < 0 || px >= page.getWidth() || py >= page.getHeight()) {
          continue;
        }
        int rgb = page.getRGB(px, py);
        int r = (rgb >> 16) & 255;
        int green = (rgb >> 8) & 255;
        int b = rgb & 255;
        if (r > 200 && green > 200 && b < 80) {
          return true;
        }
      }
    }
    return false;
  }

  static boolean whiteHandleOnChest(BufferedImage page) {
    int maxX = Math.min(page.getWidth() - 1, 360);
    int maxY = Math.min(page.getHeight() - 1, 200);
    for (int y = 10; y < maxY; y++) {
      for (int x = 10; x < maxX; x++) {
        int rgb = page.getRGB(x, y);
        int r = (rgb >> 16) & 255;
        int green = (rgb >> 8) & 255;
        int b = rgb & 255;
        if (r > 220 && green > 220 && b > 220) {
          return true;
        }
      }
    }
    return false;
  }
}
