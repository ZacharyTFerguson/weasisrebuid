/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.fusion;

/**
 * SUV display window: 1 SUVbw to the next whole SUVbw above series max, clamped 4–30. Non-SUV: 0 …
 * series max in stored units.
 */
public final class FusionWindow {

  public static final double SUV_MIN = 1.0;
  public static final double CLAMP_LO = 4.0;
  public static final double CLAMP_HI = 30.0;

  public record Range(double min, double max, String unit) {

    public double span() {
      return max - min;
    }
  }

  private FusionWindow() {}

  public static Range suv(double percentileMax) {
    double upper = Math.ceil(percentileMax);
    upper = Math.max(CLAMP_LO, Math.min(CLAMP_HI, upper));
    return new Range(SUV_MIN, upper, "SUVbw");
  }

  public static Range stored(double seriesMax, String unit) {
    return new Range(0, seriesMax, unit == null ? "" : unit);
  }

  /**
   * Overlay alpha reaches the Opacity setting within the lower tenth of the display window.
   * Transparent below ~1%.
   */
  public static double overlayAlpha(double value, Range window, double opacity) {
    double span = window.span();
    if (span <= 0) {
      return 0;
    }
    double t = (value - window.min()) / span;
    if (t < 0.01) {
      return 0;
    }
    if (t >= 0.10) {
      return opacity;
    }
    return opacity * (t / 0.10);
  }

  /** result = baseOpacity·base·(1 − a) + overlay·a. Base 0% → overlay on black. */
  public static double composite(double base, double overlay, double a, double baseOpacity) {
    return baseOpacity * base * (1.0 - a) + overlay * a;
  }

  public static boolean drawColorScale(int viewHeightPx) {
    return viewHeightPx >= 350;
  }
}
