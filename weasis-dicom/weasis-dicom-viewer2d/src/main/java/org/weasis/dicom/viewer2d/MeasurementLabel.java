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
import org.weasis.core.api.image.measure.ImageSpacing;
import org.weasis.core.ui.model.graphic.imp.angle.AngleToolGraphic;
import org.weasis.core.ui.model.graphic.imp.angle.CobbToolGraphic;
import org.weasis.core.ui.model.graphic.imp.area.PolygonGraphic;
import org.weasis.core.ui.model.graphic.imp.line.ClosedCurveGraphic;
import org.weasis.core.ui.model.graphic.imp.line.CurveGraphic;
import org.weasis.core.ui.model.graphic.imp.line.LineGraphic;
import org.weasis.core.ui.model.graphic.imp.line.PolylineGraphic;
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
    return formatLengthMm(
        line.getLength(),
        line.getLengthMm(resolved.map(InstanceSpacing.Resolved::spacing).orElse(null)),
        resolved);
  }

  public static String formatPolyline(
      PolylineGraphic polyline, Optional<InstanceSpacing.Resolved> resolved) {
    if (polyline == null) {
      return "";
    }
    return formatLengthMm(
        polyline.getLength(),
        polyline.getLengthMm(resolved.map(InstanceSpacing.Resolved::spacing).orElse(null)),
        resolved);
  }

  public static String formatCurve(
      CurveGraphic curve, Optional<InstanceSpacing.Resolved> resolved) {
    if (curve == null) {
      return "";
    }
    return formatLengthMm(
        curve.getLength(),
        curve.getLengthMm(resolved.map(InstanceSpacing.Resolved::spacing).orElse(null)),
        resolved);
  }

  public static String formatClosedCurve(
      ClosedCurveGraphic closedCurve, Optional<InstanceSpacing.Resolved> resolved) {
    if (closedCurve == null) {
      return "";
    }
    return formatLengthMm(
        closedCurve.getLength(),
        closedCurve.getLengthMm(resolved.map(InstanceSpacing.Resolved::spacing).orElse(null)),
        resolved);
  }

  public static String formatAngle(
      AngleToolGraphic angle, Optional<InstanceSpacing.Resolved> resolved) {
    if (angle == null) {
      return "";
    }
    Optional<Double> degrees = angle.getAngleDegrees();
    if (degrees.isEmpty()) {
      return "";
    }
    String degPart = formatDegrees(degrees.get());
    ImageSpacing spacing = resolved.map(InstanceSpacing.Resolved::spacing).orElse(null);
    Optional<Double> mm0 = angle.getArmLengthMm(0, spacing);
    Optional<Double> mm1 = angle.getArmLengthMm(1, spacing);
    double px0 = angle.getArmLengthPx(0);
    double px1 = angle.getArmLengthPx(1);
    String arms = formatAngleArms(px0, px1, mm0, mm1, resolved);
    return degPart + "  " + arms;
  }

  public static String formatPolygon(
      PolygonGraphic polygon, Optional<InstanceSpacing.Resolved> resolved) {
    if (polygon == null) {
      return "";
    }
    ImageSpacing spacing = resolved.map(InstanceSpacing.Resolved::spacing).orElse(null);
    Optional<Double> mmOpt = polygon.getAreaMm(spacing);
    double pxArea = polygon.getAreaValue();
    if (resolved.isEmpty() || mmOpt.isEmpty()) {
      return formatPixelsArea(pxArea);
    }
    String base = formatAreaMm(mmOpt.get());
    InstanceSpacing.Resolved r = resolved.get();
    return switch (r.source()) {
      case IMAGER_DETECTOR -> base + " (detector plane)";
      case IMAGER_OBJECT_ESTIMATE -> base + " (estimate)";
      default -> base;
    };
  }

  public static String formatCobb(
      CobbToolGraphic cobb, Optional<InstanceSpacing.Resolved> resolved) {
    if (cobb == null) {
      return "";
    }
    Optional<Double> degrees = cobb.getCobbAngleDegrees();
    if (degrees.isEmpty()) {
      return "";
    }
    String degPart = formatDegrees(degrees.get());
    ImageSpacing spacing = resolved.map(InstanceSpacing.Resolved::spacing).orElse(null);
    Optional<Double> mm0 = cobb.getEndplateLengthMm(0, spacing);
    Optional<Double> mm1 = cobb.getEndplateLengthMm(1, spacing);
    double px0 = cobb.getEndplateLengthPx(0);
    double px1 = cobb.getEndplateLengthPx(1);
    String endplates = formatAngleArms(px0, px1, mm0, mm1, resolved);
    return degPart + "  " + endplates;
  }

  private static String formatAngleArms(
      double px0,
      double px1,
      Optional<Double> mm0,
      Optional<Double> mm1,
      Optional<InstanceSpacing.Resolved> resolved) {
    if (resolved.isEmpty() || mm0.isEmpty() || mm1.isEmpty()) {
      return formatPixels(px0) + " / " + formatPixels(px1);
    }
    return formatLengthMmPair(mm0.get(), mm1.get(), resolved.get());
  }

  /**
   * Two arm lengths in mm with the same {@link InstanceSpacing.Source} suffix as a single length.
   */
  private static String formatLengthMmPair(
      double mm0, double mm1, InstanceSpacing.Resolved resolved) {
    String base = formatMm(mm0) + " / " + formatMm(mm1);
    return switch (resolved.source()) {
      case IMAGER_DETECTOR -> base + " (detector plane)";
      case IMAGER_OBJECT_ESTIMATE -> base + " (estimate)";
      default -> base;
    };
  }

  private static String formatDegrees(double degrees) {
    return String.format(Locale.US, "%.1f°", degrees);
  }

  private static String formatLengthMm(
      double pxLength, Optional<Double> mmOpt, Optional<InstanceSpacing.Resolved> resolved) {
    if (resolved.isEmpty()) {
      return formatPixels(pxLength);
    }
    if (mmOpt.isEmpty()) {
      return formatPixels(pxLength);
    }
    String base = formatMm(mmOpt.get());
    InstanceSpacing.Resolved r = resolved.get();
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

  private static String formatPixelsArea(double pxArea) {
    return String.format(Locale.US, "%.1f px²", pxArea);
  }

  /** Same rounding rules as {@link #formatMm} but with mm² area unit. */
  private static String formatAreaMm(double mm2) {
    long cents = Math.round(mm2 * 100.0);
    double value = cents / 100.0;
    if (cents % 10 != 0) {
      return String.format(Locale.US, "%.2f mm²", value);
    }
    return String.format(Locale.US, "%.1f mm²", value);
  }

  /** Enough fraction digits for spacing math (e.g. 10 px × 0.15 ÷ 1.2 → 1.25 mm, not 1.3). */
  static String formatMm(double mm) {
    long cents = Math.round(mm * 100.0);
    double value = cents / 100.0;
    if (cents % 10 != 0) {
      return String.format(Locale.US, "%.2f mm", value);
    }
    return String.format(Locale.US, "%.1f mm", value);
  }
}
