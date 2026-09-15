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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Point2D;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.image.measure.ImageSpacing;

/**
 * Why (0028,0030): each polyline segment is measured like {@link LineGraphic#getLengthMm} — row
 * spacing scales vertical delta, column spacing scales horizontal delta (PS3.3 row-first ordering).
 * Total mm is the sum of segment lengths, not a single hypot across the first and last handle.
 *
 * <p>Why fail-closed: {@link PolylineGraphic#getLengthMm} returns empty when spacing is null or
 * invalid, or fewer than two vertices — no mm without usable spacing.
 *
 * <p>Why not copy Weasis: upstream {@code MeasureTool} mixes pixel geometry, calibration hooks, and
 * UI; this rebuild keeps pixel paths separate and applies the same {@link ImageSpacing} record the
 * DICOM resolver already proved for lines.
 */
class PolylineGraphicMmTest {

  private static final ImageSpacing ANISO = new ImageSpacing(0.5, 0.25);

  @Test
  void twoSegmentsSumMmNotEndToEndHypot() {
    // L-shape: 10 px horizontal then 10 px vertical → 2.5 + 5.0 = 7.5 mm, not hypot(10,10) in mm.
    PolylineGraphic poly = polyline(
        new Point2D.Double(0, 0), new Point2D.Double(10, 0), new Point2D.Double(10, 10));
    assertEquals(7.5, poly.getLengthMm(ANISO).orElseThrow(), 1e-9);
  }

  @Test
  void singleSegmentMatchesLineFormula() {
    PolylineGraphic poly = polyline(new Point2D.Double(0, 0), new Point2D.Double(3, 4));
    LineGraphic line = new LineGraphic();
    line.setHandlePoint(0, new Point2D.Double(0, 0));
    line.setHandlePoint(1, new Point2D.Double(3, 4));
    double expected = line.getLengthMm(ANISO).orElseThrow();
    assertEquals(expected, poly.getLengthMm(ANISO).orElseThrow(), 1e-9);
  }

  @Test
  void threeCollinearHorizontalSegmentsUseColumnSpacingEach() {
    PolylineGraphic poly =
        polyline(
            new Point2D.Double(0, 0),
            new Point2D.Double(4, 0),
            new Point2D.Double(7, 0),
            new Point2D.Double(10, 0));
    assertEquals(2.5, poly.getLengthMm(ANISO).orElseThrow(), 1e-9);
  }

  @Test
  void noSpacingGivesEmptyMm() {
    PolylineGraphic poly = polyline(new Point2D.Double(0, 0), new Point2D.Double(10, 0));
    assertEquals(Optional.empty(), poly.getLengthMm(null));
    assertTrue(poly.getLengthMm(new ImageSpacing(0, 0.25)).isEmpty());
  }

  @Test
  void fewerThanTwoPointsGivesEmptyMm() {
    PolylineGraphic empty = new PolylineGraphic();
    assertTrue(empty.getLengthMm(ANISO).isEmpty());
    PolylineGraphic one = new PolylineGraphic();
    one.setHandlePoint(0, new Point2D.Double(1, 1));
    assertTrue(one.getLengthMm(ANISO).isEmpty());
  }

  private static PolylineGraphic polyline(Point2D.Double... points) {
    PolylineGraphic poly = new PolylineGraphic();
    for (int i = 0; i < points.length; i++) {
      poly.setHandlePoint(i, points[i]);
    }
    return poly;
  }
}
