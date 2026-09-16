/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.graphic.imp.area;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Point2D;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.image.measure.ImageSpacing;

/**
 * Why (0028,0030): closed polygon area in mm² scales pixel shoelace area by row spacing times
 * column spacing (PS3.3 row-first ordering) — anisotropic voxels, not a single mm² factor.
 *
 * <p>Why fail-closed: {@link PolygonGraphic#getAreaMm} returns empty when spacing is null or
 * invalid, or fewer than three vertices — no mm² without usable spacing and a closed polygon path.
 *
 * <p>Why not copy Weasis: upstream {@code MeasureTool} mixes pixel geometry, calibration hooks, and
 * UI; this rebuild keeps {@link PolygonGraphic#getAreaValue()} in pixels separate and applies the
 * same {@link ImageSpacing} record the DICOM resolver already proved for lines and polylines.
 */
class PolygonGraphicAreaMmTest {

  private static final ImageSpacing ANISO = new ImageSpacing(0.5, 0.25);

  @Test
  void pixelAreaDefinedWithoutSpacing() {
    PolygonGraphic poly =
        polygon(new Point2D.Double(0, 0), new Point2D.Double(10, 0), new Point2D.Double(10, 10));
    assertEquals(50.0, poly.getAreaValue(), 1e-9);
    assertTrue(poly.getAreaMm(null).isEmpty());
  }

  @Test
  void rightTriangleUsesRowTimesColScale() {
    PolygonGraphic poly =
        polygon(new Point2D.Double(0, 0), new Point2D.Double(10, 0), new Point2D.Double(10, 10));
    assertEquals(6.25, poly.getAreaMm(ANISO).orElseThrow(), 1e-9);
  }

  @Test
  void axisAlignedRectangleMatchesPixelAreaTimesSpacingProduct() {
    PolygonGraphic poly =
        polygon(
            new Point2D.Double(0, 0),
            new Point2D.Double(10, 0),
            new Point2D.Double(10, 5),
            new Point2D.Double(0, 5));
    assertEquals(50.0, poly.getAreaValue(), 1e-9);
    assertEquals(6.25, poly.getAreaMm(ANISO).orElseThrow(), 1e-9);
  }

  @Test
  void noSpacingGivesEmptyMm() {
    PolygonGraphic poly =
        polygon(new Point2D.Double(0, 0), new Point2D.Double(10, 0), new Point2D.Double(10, 10));
    assertEquals(Optional.empty(), poly.getAreaMm(null));
    assertTrue(poly.getAreaMm(new ImageSpacing(0, 0.25)).isEmpty());
    assertTrue(poly.getAreaMm(new ImageSpacing(0.5, -1)).isEmpty());
  }

  @Test
  void fewerThanThreeVerticesGivesEmptyMm() {
    PolygonGraphic two = polygon(new Point2D.Double(0, 0), new Point2D.Double(10, 5));
    assertTrue(two.getAreaMm(ANISO).isEmpty());
    PolygonGraphic one = new PolygonGraphic();
    one.setHandlePoint(0, new Point2D.Double(1, 1));
    assertTrue(one.getAreaMm(ANISO).isEmpty());
  }

  private static PolygonGraphic polygon(Point2D.Double... points) {
    PolygonGraphic poly = new PolygonGraphic();
    for (int i = 0; i < points.length; i++) {
      poly.setHandlePoint(i, points[i]);
    }
    return poly;
  }
}
