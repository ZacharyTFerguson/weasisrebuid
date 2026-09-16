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
 * Why (0028,0030): a closed interpolating curve is measured by sampling the smooth closed path in
 * image space (including the segment from the last handle back to the first) and summing per-step
 * physical length with the same row/column scaling as {@link LineGraphic#getLengthMm} — not by
 * reusing an open curve length or a polyline chord sum.
 *
 * <p>Why fail-closed: {@link ClosedCurveGraphic#getLengthMm} returns empty when spacing is null or
 * invalid, when there are too few handles to close the curve, or when sampling cannot proceed — no
 * guessed mm from pixel distance alone.
 *
 * <p>Why not copy Weasis: upstream spline tools mix closure, paint buffers, and measure UI; this
 * slice proves mm on a dedicated closed-curve graphic before any viewer draw or label wiring.
 */
class ClosedCurveGraphicMmTest {

  private static final ImageSpacing ANISO = new ImageSpacing(0.5, 0.25);

  @Test
  void closedMmDiffersFromOpenCurveAndPolylineForSameHandles() {
    Point2D.Double p0 = new Point2D.Double(0, 0);
    Point2D.Double p1 = new Point2D.Double(10, 0);
    Point2D.Double p2 = new Point2D.Double(10, 10);
    ClosedCurveGraphic closed = closed(p0, p1, p2);
    CurveGraphic open = openCurve(p0, p1, p2);
    PolylineGraphic poly = polyline(p0, p1, p2);
    double closedMm = closed.getLengthMm(ANISO).orElseThrow();
    double openMm = open.getLengthMm(ANISO).orElseThrow();
    double polyMm = poly.getLengthMm(ANISO).orElseThrow();
    assertNotEquals(openMm, closedMm, 1e-6);
    assertNotEquals(polyMm, closedMm, 1e-6);
    assertTrue(closedMm > openMm, "closing wrap should exceed open path");
    assertTrue(closedMm > polyMm, "smooth closed loop should exceed open polyline");
  }

  @Test
  void bowUsesAnisotropicRowColScaling() {
    ClosedCurveGraphic closed =
        closed(new Point2D.Double(0, 0), new Point2D.Double(5, 10), new Point2D.Double(10, 0));
    Optional<Double> mm = closed.getLengthMm(ANISO);
    Optional<Double> isoWrong = closed.getLengthMm(new ImageSpacing(0.375, 0.375));
    assertTrue(mm.isPresent());
    assertTrue(isoWrong.isPresent());
    assertNotEquals(mm.orElseThrow(), isoWrong.orElseThrow(), 1e-6);
  }

  @Test
  void noSpacingGivesEmptyMm() {
    ClosedCurveGraphic closed =
        closed(new Point2D.Double(0, 0), new Point2D.Double(5, 5), new Point2D.Double(10, 0));
    assertEquals(Optional.empty(), closed.getLengthMm(null));
    assertTrue(closed.getLengthMm(new ImageSpacing(0, 0.25)).isEmpty());
  }

  @Test
  void fewerThanThreeHandlesGivesEmptyMm() {
    ClosedCurveGraphic two = closed(new Point2D.Double(0, 0), new Point2D.Double(10, 0));
    assertTrue(two.getLengthMm(ANISO).isEmpty());
    ClosedCurveGraphic one = new ClosedCurveGraphic();
    one.setHandlePoint(0, new Point2D.Double(1, 1));
    assertTrue(one.getLengthMm(ANISO).isEmpty());
  }

  private static ClosedCurveGraphic closed(Point2D.Double... points) {
    ClosedCurveGraphic c = new ClosedCurveGraphic();
    for (int i = 0; i < points.length; i++) {
      c.setHandlePoint(i, points[i]);
    }
    return c;
  }

  private static CurveGraphic openCurve(Point2D.Double... points) {
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
