/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.codec.utils;

import java.util.Optional;
import java.util.OptionalDouble;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.weasis.core.api.image.measure.ImageSpacing;

/** Resolves per-instance row/column pitch in millimetres from DICOM spacing tags (fail-closed). */
public final class InstanceSpacing {

  /** Warning token when ERMF and SID/SOD disagree — object mm must not be inferred silently. */
  public static final String MAG_CONFLICT = "MAG_CONFLICT";

  static final String WARNING_DETECTOR = "detector-plane mm; object size unknown";
  static final String WARNING_ESTIMATE = "estimated at source-to-patient plane; not lesion size";

  private static final double MAG_CONFLICT_EPS = 0.01;

  public enum Source {
    PIXEL_SPACING,
    PIXEL_SPACING_CALIBRATED,
    IMAGER_DETECTOR,
    IMAGER_OBJECT_ESTIMATE
  }

  public record Resolved(ImageSpacing spacing, Source source, String warning, String note) {
    public Resolved(ImageSpacing spacing, Source source, String warning) {
      this(spacing, source, warning, "");
    }
  }

  private InstanceSpacing() {}

  public static Optional<Resolved> resolve(Attributes dataset) {
    if (dataset == null) {
      return Optional.empty();
    }
    Optional<ImageSpacing> pixel = spacingPair(dataset, Tag.PixelSpacing);
    if (pixel.isPresent()) {
      ImageSpacing spacing = pixel.get();
      String calType = dataset.getString(Tag.PixelSpacingCalibrationType, "").trim();
      String calDesc = dataset.getString(Tag.PixelSpacingCalibrationDescription, "").trim();
      Source source = calType.isEmpty() ? Source.PIXEL_SPACING : Source.PIXEL_SPACING_CALIBRATED;
      String note = calType.isEmpty() ? "" : calType;
      if (!calDesc.isEmpty()) {
        note = note.isEmpty() ? calDesc : note + "; " + calDesc;
      }
      return Optional.of(new Resolved(spacing, source, "", note));
    }

    Optional<ImageSpacing> imager = spacingPair(dataset, Tag.ImagerPixelSpacing);
    if (imager.isEmpty()) {
      return Optional.empty();
    }
    ImageSpacing detector = imager.get();

    Magnification mag = magnificationEvidence(dataset);
    if (mag.conflict()) {
      return Optional.of(
          new Resolved(
              detector,
              Source.IMAGER_DETECTOR,
              MAG_CONFLICT + ": ERMF disagrees with SID/SOD; " + WARNING_DETECTOR));
    }
    if (mag.factor().isPresent()) {
      double m = mag.factor().getAsDouble();
      ImageSpacing object = new ImageSpacing(detector.rowMm() / m, detector.colMm() / m);
      return Optional.of(new Resolved(object, Source.IMAGER_OBJECT_ESTIMATE, WARNING_ESTIMATE));
    }
    return Optional.of(new Resolved(detector, Source.IMAGER_DETECTOR, WARNING_DETECTOR));
  }

  private static Optional<ImageSpacing> spacingPair(Attributes dataset, int tag) {
    if (!dataset.containsValue(tag)) {
      return Optional.empty();
    }
    double[] values = dataset.getDoubles(tag);
    if (values == null || values.length != 2) {
      return Optional.empty();
    }
    if (!isUsable(values[0]) || !isUsable(values[1])) {
      return Optional.empty();
    }
    return Optional.of(new ImageSpacing(values[0], values[1]));
  }

  private static boolean isUsable(double mm) {
    return Double.isFinite(mm) && mm > 0;
  }

  private record Magnification(OptionalDouble factor, boolean conflict) {}

  private static Magnification magnificationEvidence(Attributes dataset) {
    double ermf = dataset.getDouble(Tag.EstimatedRadiographicMagnificationFactor, Double.NaN);
    double sid = dataset.getDouble(Tag.DistanceSourceToDetector, Double.NaN);
    double sod = dataset.getDouble(Tag.DistanceSourceToPatient, Double.NaN);

    boolean ermfUsable = isUsable(ermf) && ermf >= 1.0;
    boolean sidSodUsable = isUsable(sid) && isUsable(sod);
    OptionalDouble fromSidSod = OptionalDouble.empty();
    if (sidSodUsable) {
      double ratio = sid / sod;
      if (ratio >= 1.0) {
        fromSidSod = OptionalDouble.of(ratio);
      }
    }

    if (ermfUsable && sidSodUsable) {
      double ratio = sid / sod;
      if (ratio >= 1.0 && Math.abs(ermf - ratio) > MAG_CONFLICT_EPS) {
        return new Magnification(OptionalDouble.empty(), true);
      }
      if (ermfUsable) {
        return new Magnification(OptionalDouble.of(ermf), false);
      }
    }
    if (ermfUsable) {
      return new Magnification(OptionalDouble.of(ermf), false);
    }
    if (fromSidSod.isPresent()) {
      return new Magnification(fromSidSod, false);
    }
    return new Magnification(OptionalDouble.empty(), false);
  }
}
