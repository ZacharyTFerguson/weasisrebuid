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

import java.awt.image.BufferedImage;
import java.awt.image.Raster;

/** Intensity histogram bins for the lens / histogram dock. */
public class HistogramData {

  private final int[] bins;

  public HistogramData(int binCount) {
    this.bins = new int[Math.max(1, binCount)];
  }

  public int[] getBins() {
    return bins;
  }

  public void accumulate(BufferedImage image) {
    java.util.Arrays.fill(bins, 0);
    if (image == null) {
      return;
    }
    Raster raster = image.getRaster();
    int w = raster.getWidth();
    int h = raster.getHeight();
    int max = bins.length - 1;
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        int v = raster.getSample(x, y, 0);
        int bin = Math.max(0, Math.min(max, v * bins.length / 256));
        bins[bin]++;
      }
    }
  }
}
