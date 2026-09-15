/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.editor.image;

import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;

/**
 * Pixel statistics for the selected closed graphic (or the whole image). Samples come from the
 * raster, then the view modality LUT, matching the histogram dock.
 */
public class ImageRegionStatistics {

  public static final class Stats {
    private final int samples;
    private final double min;
    private final double max;
    private final double mean;
    private final double stdev;

    public Stats(int samples, double min, double max, double mean, double stdev) {
      this.samples = samples;
      this.min = min;
      this.max = max;
      this.mean = mean;
      this.stdev = stdev;
    }

    public int getSamples() {
      return samples;
    }

    public double getMin() {
      return min;
    }

    public double getMax() {
      return max;
    }

    public double getMean() {
      return mean;
    }

    public double getStdev() {
      return stdev;
    }

    public String text() {
      return "n=" + samples + " min=" + min + " max=" + max + " mean=" + mean + " stdev=" + stdev;
    }
  }

  public static Stats compute(DefaultView2d<?> view) {
    if (view == null) {
      return new Stats(0, 0, 0, 0, 0);
    }
    return compute(
        view.getSourceImage(),
        HistogramView.selectedRoi(view),
        view.getModalityLutSlope(),
        view.getModalityLutIntercept());
  }

  public static Stats compute(BufferedImage image, Shape roi, double slope, double intercept) {
    if (image == null) {
      return new Stats(0, 0, 0, 0, 0);
    }
    Raster raster = image.getRaster();
    int w = image.getWidth();
    int h = image.getHeight();
    double lutSlope = slope == 0 ? 1.0 : slope;
    int n = 0;
    double sum = 0;
    double sumSq = 0;
    double min = Double.POSITIVE_INFINITY;
    double max = Double.NEGATIVE_INFINITY;
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        if (roi != null && !roi.contains(x + 0.5, y + 0.5)) {
          continue;
        }
        int raw =
            raster.getNumBands() == 1
                ? raster.getSample(x, y, 0)
                : (int)
                    Math.round(
                        0.299 * raster.getSample(x, y, 0)
                            + 0.587 * raster.getSample(x, y, 1)
                            + 0.114 * raster.getSample(x, y, 2));
        double v = raw * lutSlope + intercept;
        min = Math.min(min, v);
        max = Math.max(max, v);
        sum += v;
        sumSq += v * v;
        n++;
      }
    }
    if (n == 0) {
      return new Stats(0, 0, 0, 0, 0);
    }
    double mean = sum / n;
    double variance = Math.max(0.0, sumSq / n - mean * mean);
    return new Stats(n, min, max, mean, Math.sqrt(variance));
  }
}
