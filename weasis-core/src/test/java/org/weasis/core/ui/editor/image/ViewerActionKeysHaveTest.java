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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.gui.Insertable;
import org.weasis.core.api.image.util.Unit;
import org.weasis.core.api.media.data.ImageElement;
import org.weasis.core.api.media.data.Series;
import org.weasis.core.ui.editor.image.dockable.MiniTool;
import org.weasis.core.ui.model.graphic.imp.area.RectangleGraphic;
import org.weasis.core.ui.model.graphic.imp.line.LineGraphic;

class ViewerActionKeysHaveTest {

  @Test
  void documentedKeysSelectLeftMouseActions() {
    DefaultView2d<?> view = new DefaultView2d<>();
    view.getEventManager().keyPressed(key(view, KeyEvent.VK_T, 0));
    assertEquals(MouseActions.PAN, view.getMouseActions().getLeft());
    view.getEventManager().keyPressed(key(view, KeyEvent.VK_W, 0));
    assertEquals(MouseActions.WINLEVEL, view.getMouseActions().getLeft());
    view.getEventManager().keyPressed(key(view, KeyEvent.VK_S, 0));
    assertEquals(MouseActions.SEQUENCE, view.getMouseActions().getLeft());
    view.getEventManager().keyPressed(key(view, KeyEvent.VK_Z, 0));
    assertEquals(MouseActions.ZOOM, view.getMouseActions().getLeft());
    view.getEventManager().keyPressed(key(view, KeyEvent.VK_R, 0));
    assertEquals(MouseActions.ROTATION, view.getMouseActions().getLeft());
  }

  @Test
  void winLevelDragDoesNotStartRubberBand() {
    DefaultView2d<?> view = new DefaultView2d<>();
    assertEquals(MouseActions.WINLEVEL, view.getMouseActions().getLeft());
    view.addGraphic(line(0, 0, 8, 0));
    view.getEventManager().mousePressed(mouse(view, 20, 20, 1, false));
    view.getEventManager().mouseDragged(mouse(view, 30, 25, 1, false));
    view.getEventManager().mouseReleased(mouse(view, 30, 25, 1, false));
    assertEquals(1, view.getGraphicList().size());
    assertTrue(view.getSelectedGraphics().isEmpty());
  }

  @Test
  void qBuildsContextMenuAndPPrints() {
    DefaultView2d<?> view = new DefaultView2d<>();
    view.setSourceImage(gray(new int[][] {{8, 8}, {8, 8}}));
    view.getEventManager().keyPressed(key(view, KeyEvent.VK_Q, 0));
    assertNotNull(view.getContextMenuHandler().lastMenu());
    assertTrue(
        view.getContextMenuHandler().lastMenu().getComponentCount() > ViewerToolBar.ACTIONS.length);
    view.getEventManager().keyPressed(key(view, KeyEvent.VK_P, 0));
    assertNotNull(view.getLastPrint());
    assertNotNull(view.getLastPrint().getOptions());
  }

  @Test
  void cTogglesCineOnTheBoundSeries() {
    ImageElement a = new ImageElement();
    a.setImage(gray(new int[][] {{1, 1}}));
    ImageElement b = new ImageElement();
    b.setImage(gray(new int[][] {{2, 2}, {2, 2}}));
    Series<ImageElement> series = new Series<>();
    series.addMedia(a);
    series.addMedia(b);
    DefaultView2d<?> view = new DefaultView2d<>();
    view.setSeries(series);
    view.setFrameIndex(0);
    view.getEventManager().keyPressed(key(view, KeyEvent.VK_C, 0));
    assertTrue(view.cineListener().isCineRunning());
    view.cineListener().tick();
    assertEquals(1, view.getFrameIndex());
    view.getEventManager().keyPressed(key(view, KeyEvent.VK_C, 0));
    assertFalse(view.cineListener().isCineRunning());
  }

  @Test
  void ctrlAndAltDoNotStealLetterActionsExceptAltP() {
    DefaultView2d<?> view = new DefaultView2d<>();
    assertEquals(MouseActions.WINLEVEL, view.getMouseActions().getLeft());
    view.getEventManager().keyPressed(key(view, KeyEvent.VK_M, InputEvent.CTRL_DOWN_MASK));
    assertEquals(MouseActions.WINLEVEL, view.getMouseActions().getLeft());
    view.getEventManager().keyPressed(key(view, KeyEvent.VK_C, InputEvent.ALT_DOWN_MASK));
    assertFalse(view.cineListener().isCineRunning());
    view.getEventManager().keyPressed(key(view, KeyEvent.VK_P, InputEvent.ALT_DOWN_MASK));
    assertNotNull(view.getLastPrint());
  }

  @Test
  void ctrlDragAcceleratesCurrentAction() {
    DefaultView2d<?> view = new DefaultView2d<>();
    view.getMouseActions().setLeft(MouseActions.PAN);
    view.getEventManager().mousePressed(mouse(view, 10, 10, 1, false));
    view.getEventManager().mouseDragged(drag(view, 20, 10, InputEvent.CTRL_DOWN_MASK));
    assertEquals(20.0, view.getPanX(), 1e-9);
    view.setPan(0, 0);
    view.getEventManager().mousePressed(mouse(view, 10, 10, 1, false));
    view.getEventManager()
        .mouseDragged(drag(view, 20, 10, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
    assertEquals(40.0, view.getPanX(), 1e-9);
    view.getMouseActions().setLeft(MouseActions.NONE);
    view.setPan(0, 0);
    view.getEventManager().mousePressed(mouse(view, 20, 20, 1, false));
    view.getEventManager().mouseDragged(drag(view, 30, 25, InputEvent.CTRL_DOWN_MASK));
    assertEquals(0.0, view.getPanX(), 1e-9);
  }

  @Test
  void miniToolSlidersAndPannerMoveTheView() {
    DefaultView2d<?> view = new DefaultView2d<>();
    view.setSize(200, 200);
    view.setZoom(1.0);
    view.setSourceImage(gray(new int[100][100]));
    MiniTool mini = new MiniTool();
    assertEquals("Mini Tool", mini.getComponentName());
    assertEquals(Insertable.Type.TOOL, mini.getType());
    mini.bind(view);
    assertSameView(view, mini.boundView());
    mini.getPanner().setSize(100, 100);
    mini.getPanner().panAt(0, 0);
    assertEquals(50.0, view.getPanX(), 1e-9);
    assertEquals(50.0, view.getPanY(), 1e-9);
    mini.getZoomSlider().setValue(200);
    assertEquals(2.0, view.getZoom(), 1e-9);
    mini.getRotationSlider().setValue(90);
    assertEquals(90.0, view.getRotation(), 1e-9);
  }

  @Test
  void manualCalibrationDoesNotWriteMonitorPitch() {
    DefaultView2d<?> view = new DefaultView2d<>();
    view.setMonitorCalibrationMmPerPixel(0.2);
    LineGraphic line = line(0, 0, 10, 0);
    line.setSelected(true);
    view.addGraphic(line);
    CalibrationView cal = new CalibrationView(view);
    cal.setKnownLength(20.0, Unit.MILLIMETER);
    assertEquals(2.0, cal.apply(), 1e-9);
    assertEquals(2.0, view.getSessionManualCalibrationMmPerPixel(), 1e-9);
    assertEquals(0.2, view.getMonitorCalibrationMmPerPixel(), 1e-9);
    assertNotEquals(
        view.getMonitorCalibrationMmPerPixel(), view.getSessionManualCalibrationMmPerPixel());
  }

  @Test
  void regionStatisticsFollowSelectedClosedGraphic() {
    DefaultView2d<?> view = new DefaultView2d<>();
    view.setSourceImage(gray(new int[][] {{10, 200}, {10, 200}}));
    RectangleGraphic roi = new RectangleGraphic();
    roi.setHandlePoint(0, new Point2D.Double(0, 0));
    roi.setHandlePoint(1, new Point2D.Double(1, 2));
    roi.setSelected(true);
    view.addGraphic(roi);
    ImageRegionStatistics.Stats stats = ImageRegionStatistics.compute(view);
    assertEquals(2, stats.getSamples());
    assertEquals(10.0, stats.getMin(), 1e-9);
    assertEquals(10.0, stats.getMax(), 1e-9);
    assertTrue(stats.text().contains("n=2"));
  }

  static void assertSameView(DefaultView2d<?> expected, DefaultView2d<?> actual) {
    assertEquals(expected, actual);
  }

  static LineGraphic line(double x1, double y1, double x2, double y2) {
    LineGraphic graphic = new LineGraphic();
    graphic.setHandlePoint(0, new Point2D.Double(x1, y1));
    graphic.setHandlePoint(1, new Point2D.Double(x2, y2));
    return graphic;
  }

  static MouseEvent mouse(DefaultView2d<?> view, int x, int y, int clicks, boolean shift) {
    int mods = InputEvent.BUTTON1_DOWN_MASK | (shift ? InputEvent.SHIFT_DOWN_MASK : 0);
    return new MouseEvent(
        view, MouseEvent.MOUSE_PRESSED, 0L, mods, x, y, clicks, false, MouseEvent.BUTTON1);
  }

  static MouseEvent drag(DefaultView2d<?> view, int x, int y, int extraMods) {
    return new MouseEvent(
        view,
        MouseEvent.MOUSE_DRAGGED,
        0L,
        InputEvent.BUTTON1_DOWN_MASK | extraMods,
        x,
        y,
        1,
        false,
        MouseEvent.BUTTON1);
  }

  static KeyEvent key(DefaultView2d<?> view, int code, int mods) {
    return new KeyEvent(view, KeyEvent.KEY_PRESSED, 0L, mods, code, (char) code);
  }

  static BufferedImage gray(int[][] pixels) {
    BufferedImage image =
        new BufferedImage(pixels[0].length, pixels.length, BufferedImage.TYPE_BYTE_GRAY);
    for (int y = 0; y < pixels.length; y++) {
      for (int x = 0; x < pixels[y].length; x++) {
        image.getRaster().setSample(x, y, 0, pixels[y][x]);
      }
    }
    return image;
  }
}
