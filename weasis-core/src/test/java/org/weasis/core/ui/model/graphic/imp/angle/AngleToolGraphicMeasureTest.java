/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.graphic.imp.angle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Point2D;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.image.measure.ImageSpacing;

/**
 * Why angle is dimensionless: the included angle depends only on direction in image pixel space
 * (two arms from the vertex handle). Scaling row/column mm per axis changes arm lengths but not the
 * angle between the same pixel vectors — so {@link AngleToolGraphic#getAngleDegrees()} uses raw
 * pixel deltas, never mm-scaled vectors.
 *
 * <p>Why arms use row/col spacing: each arm is a single segment from the vertex (handle 1) to an
 * end handle; mm length applies the same rule as {@link
 * org.weasis.core.ui.model.graphic.imp.line.LineGraphic#getLengthMm} — column spacing on horizontal
 * delta, row spacing on vertical (PS3.3 row-first PixelSpacing).
 *
 * <p>Why no spacing → px arms / degrees still ok: {@link AngleToolGraphic#getArmLengthPx(int)} and
 * {@link AngleToolGraphic#getAngleDegrees()} remain defined from handle geometry; {@link
 * AngleToolGraphic#getArmLengthMm(int, ImageSpacing)} returns empty when spacing is null or
 * invalid, without blocking the dimensionless angle or pixel arm lengths.
 *
 * <p>Why not copy Weasis {@code MeasureTool}: upstream bundles drawing, calibration overrides, and
 * presentation; this rebuild keeps three image-space handles on {@link AngleToolGraphic} and adds
 * measurement APIs beside {@code buildShape()} still null — no measure-tool import or Cobb logic.
 */
class AngleToolGraphicMeasureTest {

  private static final ImageSpacing ANISO = new ImageSpacing(0.5, 0.25);
  private static final ImageSpacing OTHER = new ImageSpacing(1.0, 2.0);

  @Test
  void rightAngleInPixelSpace() {
    AngleToolGraphic angle = rightAngleAtOrigin();
    assertEquals(90.0, angle.getAngleDegrees().orElseThrow(), 1e-9);
  }

  @Test
  void degreesUnchangedWhenSpacingChanges() {
    AngleToolGraphic angle = rightAngleAtOrigin();
    double d1 = angle.getAngleDegrees().orElseThrow();
    assertEquals(d1, angle.getAngleDegrees().orElseThrow(), 1e-9);
    assertEquals(2.5, angle.getArmLengthMm(0, ANISO).orElseThrow(), 1e-9);
    assertEquals(5.0, angle.getArmLengthMm(1, ANISO).orElseThrow(), 1e-9);
    assertEquals(20.0, angle.getArmLengthMm(0, OTHER).orElseThrow(), 1e-9);
    assertEquals(10.0, angle.getArmLengthMm(1, OTHER).orElseThrow(), 1e-9);
  }

  @Test
  void armLengthsPxFromVertex() {
    AngleToolGraphic angle = rightAngleAtOrigin();
    assertEquals(10.0, angle.getArmLengthPx(0), 1e-9);
    assertEquals(10.0, angle.getArmLengthPx(1), 1e-9);
  }

  @Test
  void anisotropicMmMatchesLineFormulaPerArm() {
    AngleToolGraphic angle = rightAngleAtOrigin();
    assertEquals(2.5, angle.getArmLengthMm(0, ANISO).orElseThrow(), 1e-9);
    assertEquals(5.0, angle.getArmLengthMm(1, ANISO).orElseThrow(), 1e-9);
  }

  @Test
  void noSpacingGivesEmptyMmOnly() {
    AngleToolGraphic angle = rightAngleAtOrigin();
    assertEquals(Optional.empty(), angle.getArmLengthMm(0, null));
    assertTrue(angle.getArmLengthMm(1, new ImageSpacing(0, 0.25)).isEmpty());
    assertEquals(90.0, angle.getAngleDegrees().orElseThrow(), 1e-9);
    assertEquals(10.0, angle.getArmLengthPx(0), 1e-9);
  }

  @Test
  void zeroLengthArmGivesEmptyDegrees() {
    AngleToolGraphic angle = new AngleToolGraphic();
    angle.setHandlePoint(0, new Point2D.Double(0, 0));
    angle.setHandlePoint(1, new Point2D.Double(0, 0));
    angle.setHandlePoint(2, new Point2D.Double(10, 0));
    assertTrue(angle.getAngleDegrees().isEmpty());
  }

  private static AngleToolGraphic rightAngleAtOrigin() {
    AngleToolGraphic angle = new AngleToolGraphic();
    angle.setHandlePoint(0, new Point2D.Double(10, 0));
    angle.setHandlePoint(1, new Point2D.Double(0, 0));
    angle.setHandlePoint(2, new Point2D.Double(0, 10));
    return angle;
  }
}
