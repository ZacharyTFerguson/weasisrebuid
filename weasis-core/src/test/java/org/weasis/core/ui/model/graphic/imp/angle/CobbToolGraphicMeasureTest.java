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
 * Why Cobb angle is dimensionless: scoliosis Cobb is the angle between two vertebral endplate
 * directions in image pixel space (handles 0→1 and 2→3). Row/column mm scaling changes endplate
 * segment lengths but not the angle between the same pixel direction vectors — so {@link
 * CobbToolGraphic#getCobbAngleDegrees()} uses raw pixel deltas per endplate, never mm-scaled
 * vectors (same rule as {@link AngleToolGraphic#getAngleDegrees()}).
 *
 * <p>Why endplate mm uses row/col spacing: each endplate is one segment between its two handles; mm
 * length applies PS3.3 PixelSpacing the same way as {@link
 * org.weasis.core.ui.model.graphic.imp.line.LineGraphic#getLengthMm} — column spacing on horizontal
 * delta, row spacing on vertical.
 *
 * <p>Why no spacing → px endplates / degrees still ok: {@link
 * CobbToolGraphic#getEndplateLengthPx(int)} and {@link CobbToolGraphic#getCobbAngleDegrees()} stay
 * defined from handle geometry; {@link CobbToolGraphic#getEndplateLengthMm(int, ImageSpacing)}
 * returns empty when spacing is null or invalid, without blocking the dimensionless Cobb angle or
 * pixel endplate lengths.
 *
 * <p>Why not copy Weasis {@code MeasureTool}: upstream bundles drawing, calibration overrides, and
 * presentation; this rebuild keeps four image-space handles on {@link CobbToolGraphic} (upper then
 * lower endplate), leaves {@code buildShape()} null, and adds measurement APIs beside existing
 * line/angle spacing discipline — no measure-tool import, no interactive draw, and no View2d label
 * in this slice.
 */
class CobbToolGraphicMeasureTest {

  private static final ImageSpacing ANISO = new ImageSpacing(0.5, 0.25);
  private static final ImageSpacing OTHER = new ImageSpacing(1.0, 2.0);

  @Test
  void perpendicularEndplatesInPixelSpace() {
    CobbToolGraphic cobb = perpendicularEndplates();
    assertEquals(90.0, cobb.getCobbAngleDegrees().orElseThrow(), 1e-9);
  }

  @Test
  void degreesUnchangedWhenSpacingChanges() {
    CobbToolGraphic cobb = perpendicularEndplates();
    double d1 = cobb.getCobbAngleDegrees().orElseThrow();
    assertEquals(d1, cobb.getCobbAngleDegrees().orElseThrow(), 1e-9);
    assertEquals(2.5, cobb.getEndplateLengthMm(0, ANISO).orElseThrow(), 1e-9);
    assertEquals(5.0, cobb.getEndplateLengthMm(1, ANISO).orElseThrow(), 1e-9);
    assertEquals(20.0, cobb.getEndplateLengthMm(0, OTHER).orElseThrow(), 1e-9);
    assertEquals(10.0, cobb.getEndplateLengthMm(1, OTHER).orElseThrow(), 1e-9);
  }

  @Test
  void endplateLengthsPxFromHandles() {
    CobbToolGraphic cobb = perpendicularEndplates();
    assertEquals(10.0, cobb.getEndplateLengthPx(0), 1e-9);
    assertEquals(10.0, cobb.getEndplateLengthPx(1), 1e-9);
  }

  @Test
  void anisotropicMmMatchesLineFormulaPerEndplate() {
    CobbToolGraphic cobb = perpendicularEndplates();
    assertEquals(2.5, cobb.getEndplateLengthMm(0, ANISO).orElseThrow(), 1e-9);
    assertEquals(5.0, cobb.getEndplateLengthMm(1, ANISO).orElseThrow(), 1e-9);
  }

  @Test
  void noSpacingGivesEmptyMmOnly() {
    CobbToolGraphic cobb = perpendicularEndplates();
    assertEquals(Optional.empty(), cobb.getEndplateLengthMm(0, null));
    assertTrue(cobb.getEndplateLengthMm(1, new ImageSpacing(0, 0.25)).isEmpty());
    assertEquals(90.0, cobb.getCobbAngleDegrees().orElseThrow(), 1e-9);
    assertEquals(10.0, cobb.getEndplateLengthPx(0), 1e-9);
  }

  @Test
  void parallelEndplatesZeroDegrees() {
    CobbToolGraphic cobb = new CobbToolGraphic();
    cobb.setHandlePoint(0, new Point2D.Double(0, 0));
    cobb.setHandlePoint(1, new Point2D.Double(10, 0));
    cobb.setHandlePoint(2, new Point2D.Double(0, 20));
    cobb.setHandlePoint(3, new Point2D.Double(10, 20));
    assertEquals(0.0, cobb.getCobbAngleDegrees().orElseThrow(), 1e-9);
  }

  @Test
  void zeroLengthEndplateGivesEmptyDegrees() {
    CobbToolGraphic cobb = new CobbToolGraphic();
    cobb.setHandlePoint(0, new Point2D.Double(0, 0));
    cobb.setHandlePoint(1, new Point2D.Double(0, 0));
    cobb.setHandlePoint(2, new Point2D.Double(0, 10));
    cobb.setHandlePoint(3, new Point2D.Double(10, 10));
    assertTrue(cobb.getCobbAngleDegrees().isEmpty());
  }

  private static CobbToolGraphic perpendicularEndplates() {
    CobbToolGraphic cobb = new CobbToolGraphic();
    cobb.setHandlePoint(0, new Point2D.Double(0, 0));
    cobb.setHandlePoint(1, new Point2D.Double(10, 0));
    cobb.setHandlePoint(2, new Point2D.Double(5, 10));
    cobb.setHandlePoint(3, new Point2D.Double(5, 0));
    return cobb;
  }
}
