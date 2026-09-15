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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
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
    assertEquals(MeasureTool.NAMES.size(), bar.getComponent().getComponentCount());
    assertTrue(bar.newGraphic() instanceof LineGraphic);
    bar.setSelected("A");
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
    assertNotNull(view.getDrawing());
    view.getEventManager().mousePressed(mouse(view, MouseEvent.MOUSE_PRESSED, 0, 10, 1));
    assertNull(view.getDrawing());
    assertEquals(1, view.getGraphicList().size());
    AngleToolGraphic angle = (AngleToolGraphic) view.getGraphicList().getFirst();
    assertEquals(90.0, angle.getAngleDegrees(), 1e-6);
    assertNotNull(angle.getShape());
  }

  static MouseEvent mouse(DefaultView2d<?> view, int id, int x, int y, int clicks) {
    int mods = id == MouseEvent.MOUSE_RELEASED ? 0 : InputEvent.BUTTON1_DOWN_MASK;
    return new MouseEvent(view, id, 0L, mods, x, y, clicks, false, MouseEvent.BUTTON1);
  }
}
