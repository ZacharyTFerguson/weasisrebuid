/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.graphic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.weasis.core.ui.model.graphic.imp.AnnotationGraphic;
import org.weasis.core.ui.model.graphic.imp.PixelInfoGraphic;
import org.weasis.core.ui.model.graphic.imp.PointGraphic;
import org.weasis.core.ui.model.graphic.imp.angle.AngleToolGraphic;
import org.weasis.core.ui.model.graphic.imp.angle.CobbAngleToolGraphic;
import org.weasis.core.ui.model.graphic.imp.angle.FourPointsAngleGraphic;
import org.weasis.core.ui.model.graphic.imp.angle.OpenAngleToolGraphic;
import org.weasis.core.ui.model.graphic.imp.area.EllipseGraphic;
import org.weasis.core.ui.model.graphic.imp.area.ObliqueRectangleGraphic;
import org.weasis.core.ui.model.graphic.imp.area.PolygonGraphic;
import org.weasis.core.ui.model.graphic.imp.area.RectangleGraphic;
import org.weasis.core.ui.model.graphic.imp.area.SelectGraphic;
import org.weasis.core.ui.model.graphic.imp.area.ThreePointsCircleGraphic;
import org.weasis.core.ui.model.graphic.imp.line.LineGraphic;
import org.weasis.core.ui.model.graphic.imp.line.LineWithGapGraphic;
import org.weasis.core.ui.model.graphic.imp.line.ParallelLineGraphic;
import org.weasis.core.ui.model.graphic.imp.line.PerpendicularLineGraphic;
import org.weasis.core.ui.model.graphic.imp.line.PolylineGraphic;

class GraphicCatalogTest {

  @Test
  void everyChecklistGraphicKindCreates() {
    for (GraphicKind kind : GraphicKind.values()) {
      if (kind == GraphicKind.NON_EDITABLE) {
        continue;
      }
      Graphic g = kind.create();
      assertNotNull(g, kind.name());
      assertEquals(kind.measurement(), g.isMeasurement(), kind.name());
    }
  }

  @Test
  void lineAndGapAndParallelAndPerp() {
    LineGraphic line = new LineGraphic();
    line.setHandlePoint(0, new Point2D.Double(0, 0));
    line.setHandlePoint(1, new Point2D.Double(10, 0));
    assertEquals(10.0, line.getLength(), 1e-9);
    LineWithGapGraphic gap = new LineWithGapGraphic();
    gap.setHandlePoint(0, new Point2D.Double(0, 0));
    gap.setHandlePoint(1, new Point2D.Double(20, 0));
    gap.setGapSize(4);
    assertNotNull(gap.getShape());
    ParallelLineGraphic par = new ParallelLineGraphic();
    par.setHandlePoint(0, new Point2D.Double(0, 0));
    par.setHandlePoint(1, new Point2D.Double(10, 0));
    par.setHandlePoint(2, new Point2D.Double(0, 5));
    par.setHandlePoint(3, new Point2D.Double(8, 5));
    assertTrue(par.isParallel());
    PerpendicularLineGraphic perp = new PerpendicularLineGraphic();
    perp.setHandlePoint(0, new Point2D.Double(0, 0));
    perp.setHandlePoint(1, new Point2D.Double(10, 0));
    perp.setHandlePoint(2, new Point2D.Double(4, 6));
    assertTrue(perp.isPerpendicular());
  }

  @Test
  void anglesRightAngleAndCobb() {
    AngleToolGraphic ang = new AngleToolGraphic();
    ang.setHandlePoint(0, new Point2D.Double(1, 0));
    ang.setHandlePoint(1, new Point2D.Double(0, 0));
    ang.setHandlePoint(2, new Point2D.Double(0, 1));
    assertEquals(90.0, ang.getAngleDeg(), 1e-6);
    OpenAngleToolGraphic open = new OpenAngleToolGraphic();
    open.setHandlePoint(0, new Point2D.Double(0, 0));
    open.setHandlePoint(1, new Point2D.Double(1, 0));
    open.setHandlePoint(2, new Point2D.Double(0, 0));
    open.setHandlePoint(3, new Point2D.Double(0, 2));
    assertEquals(90.0, open.getAngleDeg(), 1e-6);
    CobbAngleToolGraphic cobb = new CobbAngleToolGraphic();
    cobb.setHandlePoint(0, new Point2D.Double(0, 0));
    cobb.setHandlePoint(1, new Point2D.Double(1, 0));
    cobb.setHandlePoint(2, new Point2D.Double(0, 0));
    cobb.setHandlePoint(3, new Point2D.Double(1, 1));
    assertEquals(45.0, cobb.getAngleDeg(), 1e-6);
    FourPointsAngleGraphic four = new FourPointsAngleGraphic();
    four.setHandlePoint(0, new Point2D.Double(0, 0));
    four.setHandlePoint(1, new Point2D.Double(1, 0));
    four.setHandlePoint(2, new Point2D.Double(0, 0));
    four.setHandlePoint(3, new Point2D.Double(0, 1));
    assertEquals(90.0, four.getAngleDeg(), 1e-6);
  }

  @Test
  void areasPolygonOmbbThreePointCircleSelectNotMeasurement() {
    RectangleGraphic r = new RectangleGraphic();
    r.setHandlePoint(0, new Point2D.Double(0, 0));
    r.setHandlePoint(1, new Point2D.Double(4, 3));
    assertEquals(12.0, r.getAreaValue(), 1e-9);
    EllipseGraphic e = new EllipseGraphic();
    e.setHandlePoint(0, new Point2D.Double(0, 0));
    e.setHandlePoint(1, new Point2D.Double(2, 2));
    assertNotNull(e.getShape());
    PolygonGraphic poly = new PolygonGraphic();
    List<Point2D.Double> pts = new ArrayList<>();
    pts.add(new Point2D.Double(0, 0));
    pts.add(new Point2D.Double(4, 0));
    pts.add(new Point2D.Double(4, 3));
    pts.add(new Point2D.Double(0, 3));
    poly.setPts(pts);
    assertEquals(12.0, poly.getAreaValue(), 1e-9);
    assertEquals(14.0, poly.getPerimeter(), 1e-9);
    assertEquals(12.0, poly.getOmbbArea(), 1e-6);
    ThreePointsCircleGraphic c = new ThreePointsCircleGraphic();
    c.setHandlePoint(0, new Point2D.Double(1, 0));
    c.setHandlePoint(1, new Point2D.Double(0, 1));
    c.setHandlePoint(2, new Point2D.Double(-1, 0));
    assertEquals(Math.PI, c.getAreaValue(), 1e-6);
    ObliqueRectangleGraphic ob = new ObliqueRectangleGraphic();
    ob.setHandlePoint(0, new Point2D.Double(0, 0));
    ob.setHandlePoint(1, new Point2D.Double(4, 0));
    ob.setHandlePoint(2, new Point2D.Double(0, 3));
    assertEquals(12.0, ob.getAreaValue(), 1e-9);
    assertFalse(new SelectGraphic().isMeasurement());
    assertFalse(new AnnotationGraphic().isMeasurement());
    assertTrue(new PointGraphic().isMeasurement());
    assertTrue(new PixelInfoGraphic().isMeasurement());
    PolylineGraphic pl = new PolylineGraphic();
    pl.setPts(List.of(new Point2D.Double(0, 0), new Point2D.Double(3, 4)));
    assertNotNull(pl.getShape());
  }

  @Test
  void fillOpacityTimesLineAlpha() {
    LineGraphic g = new LineGraphic();
    g.setFillOpacity(0.8f);
    g.setLineAlpha(0.2f);
    assertEquals(0.16f, g.perceivedFillAlpha(), 1e-6f);
  }
}
