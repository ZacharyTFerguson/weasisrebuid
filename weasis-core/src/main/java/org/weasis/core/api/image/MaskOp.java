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

/**
 * Apply {@code P_MASK}: luma 0 blackens the source pixel (SEG/shutter-style hide). Missing mask is
 * a passthrough. Output is a copy.
 */
public class MaskOp extends AbstractOp {

  public static final String P_MASK = "mask";

  public MaskOp() {
    super("op.mask");
  }

  @Override
  protected void processEnabled() {
    setParam(OUTPUT_IMG, maskIfNeeded(getParam(INPUT_IMG)));
  }

  Object maskIfNeeded(Object in) {
    if (!(in instanceof BufferedImage src)) {
      return in;
    }
    Object value = getParam(P_MASK);
    if (!(value instanceof BufferedImage mask)) {
      return in;
    }
    return apply(src, mask);
  }

  static BufferedImage apply(BufferedImage src, BufferedImage mask) {
    BufferedImage dst = BrightnessOp.canvas(src);
    int w = src.getWidth();
    int h = src.getHeight();
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        write(src, dst, mask, x, y);
      }
    }
    return dst;
  }

  static void write(BufferedImage src, BufferedImage dst, BufferedImage mask, int x, int y) {
    if (hidden(mask, x, y)) {
      blacken(dst, x, y);
      return;
    }
    copyPixel(src, dst, x, y);
  }

  static boolean hidden(BufferedImage mask, int x, int y) {
    if (x >= mask.getWidth() || y >= mask.getHeight()) {
      return false;
    }
    return AutoLevelsOp.luma(mask, x, y) == 0;
  }

  static void blacken(BufferedImage dst, int x, int y) {
    if (dst.getRaster().getNumBands() == 1) {
      dst.getRaster().setSample(x, y, 0, 0);
      return;
    }
    dst.setRGB(x, y, 0xFF000000);
  }

  static void copyPixel(BufferedImage src, BufferedImage dst, int x, int y) {
    if (src.getRaster().getNumBands() == 1 && dst.getRaster().getNumBands() == 1) {
      dst.getRaster().setSample(x, y, 0, src.getRaster().getSample(x, y, 0));
      return;
    }
    dst.setRGB(x, y, src.getRGB(x, y));
  }
}
