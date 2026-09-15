/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.utils;

/** Pixel statistics for a raster or selected closed graphic. */
public class ImageStatistics {

  private final int samples;
  private final double min;
  private final double max;
  private final double mean;
  private final double stdev;

  public ImageStatistics(int samples, double min, double max, double mean, double stdev) {
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
}
