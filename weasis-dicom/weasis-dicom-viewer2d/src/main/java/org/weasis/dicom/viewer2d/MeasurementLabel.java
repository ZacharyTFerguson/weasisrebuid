/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d;

import java.util.Locale;
import java.util.Optional;
import org.weasis.core.ui.model.graphic.imp.line.LineGraphic;
import org.weasis.dicom.codec.utils.InstanceSpacing;
import org.weasis.dicom.codec.utils.RoiStatistics;

/** Text-only measurement labels from resolved spacing and ROI stats — no paint buffer. */
public final class MeasurementLabel {

  /** Geometry banner when {@link InstanceSpacing#resolve} is empty on the current instance. */
  public static final String NO_USABLE_SPACING_WARNING = "no usable pixel spacing for mm";

  private MeasurementLabel() {}

  public static String formatLine(LineGraphic line, Optional<InstanceSpacing.Resolved> resolved) {
    if (line == null) {
      return "";
    }
    double px = line.getLength();
    if (resolved.isEmpty()) {
      return formatPixels(px);
    }
    InstanceSpacing.Resolved r = resolved.get();
    Optional<Double> mm = line.getLengthMm(r.spacing());
    if (mm.isEmpty()) {
      return formatPixels(px);
    }
    String base = formatMm(mm.get());
    return switch (r.source()) {
      case IMAGER_DETECTOR -> base + " (detector plane)";
      case IMAGER_OBJECT_ESTIMATE -> base + " (estimate)";
      default -> base;
    };
  }

  public static String formatEllipse(RoiStatistics.RoiStats stats) {
    return RoiStatistics.formatMeanLabel(stats);
  }

  private static String formatPixels(double px) {
    return String.format(Locale.US, "%.1f px", px);
  }

  /**
   * Enough fraction digits for spacing math so magnification divides do not round 1.25 mm to 1.3
   * (e.g. 10 px × 0.15 ÷ 1.2).
   */
  static String formatMm(double mm) {
    long cents = Math.round(mm * 100.0);
    double value = cents / 100.0;
    if (cents % 10 != 0) {
      return String.format(Locale.US, "%.2f mm", value);
    }
    return String.format(Locale.US, "%.1f mm", value);
  }
}
