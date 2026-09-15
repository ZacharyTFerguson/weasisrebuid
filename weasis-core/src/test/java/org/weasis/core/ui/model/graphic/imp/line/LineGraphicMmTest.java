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
 * Why (0028,0030): row spacing scales vertical pixel delta, column spacing scales horizontal delta
 * (PS3.3 ordering).
 *
 * <p>Why fail-closed: {@link LineGraphic#getLengthMm} returns empty when spacing is null or invalid
 * — pixel length remains the only number.
 *
 * <p>Why not copy Weasis: {@link LineGraphic#getLength()} stays pure pixels; mm is an additive API
 * on tag-agnostic {@link ImageSpacing}.
 *
 * <p>Why this fixture: row 0.5 / col 0.25 breaks any implementation that uses one mm-per-pixel or
 * swaps row and column.
 */
class LineGraphicMmTest {

  private static final ImageSpacing ANISO = new ImageSpacing(0.5, 0.25);

  @Test
  void horizontalTenPixelsUsesColumnSpacing() {
    LineGraphic line = axisAlignedLine(0, 0, 10, 0);
    assertEquals(2.5, line.getLengthMm(ANISO).orElseThrow(), 1e-9);
  }

  @Test
  void verticalTenPixelsUsesRowSpacing() {
    LineGraphic line = axisAlignedLine(0, 0, 0, 10);
    assertEquals(5.0, line.getLengthMm(ANISO).orElseThrow(), 1e-9);
  }

  @Test
  void threeFourFiveAnisotropic() {
    LineGraphic line = axisAlignedLine(0, 0, 3, 4);
    double expected = Math.hypot(3 * ANISO.colMm(), 4 * ANISO.rowMm());
    assertEquals(expected, line.getLengthMm(ANISO).orElseThrow(), 1e-9);
  }

  @Test
  void pixelLengthUnchanged() {
    LineGraphic line = axisAlignedLine(0, 0, 3, 4);
    assertEquals(5.0, line.getLength(), 1e-9);
  }

  @Test
  void noSpacingGivesEmptyMm() {
    LineGraphic line = axisAlignedLine(0, 0, 3, 4);
    assertEquals(Optional.empty(), line.getLengthMm(null));
    assertTrue(line.getLengthMm(new ImageSpacing(0, 0.25)).isEmpty());
    assertTrue(line.getLengthMm(new ImageSpacing(0.5, -1)).isEmpty());
  }

  private static LineGraphic axisAlignedLine(double x0, double y0, double x1, double y1) {
    LineGraphic line = new LineGraphic();
    line.setHandlePoint(0, new Point2D.Double(x0, y0));
    line.setHandlePoint(1, new Point2D.Double(x1, y1));
    return line;
  }
}
