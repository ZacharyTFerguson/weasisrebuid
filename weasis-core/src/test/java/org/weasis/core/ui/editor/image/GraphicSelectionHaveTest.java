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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.weasis.core.ui.editor.image.dockable.MeasureTool;
import org.weasis.core.ui.model.graphic.Graphic;
import org.weasis.core.ui.model.graphic.imp.line.LineGraphic;
import org.weasis.core.ui.model.graphic.imp.line.PolylineGraphic;

class GraphicSelectionHaveTest {

  @Test
  void clickSelectsAndShiftClickToggles() {
    DefaultView2d<?> view = new DefaultView2d<>();
    view.getMouseActions().setLeft(MouseActions.NONE);
    LineGraphic a = line(0, 0, 10, 0);
    LineGraphic b = line(0, 10, 10, 10);
    view.addGraphic(a);
    view.addGraphic(b);
    view.getEventManager().mousePressed(mouse(view, 1, 0, 1, false));
    assertTrue(Boolean.TRUE.equals(a.getSelected()));
    assertFalse(Boolean.TRUE.equals(b.getSelected()));
    view.getEventManager().mousePressed(mouse(view, 1, 10, 1, true));
    assertTrue(Boolean.TRUE.equals(a.getSelected()));
    assertTrue(Boolean.TRUE.equals(b.getSelected()));
    view.getEventManager().mousePressed(mouse(view, 1, 10, 1, true));
    assertFalse(Boolean.TRUE.equals(b.getSelected()));
  }

  @Test
  void ctrlASelectsAllCtrlDClearsDeleteRemoves() {
    DefaultView2d<?> view = new DefaultView2d<>();
    LineGraphic a = line(0, 0, 4, 0);
    LineGraphic b = line(0, 4, 4, 4);
    view.addGraphic(a);
    view.addGraphic(b);
    AtomicInteger models = new AtomicInteger();
    List<Graphic> last = new ArrayList<>();
    view.addGraphicModelChangeListener(models::incrementAndGet);
    view.addGraphicSelectionListener(last::addAll);
    view.getEventManager().keyPressed(key(view, KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
    assertEquals(2, view.getSelectedGraphics().size());
    view.getEventManager().keyPressed(key(view, KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK));
    assertTrue(view.getSelectedGraphics().isEmpty());
    a.setSelected(true);
    view.getEventManager().keyPressed(key(view, KeyEvent.VK_DELETE, 0));
    assertEquals(1, view.getGraphicList().size());
    assertSame(b, view.getGraphicList().getFirst());
    assertTrue(models.get() > 0);
  }

  @Test
  void rubberBandSelectsIntersectingAndNEnablesSelect() {
    DefaultView2d<?> view = new DefaultView2d<>();
    view.getEventManager().keyPressed(key(view, KeyEvent.VK_N, 0));
    assertEquals(MouseActions.NONE, view.getMouseActions().getLeft());
    view.addGraphic(line(0, 0, 8, 0));
    view.addGraphic(line(0, 8, 8, 8));
    view.getEventManager().mousePressed(mouse(view, 20, 20, 1, false));
    view.getEventManager().mouseDragged(mouse(view, -1, -1, 1, false));
    view.getEventManager().mouseReleased(mouse(view, -1, -1, 1, false));
    assertEquals(2, view.getSelectedGraphics().size());
    assertEquals(2, view.getGraphicList().size());
  }

  @Test
  void dSetsDistanceAndDoubleClickStillEndsPolyline() {
    DefaultView2d<?> view = new DefaultView2d<>();
    view.getEventManager().keyPressed(key(view, KeyEvent.VK_D, 0));
    assertEquals(MeasureTool.DISTANCE, view.getMeasureTool());
    assertEquals(MouseActions.MEASURE, view.getMouseActions().getLeft());
    view.setMeasureTool(MeasureTool.POLYLINE);
    view.getEventManager().mousePressed(mouse(view, 0, 0, 1, false));
    view.getEventManager().mousePressed(mouse(view, 4, 0, 1, false));
    assertTrue(view.getDrawing() instanceof PolylineGraphic);
    view.getEventManager().mousePressed(mouse(view, 4, 0, 2, false));
    assertEquals(null, view.getDrawing());
  }

  static LineGraphic line(double x1, double y1, double x2, double y2) {
    LineGraphic line = new LineGraphic();
    line.setHandlePoint(0, new Point2D.Double(x1, y1));
    line.setHandlePoint(1, new Point2D.Double(x2, y2));
    return line;
  }

  static MouseEvent mouse(DefaultView2d<?> view, int x, int y, int clicks, boolean shift) {
    int mods = InputEvent.BUTTON1_DOWN_MASK | (shift ? InputEvent.SHIFT_DOWN_MASK : 0);
    return new MouseEvent(
        view, MouseEvent.MOUSE_PRESSED, 0L, mods, x, y, clicks, false, MouseEvent.BUTTON1);
  }

  static KeyEvent key(DefaultView2d<?> view, int code, int mods) {
    return new KeyEvent(view, KeyEvent.KEY_PRESSED, 0L, mods, code, (char) code);
  }
}
