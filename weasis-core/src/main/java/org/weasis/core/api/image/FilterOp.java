/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.image;

import java.awt.image.BufferedImage;
import org.weasis.core.api.image.util.KernelData;

/** Windowing-panel filter. Default {@code None}; runs before pseudo-color. */
public class FilterOp extends AbstractOp {

  public static final String P_FILTER = "filter";
  public static final String NONE = "None";

  public FilterOp() {
    super("op.filter");
    setParam(P_FILTER, NONE);
  }

  @Override
  protected void processEnabled() {
    setParam(OUTPUT_IMG, filterIfNeeded(getParam(INPUT_IMG)));
  }

  Object filterIfNeeded(Object in) {
    if (!(in instanceof BufferedImage src)) {
      return in;
    }
    KernelData kernel = kernel();
    if (identity(kernel)) {
      return in;
    }
    return convolve(src, kernel);
  }

  KernelData kernel() {
    Object value = getParam(P_FILTER);
    if (value instanceof KernelData k) {
      return k;
    }
    return named(String.valueOf(value));
  }

  static KernelData named(String name) {
    if (KernelData.SHARPEN.getName().equalsIgnoreCase(name)) {
      return KernelData.SHARPEN;
    }
    return KernelData.NONE;
  }

  static boolean identity(KernelData kernel) {
    return kernel.getKernelSize() <= 1 || NONE.equals(kernel.getName());
  }

  static BufferedImage convolve(BufferedImage src, KernelData kernel) {
    BufferedImage dst = BrightnessOp.canvas(src);
    int w = src.getWidth();
    int h = src.getHeight();
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        write(src, dst, kernel, x, y);
      }
    }
    return dst;
  }

  static void write(BufferedImage src, BufferedImage dst, KernelData kernel, int x, int y) {
    int value = BrightnessOp.clamp(dot(src, kernel, x, y));
    if (dst.getRaster().getNumBands() == 1) {
      dst.getRaster().setSample(x, y, 0, value);
      return;
    }
    dst.setRGB(x, y, 0xFF000000 | (value << 16) | (value << 8) | value);
  }

  static float dot(BufferedImage src, KernelData kernel, int x, int y) {
    int size = kernel.getKernelSize();
    float[] data = kernel.getData();
    int radius = size / 2;
    float sum = 0f;
    for (int ky = 0; ky < size; ky++) {
      for (int kx = 0; kx < size; kx++) {
        int sx = clamp(x + kx - radius, src.getWidth() - 1);
        int sy = clamp(y + ky - radius, src.getHeight() - 1);
        sum += AutoLevelsOp.luma(src, sx, sy) * data[ky * size + kx];
      }
    }
    return sum;
  }

  static int clamp(int value, int max) {
    if (value < 0) {
      return 0;
    }
    if (value > max) {
      return max;
    }
    return value;
  }
}
