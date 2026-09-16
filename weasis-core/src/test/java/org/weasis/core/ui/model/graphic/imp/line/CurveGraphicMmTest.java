/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.graphic.imp.line;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Point2D;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.image.measure.ImageSpacing;

/**
 * Why (0028,0030): an open interpolating curve is measured by sampling the smooth path in image
 * space and summing per-step physical length with the same row/column scaling as {@link
 * LineGraphic#getLengthMm} — not by chaining straight segments between handles unless the path is
 * actually straight.
 *
 * <p>Why fail-closed: {@link CurveGraphic#getLengthMm} returns empty when spacing is null or
 * invalid, when there are too few handles to define the curve, or when sampling cannot proceed — no
 * guessed mm from pixel distance alone.
 *
 * <p>Why not copy Weasis: upstream open/closed spline tools tie paint buffers, closure flags, and
 * measure UI together; this slice proves mm on a dedicated graphic type before any viewer draw or
 * label wiring.
 */
class CurveGraphicMmTest {

  private static final ImageSpacing ANISO = new ImageSpacing(0.5, 0.25);

  @Test
  void collinearHandlesMatchPolylineMm() {
    Point2D.Double p0 = new Point2D.Double(0, 0);
    Point2D.Double p1 = new Point2D.Double(4, 0);
    Point2D.Double p2 = new Point2D.Double(7, 0);
    Point2D.Double p3 = new Point2D.Double(10, 0);
    CurveGraphic curve = curve(p0, p1, p2, p3);
    PolylineGraphic poly = polyline(p0, p1, p2, p3);
    assertEquals(
        poly.getLengthMm(ANISO).orElseThrow(),
        curve.getLengthMm(ANISO).orElseThrow(),
        1e-6);
  }

  @Test
  void nonColinearLShapeDiffersFromPolylineMm() {
    Point2D.Double p0 = new Point2D.Double(0, 0);
    Point2D.Double p1 = new Point2D.Double(10, 0);
    Point2D.Double p2 = new Point2D.Double(10, 10);
    CurveGraphic curve = curve(p0, p1, p2);
    PolylineGraphic poly = polyline(p0, p1, p2);
    double polyMm = poly.getLengthMm(ANISO).orElseThrow();
    double curveMm = curve.getLengthMm(ANISO).orElseThrow();
    assertEquals(7.5, polyMm, 1e-9);
    assertNotEquals(polyMm, curveMm, 1e-6);
    assertTrue(curveMm > polyMm, "smooth corner should exceed broken-line length");
  }

  @Test
  void bowUsesAnisotropicRowColScaling() {
    CurveGraphic curve =
        curve(
            new Point2D.Double(0, 0),
            new Point2D.Double(5, 10),
            new Point2D.Double(10, 0));
    Optional<Double> mm = curve.getLengthMm(ANISO);
    Optional<Double> isoWrong = curve.getLengthMm(new ImageSpacing(0.375, 0.375));
    assertTrue(mm.isPresent());
    assertTrue(isoWrong.isPresent());
    assertNotEquals(mm.orElseThrow(), isoWrong.orElseThrow(), 1e-6);
  }

  @Test
  void noSpacingGivesEmptyMm() {
    CurveGraphic curve = curve(new Point2D.Double(0, 0), new Point2D.Double(5, 5), new Point2D.Double(10, 0));
    assertEquals(Optional.empty(), curve.getLengthMm(null));
    assertTrue(curve.getLengthMm(new ImageSpacing(0, 0.25)).isEmpty());
  }

  @Test
  void fewerThanThreeHandlesGivesEmptyMm() {
    CurveGraphic two = curve(new Point2D.Double(0, 0), new Point2D.Double(10, 0));
    assertTrue(two.getLengthMm(ANISO).isEmpty());
    CurveGraphic one = new CurveGraphic();
    one.setHandlePoint(0, new Point2D.Double(1, 1));
    assertTrue(one.getLengthMm(ANISO).isEmpty());
  }

  private static CurveGraphic curve(Point2D.Double... points) {
    CurveGraphic c = new CurveGraphic();
    for (int i = 0; i < points.length; i++) {
      c.setHandlePoint(i, points[i]);
    }
    return c;
  }

  private static PolylineGraphic polyline(Point2D.Double... points) {
    PolylineGraphic poly = new PolylineGraphic();
    for (int i = 0; i < points.length; i++) {
      poly.setHandlePoint(i, points[i]);
    }
    return poly;
  }
}
