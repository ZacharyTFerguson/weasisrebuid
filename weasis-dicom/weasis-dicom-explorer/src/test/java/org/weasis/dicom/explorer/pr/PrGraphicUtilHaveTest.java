/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.pr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;
import org.junit.jupiter.api.Test;
import org.weasis.core.ui.model.graphic.Graphic;
import org.weasis.core.ui.model.graphic.imp.AnnotationGraphic;
import org.weasis.core.ui.model.graphic.imp.NonEditableGraphic;
import org.weasis.core.ui.model.graphic.imp.PointGraphic;
import org.weasis.core.ui.model.graphic.imp.area.EllipseGraphic;
import org.weasis.core.ui.model.graphic.imp.area.PolygonGraphic;
import org.weasis.core.ui.model.graphic.imp.area.RectangleGraphic;
import org.weasis.core.ui.model.graphic.imp.line.LineGraphic;
import org.weasis.core.ui.model.graphic.imp.line.PolylineGraphic;

class PrGraphicUtilHaveTest {

  private final PrGraphicUtil util = new PrGraphicUtil();

  @Test
  void interpolatedPathPassesThroughControlPoints() {
    InterpolatedPath2D open =
        new InterpolatedPath2D(new float[] {0, 0, 10, 0, 10, 10, 0, 10}, false);
    assertTrue(open.distanceTo(0, 0) < 0.05);
    assertTrue(open.distanceTo(10, 0) < 0.05);
    assertTrue(open.distanceTo(10, 10) < 0.05);
    assertTrue(open.distanceTo(0, 10) < 0.05);
    InterpolatedPath2D line = new InterpolatedPath2D(new float[] {0, 0, 10, 0}, false);
    Rectangle2D bounds = line.getBounds2D();
    assertEquals(10.0, bounds.getWidth(), 0.01);
    assertEquals(0.0, bounds.getHeight(), 0.01);
    InterpolatedPath2D closed =
        new InterpolatedPath2D(
            List.of(
                new Point2D.Double(0, 0),
                new Point2D.Double(10, 0),
                new Point2D.Double(10, 10),
                new Point2D.Double(0, 10)),
            true);
    assertTrue(closed.distanceTo(0, 0) < 0.05);
    assertTrue(new InterpolatedPath2D((float[]) null, false).getBounds2D().isEmpty());
  }

  @Test
  void graphicObjectTypesMapToWeasisGraphics() {
    Graphic point = util.buildGraphic(graphic("POINT", false, 4f, 6f));
    assertInstanceOf(PointGraphic.class, point);
    assertEquals(4.0, point.getPts().get(0).x, 0.001);
    assertEquals(6.0, point.getPts().get(0).y, 0.001);

    Graphic line = util.buildGraphic(graphic("POLYLINE", false, 0f, 0f, 10f, 0f));
    assertInstanceOf(LineGraphic.class, line);
    assertEquals(10.0, ((LineGraphic) line).getLength(), 0.001);

    Graphic poly = util.buildGraphic(graphic("POLYLINE", true, 0f, 0f, 10f, 0f, 10f, 8f, 0f, 8f));
    assertInstanceOf(PolygonGraphic.class, poly);
    assertEquals(Boolean.TRUE, poly.getFilled());
    assertEquals(80.0, ((PolygonGraphic) poly).getAreaValue(), 0.001);

    Graphic openPoly = util.buildGraphic(graphic("POLYLINE", false, 0f, 0f, 10f, 0f, 10f, 8f));
    assertInstanceOf(PolylineGraphic.class, openPoly);

    Graphic interp =
        util.buildGraphic(graphic("INTERPOLATED", false, 0f, 0f, 10f, 0f, 10f, 10f, 0f, 10f));
    assertInstanceOf(NonEditableGraphic.class, interp);
    assertInstanceOf(InterpolatedPath2D.class, interp.getShape());
    InterpolatedPath2D path = (InterpolatedPath2D) interp.getShape();
    assertTrue(path.distanceTo(0, 0) < 0.05);
    assertTrue(path.distanceTo(10, 10) < 0.05);

    Graphic circle = util.buildGraphic(graphic("CIRCLE", false, 5f, 5f, 8f, 5f));
    assertInstanceOf(EllipseGraphic.class, circle);
    Rectangle2D box = circle.getShape().getBounds2D();
    assertEquals(6.0, box.getWidth(), 0.001);
    assertEquals(6.0, box.getHeight(), 0.001);
    assertEquals(2.0, box.getX(), 0.001);

    Graphic ellipse = util.buildGraphic(graphic("ELLIPSE", true, 0f, 5f, 10f, 5f, 5f, 3f, 5f, 7f));
    assertInstanceOf(NonEditableGraphic.class, ellipse);
    Rectangle2D ebox = ellipse.getShape().getBounds2D();
    assertEquals(10.0, ebox.getWidth(), 0.05);
    assertEquals(4.0, ebox.getHeight(), 0.05);
  }

  @Test
  void displayUnitsScaleAndCompoundRectangleAndText() {
    Attributes go = graphic("POINT", false, 0.5f, 0.25f);
    go.setString(Tag.GraphicAnnotationUnits, VR.CS, "DISPLAY");
    Graphic scaled = util.buildGraphic(go, 200, 100);
    assertEquals(100.0, scaled.getPts().get(0).x, 0.001);
    assertEquals(25.0, scaled.getPts().get(0).y, 0.001);

    Attributes rect = new Attributes();
    rect.setString(Tag.CompoundGraphicType, VR.CS, "RECTANGLE");
    rect.setFloat(Tag.GraphicData, VR.FL, 1f, 2f, 5f, 6f);
    Graphic box = util.buildCompoundGraphic(rect);
    assertInstanceOf(RectangleGraphic.class, box);

    Attributes pr = new Attributes();
    Sequence layers = pr.newSequence(Tag.GraphicLayerSequence, 1);
    Attributes layer = new Attributes();
    layer.setString(Tag.GraphicLayer, VR.CS, "MEASURE");
    layer.setInt(Tag.GraphicLayerRecommendedDisplayGrayscaleValue, VR.US, 65535);
    layers.add(layer);
    Sequence anns = pr.newSequence(Tag.GraphicAnnotationSequence, 1);
    Attributes ann = new Attributes();
    ann.setString(Tag.GraphicLayer, VR.CS, "MEASURE");
    Sequence objs = ann.newSequence(Tag.GraphicObjectSequence, 1);
    objs.add(graphic("POLYLINE", false, 0f, 0f, 4f, 0f));
    Sequence texts = ann.newSequence(Tag.TextObjectSequence, 1);
    Attributes text = new Attributes();
    text.setString(Tag.UnformattedTextValue, VR.ST, "ROI");
    text.setFloat(Tag.AnchorPoint, VR.FL, 2f, 3f);
    texts.add(text);
    anns.add(ann);

    List<Graphic> graphics = util.getGraphics(pr);
    assertEquals(2, graphics.size());
    assertInstanceOf(LineGraphic.class, graphics.get(0));
    assertEquals(Color.WHITE, graphics.get(0).getColorPaint());
    assertInstanceOf(AnnotationGraphic.class, graphics.get(1));
    assertEquals("ROI", graphics.get(1).getLabel()[0]);
    assertTrue(util.getGraphics(null).isEmpty());
  }

  static Attributes graphic(String type, boolean filled, float... data) {
    Attributes go = new Attributes();
    go.setString(Tag.GraphicAnnotationUnits, VR.CS, "PIXEL");
    go.setString(Tag.GraphicType, VR.CS, type);
    go.setString(Tag.GraphicFilled, VR.CS, filled ? "Y" : "N");
    go.setInt(Tag.NumberOfGraphicPoints, VR.US, data.length / 2);
    go.setFloat(Tag.GraphicData, VR.FL, data);
    return go;
  }
}
