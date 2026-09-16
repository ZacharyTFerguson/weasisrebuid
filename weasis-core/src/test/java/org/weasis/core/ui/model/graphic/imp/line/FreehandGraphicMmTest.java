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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.image.measure.ImageSpacing;

/**
 * Why (0028,0030): free-hand scribble length is the sum of physical steps between consecutive
 * image-space samples along the drawn stroke — the same per-step row/column scaling as {@link
 * LineGraphic#getLengthMm}, not a smooth spline through sparse handles and not a closed wrap.
 *
 * <p>Why fail-closed: {@link FreehandGraphic#getLengthMm} returns empty when spacing is null or
 * invalid, or when fewer than two samples exist — no mm from pixel distance alone.
 *
 * <p>Why not copy Weasis: upstream free-hand tools mix paint buffers, mouse drag, and measure UI;
 * this slice proves mm on a dedicated open-sample graphic before any viewer draw or label wiring.
 */
class FreehandGraphicMmTest {

  private static final ImageSpacing ANISO = new ImageSpacing(0.5, 0.25);

  @Test
  void denseSamplesMatchPolylineMmOnSamePoints() {
    List<Point2D.Double> samples =
        List.of(
            new Point2D.Double(0, 0),
            new Point2D.Double(4, 0),
            new Point2D.Double(7, 0),
            new Point2D.Double(10, 0));
    FreehandGraphic scribble = freehand(samples);
    PolylineGraphic poly = polyline(samples.toArray(Point2D.Double[]::new));
    assertEquals(
        poly.getLengthMm(ANISO).orElseThrow(), scribble.getLengthMm(ANISO).orElseThrow(), 1e-9);
  }

  @Test
  void lShapeSamplesDifferFromOpenCurveMm() {
    Point2D.Double p0 = new Point2D.Double(0, 0);
    Point2D.Double p1 = new Point2D.Double(10, 0);
    Point2D.Double p2 = new Point2D.Double(10, 10);
    FreehandGraphic scribble = freehand(p0, p1, p2);
    CurveGraphic curve = curve(p0, p1, p2);
    double scribbleMm = scribble.getLengthMm(ANISO).orElseThrow();
    double curveMm = curve.getLengthMm(ANISO).orElseThrow();
    assertEquals(7.5, scribbleMm, 1e-9);
    assertNotEquals(curveMm, scribbleMm, 1e-6);
  }

  @Test
  void lShapeSamplesDifferFromClosedCurveMm() {
    Point2D.Double p0 = new Point2D.Double(0, 0);
    Point2D.Double p1 = new Point2D.Double(10, 0);
    Point2D.Double p2 = new Point2D.Double(10, 10);
    FreehandGraphic scribble = freehand(p0, p1, p2);
    ClosedCurveGraphic closed = closed(p0, p1, p2);
    double scribbleMm = scribble.getLengthMm(ANISO).orElseThrow();
    double closedMm = closed.getLengthMm(ANISO).orElseThrow();
    assertNotEquals(closedMm, scribbleMm, 1e-6);
    assertTrue(closedMm > scribbleMm, "closed wrap should exceed open scribble");
  }

  @Test
  void denseOffChordScribbleDiffersFromSparseCornerPolyline() {
    Point2D.Double p0 = new Point2D.Double(0, 0);
    Point2D.Double p1 = new Point2D.Double(10, 0);
    Point2D.Double p2 = new Point2D.Double(10, 10);
    List<Point2D.Double> dense = new ArrayList<>();
    dense.add(p0);
    for (int i = 1; i <= 8; i++) {
      dense.add(new Point2D.Double(5, i));
    }
    dense.add(p1);
    dense.add(p2);
    FreehandGraphic scribble = freehand(dense);
    PolylineGraphic sparse = polyline(p0, p1, p2);
    double scribbleMm = scribble.getLengthMm(ANISO).orElseThrow();
    double sparseMm = sparse.getLengthMm(ANISO).orElseThrow();
    assertNotEquals(sparseMm, scribbleMm, 1e-6);
    assertTrue(scribbleMm > sparseMm, "bulging stroke should exceed corner-only polyline");
  }

  @Test
  void usesAnisotropicRowColScaling() {
    FreehandGraphic scribble =
        freehand(new Point2D.Double(0, 0), new Point2D.Double(5, 10), new Point2D.Double(10, 0));
    Optional<Double> mm = scribble.getLengthMm(ANISO);
    Optional<Double> isoWrong = scribble.getLengthMm(new ImageSpacing(0.375, 0.375));
    assertTrue(mm.isPresent());
    assertTrue(isoWrong.isPresent());
    assertNotEquals(mm.orElseThrow(), isoWrong.orElseThrow(), 1e-6);
  }

  @Test
  void noSpacingGivesEmptyMm() {
    FreehandGraphic scribble =
        freehand(new Point2D.Double(0, 0), new Point2D.Double(5, 5), new Point2D.Double(10, 0));
    assertEquals(Optional.empty(), scribble.getLengthMm(null));
    assertTrue(scribble.getLengthMm(new ImageSpacing(0, 0.25)).isEmpty());
  }

  @Test
  void fewerThanTwoSamplesGivesEmptyMm() {
    FreehandGraphic empty = new FreehandGraphic();
    assertTrue(empty.getLengthMm(ANISO).isEmpty());
    FreehandGraphic one = freehand(new Point2D.Double(1, 1));
    assertTrue(one.getLengthMm(ANISO).isEmpty());
  }

  private static FreehandGraphic freehand(Point2D.Double... points) {
    return freehand(List.of(points));
  }

  private static FreehandGraphic freehand(List<Point2D.Double> samples) {
    FreehandGraphic g = new FreehandGraphic();
    g.setSamples(samples);
    return g;
  }

  private static PolylineGraphic polyline(Point2D.Double... points) {
    PolylineGraphic poly = new PolylineGraphic();
    for (int i = 0; i < points.length; i++) {
      poly.setHandlePoint(i, points[i]);
    }
    return poly;
  }

  private static CurveGraphic curve(Point2D.Double... points) {
    CurveGraphic c = new CurveGraphic();
    for (int i = 0; i < points.length; i++) {
      c.setHandlePoint(i, points[i]);
    }
    return c;
  }

  private static ClosedCurveGraphic closed(Point2D.Double... points) {
    ClosedCurveGraphic c = new ClosedCurveGraphic();
    for (int i = 0; i < points.length; i++) {
      c.setHandlePoint(i, points[i]);
    }
    return c;
  }
}
