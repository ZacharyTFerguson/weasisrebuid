/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.dockable.components.actions.annotate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.awt.geom.Point2D;
import org.junit.jupiter.api.Test;
import org.weasis.core.ui.editor.image.dockable.MeasureTool;
import org.weasis.core.ui.model.graphic.Graphic;
import org.weasis.core.ui.model.graphic.imp.AnnotationGraphic;
import org.weasis.core.ui.model.graphic.imp.angle.AngleToolGraphic;
import org.weasis.core.ui.model.graphic.imp.area.EllipseGraphic;
import org.weasis.core.ui.model.graphic.imp.area.PolygonGraphic;
import org.weasis.core.ui.model.graphic.imp.area.RectangleGraphic;
import org.weasis.core.ui.model.graphic.imp.line.LineGraphic;
import org.weasis.core.ui.model.graphic.imp.line.PolylineGraphic;

class AcquireAnnotateHaveTest {

  @Test
  void annotatePanelCreatesWp5GraphicsOnly() {
    AnnotatePanel panel = new AnnotatePanel();
    Point2D.Double a = new Point2D.Double(0, 0);
    Point2D.Double b = new Point2D.Double(10, 0);
    Point2D.Double c = new Point2D.Double(10, 10);
    Point2D.Double d = new Point2D.Double(0, 10);

    panel.setTool(MeasureTool.DISTANCE);
    assertInstanceOf(LineGraphic.class, panel.createGraphic(a, b));
    panel.setTool(MeasureTool.ANGLE);
    assertInstanceOf(AngleToolGraphic.class, panel.createGraphic(a, b, c));
    panel.setTool(MeasureTool.POLYLINE);
    assertInstanceOf(PolylineGraphic.class, panel.createGraphic(a, b, c));
    panel.setTool(MeasureTool.TEXTBOX);
    assertInstanceOf(AnnotationGraphic.class, panel.createGraphic(a, b));
    panel.setTool(MeasureTool.RECTANGLE);
    assertInstanceOf(RectangleGraphic.class, panel.createGraphic(a, c));
    panel.setTool(MeasureTool.ELLIPSE);
    assertInstanceOf(EllipseGraphic.class, panel.createGraphic(a, c));
    panel.setTool(MeasureTool.POLYGON);
    assertInstanceOf(PolygonGraphic.class, panel.createGraphic(a, b, c, d));

    for (String name : MeasureTool.NAMES) {
      panel.setTool(name);
      Graphic g = panel.createGraphic(a, b, c, d);
      assertNotNull(g, name);
      assertNotNull(g.getShape(), name);
      assertTrue(
          g instanceof LineGraphic
              || g instanceof AngleToolGraphic
              || g instanceof PolylineGraphic
              || g instanceof AnnotationGraphic
              || g instanceof RectangleGraphic
              || g instanceof EllipseGraphic
              || g instanceof PolygonGraphic,
          name);
    }
  }

  @Test
  void optionsApplyColorThicknessFillToCreatedGraphic() {
    AnnotatePanel panel = new AnnotatePanel();
    panel.setTool(MeasureTool.RECTANGLE);
    panel.options().setColor(Color.RED);
    panel.options().setLineThickness(2.5f);
    panel.options().setFilled(true);
    panel.options().setLabelVisible(false);
    Graphic g = panel.createGraphic(new Point2D.Double(1, 1), new Point2D.Double(4, 5));
    assertEquals(Color.RED, g.getColorPaint());
    assertEquals(2.5f, g.getLineThickness(), 1e-6f);
    assertEquals(Boolean.TRUE, g.getFilled());
    assertEquals(Boolean.FALSE, g.getLabelVisible());
  }

  @Test
  void annotateActionAddsGraphicsToModel() {
    AnnotateAction action = new AnnotateAction();
    action.setTool(MeasureTool.DISTANCE);
    Graphic first = action.add(new Point2D.Double(0, 0), new Point2D.Double(8, 0));
    action.setTool(MeasureTool.TEXTBOX);
    Graphic second = action.add(new Point2D.Double(2, 2), new Point2D.Double(6, 4));
    assertEquals(2, action.model().getModels().size());
    assertTrue(action.model().getModels().contains(first));
    assertTrue(action.model().getModels().contains(second));
    assertInstanceOf(LineGraphic.class, first);
    assertInstanceOf(AnnotationGraphic.class, second);
    assertNotNull(first.getShape());
  }
}
