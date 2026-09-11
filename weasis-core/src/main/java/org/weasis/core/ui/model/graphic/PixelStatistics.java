/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.graphic;

import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pixel stats for closed measurement graphics: Pixels, Min, Max, Median, Mean, StDev, Skewness,
 * Kurtosis, Entropy. SUV min/max/mean is applied by the caller when PET metadata is present.
 */
public final class PixelStatistics {

  private PixelStatistics() {}

  public record Result(
      int pixels,
      double min,
      double max,
      double median,
      double mean,
      double stdev,
      double skewness,
      double kurtosis,
      double entropy) {}

  public static Result of(BufferedImage image, Shape shape) {
    if (image == null || shape == null) {
      return empty();
    }
    Raster raster = image.getRaster();
    int w = image.getWidth();
    int h = image.getHeight();
    List<Double> vals = new ArrayList<>();
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        if (shape.contains(x + 0.5, y + 0.5)) {
          vals.add((double) raster.getSample(x, y, 0));
        }
      }
    }
    return of(vals);
  }

  public static Result of(List<Double> vals) {
    if (vals == null || vals.isEmpty()) {
      return empty();
    }
    List<Double> sorted = new ArrayList<>(vals);
    Collections.sort(sorted);
    int n = sorted.size();
    double min = sorted.get(0);
    double max = sorted.get(n - 1);
    double median =
        n % 2 == 1 ? sorted.get(n / 2) : 0.5 * (sorted.get(n / 2 - 1) + sorted.get(n / 2));
    double sum = 0;
    for (double v : sorted) {
      sum += v;
    }
    double mean = sum / n;
    double m2 = 0;
    double m3 = 0;
    double m4 = 0;
    for (double v : sorted) {
      double d = v - mean;
      m2 += d * d;
      m3 += d * d * d;
      m4 += d * d * d * d;
    }
    double variance = m2 / n;
    double stdev = Math.sqrt(variance);
    double skewness = stdev == 0 ? 0 : (m3 / n) / (stdev * stdev * stdev);
    double kurtosis = stdev == 0 ? 0 : (m4 / n) / (variance * variance) - 3;
    double entropy = histogramEntropy(sorted);
    return new Result(n, min, max, median, mean, stdev, skewness, kurtosis, entropy);
  }

  static Result empty() {
    return new Result(0, 0, 0, 0, 0, 0, 0, 0, 0);
  }

  static double histogramEntropy(List<Double> sorted) {
    if (sorted.isEmpty()) {
      return 0;
    }
    int n = sorted.size();
    double h = 0;
    int i = 0;
    while (i < n) {
      double v = sorted.get(i);
      int j = i + 1;
      while (j < n && Double.compare(sorted.get(j), v) == 0) {
        j++;
      }
      double p = (j - i) / (double) n;
      h -= p * (Math.log(p) / Math.log(2));
      i = j;
    }
    return h;
  }
}
