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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Point2D;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.image.measure.ImageSpacing;

/**
 * Why (0028,0030): a two-corner rectangle scales the same pixel bbox area as a closed four-vertex
 * polygon by row spacing times column spacing — anisotropic voxels, not isotropic mm² or an
 * inscribed-ellipse shortcut.
 *
 * <p>Why fail-closed: {@link RectangleGraphic#getAreaMm} returns empty when spacing is null or
 * invalid, or either corner handle is missing — no mm² without usable spacing and a measurable
 * bbox.
 *
 * <p>Why not copy Weasis: upstream measure tools blend pixel geometry, calibration, and UI; this
 * rebuild keeps {@link RectangleGraphic#getAreaValue()} in pixels and applies the same {@link
 * ImageSpacing} record used for lines, polylines, and closed polygons.
 */
class RectangleGraphicAreaMmTest {

  private static final ImageSpacing ANISO = new ImageSpacing(0.5, 0.25);

  private static final Point2D.Double CORNER_A = new Point2D.Double(0, 0);
  private static final Point2D.Double CORNER_B = new Point2D.Double(10, 5);

  @Test
  void pixelAreaDefinedWithoutSpacing() {
    RectangleGraphic rect = rectangle(CORNER_A, CORNER_B);
    assertEquals(50.0, rect.getAreaValue(), 1e-9);
    assertTrue(rect.getAreaMm(null).isEmpty());
  }

  @Test
  void anisotropicSpacingScalesPixelBboxArea() {
    RectangleGraphic rect = rectangle(CORNER_A, CORNER_B);
    double expected = 50.0 * Math.abs(0.5 * 0.25);
    assertEquals(expected, rect.getAreaMm(ANISO).orElseThrow(), 1e-9);
  }

  @Test
  void matchesClosedPolygonOfSameBbox() {
    RectangleGraphic rect = rectangle(CORNER_A, CORNER_B);
    PolygonGraphic poly =
        polygon(
            new Point2D.Double(0, 0),
            new Point2D.Double(10, 0),
            new Point2D.Double(10, 5),
            new Point2D.Double(0, 5));
    assertEquals(
        poly.getAreaMm(ANISO).orElseThrow(), rect.getAreaMm(ANISO).orElseThrow(), 1e-9);
  }

  @Test
  void notEllipseAreaAlias() {
    RectangleGraphic rect = rectangle(CORNER_A, CORNER_B);
    double rectMm = rect.getAreaMm(ANISO).orElseThrow();
    double ellipseAlias = rectMm * (Math.PI / 4.0);
    assertNotEquals(ellipseAlias, rectMm, 1e-9);
  }

  @Test
  void twoVertexPolygonEmptyButRectangleDefined() {
    PolygonGraphic two = polygon(CORNER_A, CORNER_B);
    assertTrue(two.getAreaMm(ANISO).isEmpty());
    RectangleGraphic rect = rectangle(CORNER_A, CORNER_B);
    assertEquals(6.25, rect.getAreaMm(ANISO).orElseThrow(), 1e-9);
  }

  @Test
  void noSpacingGivesEmptyMm() {
    RectangleGraphic rect = rectangle(CORNER_A, CORNER_B);
    assertEquals(Optional.empty(), rect.getAreaMm(null));
    assertTrue(rect.getAreaMm(new ImageSpacing(0, 0.25)).isEmpty());
    assertTrue(rect.getAreaMm(new ImageSpacing(0.5, -1)).isEmpty());
  }

  @Test
  void missingHandleGivesEmptyMm() {
    RectangleGraphic oneCorner = new RectangleGraphic();
    oneCorner.setHandlePoint(0, CORNER_A);
    assertTrue(oneCorner.getAreaMm(ANISO).isEmpty());
  }

  private static RectangleGraphic rectangle(Point2D.Double a, Point2D.Double b) {
    RectangleGraphic rect = new RectangleGraphic();
    rect.setHandlePoint(0, a);
    rect.setHandlePoint(1, b);
    return rect;
  }

  private static PolygonGraphic polygon(Point2D.Double... points) {
    PolygonGraphic poly = new PolygonGraphic();
    for (int i = 0; i < points.length; i++) {
      poly.setHandlePoint(i, points[i]);
    }
    return poly;
  }
}
