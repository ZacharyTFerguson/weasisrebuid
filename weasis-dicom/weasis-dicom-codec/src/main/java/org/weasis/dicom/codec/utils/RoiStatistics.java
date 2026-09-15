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

import java.awt.geom.Ellipse2D;
import java.util.Locale;
import java.util.Optional;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;

/**
 * ROI statistics on stored pixels through {@link LutPipeline#modalityValue} — never painted grey.
 */
public final class RoiStatistics {

  public record RoiStats(
      int n, int excluded, double mean, double min, double max, double stdDev, String unit) {}

  private RoiStatistics() {}

  public static Optional<RoiStats> ellipse(Attributes dcm, Ellipse2D roi) {
    if (dcm == null || roi == null || roi.getWidth() <= 0 || roi.getHeight() <= 0) {
      return Optional.empty();
    }
    if (dcm.containsValue(Tag.ModalityLUTSequence)) {
      return Optional.empty();
    }
    int rows = dcm.getInt(Tag.Rows, 0);
    int cols = dcm.getInt(Tag.Columns, 0);
    int[] pixels = dcm.getInts(Tag.PixelData);
    if (rows <= 0 || cols <= 0 || pixels == null || pixels.length < rows * cols) {
      return Optional.empty();
    }
    int pad = DicomMediaUtils.pixelPaddingValue(dcm);
    boolean hasPad = dcm.containsValue(Tag.PixelPaddingValue);

    int n = 0;
    int excluded = 0;
    double sum = 0;
    double min = Double.POSITIVE_INFINITY;
    double max = Double.NEGATIVE_INFINITY;
    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        double cx = col + 0.5;
        double cy = row + 0.5;
        if (!roi.contains(cx, cy)) {
          continue;
        }
        int stored = pixels[row * cols + col];
        if (hasPad && stored == pad) {
          excluded++;
          continue;
        }
        double modality = LutPipeline.modalityValue(dcm, stored);
        sum += modality;
        n++;
        min = Math.min(min, modality);
        max = Math.max(max, modality);
      }
    }
    if (n == 0) {
      return Optional.empty();
    }
    double mean = sum / n;
    double varSum = 0;
    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        double cx = col + 0.5;
        double cy = row + 0.5;
        if (!roi.contains(cx, cy)) {
          continue;
        }
        int stored = pixels[row * cols + col];
        if (hasPad && stored == pad) {
          continue;
        }
        double modality = LutPipeline.modalityValue(dcm, stored);
        double d = modality - mean;
        varSum += d * d;
      }
    }
    double stdDev = n > 1 ? Math.sqrt(varSum / (n - 1)) : 0;
    String unit = unitFrom(dcm);
    return Optional.of(new RoiStats(n, excluded, mean, min, max, stdDev, unit));
  }

  /** UI-facing mean string — must reflect {@link RoiStats}, not window/level paint. */
  public static String formatMeanLabel(RoiStats stats) {
    if (stats == null) {
      return "";
    }
    return String.format(Locale.US, "%.1f %s (n=%d)", stats.mean(), stats.unit(), stats.n());
  }

  private static String unitFrom(Attributes dcm) {
    String type = dcm.getString(Tag.RescaleType, "").trim();
    if (type.isEmpty()) {
      return "stored";
    }
    return type;
  }
}
