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
 * Histogram stretch onto a full 8-bit window: {@code contrast=255/(max-min)}, {@code
 * brightness=-min*contrast}. A flat histogram is a passthrough.
 */
public class AutoLevelsOp extends AbstractOp {

  public AutoLevelsOp() {
    super("op.autolevels");
  }

  @Override
  protected void processEnabled() {
    setParam(OUTPUT_IMG, stretchIfNeeded(getParam(INPUT_IMG)));
  }

  Object stretchIfNeeded(Object in) {
    if (!(in instanceof BufferedImage src)) {
      return in;
    }
    int[] range = extrema(src);
    if (range[1] <= range[0]) {
      return in;
    }
    return stretch(src, range[0], range[1]);
  }

  static BufferedImage stretch(BufferedImage src, int min, int max) {
    float contrast = 255.0f / (max - min);
    BrightnessOp window = new BrightnessOp();
    window.setParam(BrightnessOp.P_CONTRAST, contrast);
    window.setParam(BrightnessOp.P_BRIGHTNESS, -min * contrast);
    window.setParam(INPUT_IMG, src);
    window.processEnabled();
    Object out = window.getParam(OUTPUT_IMG);
    return out instanceof BufferedImage image ? image : src;
  }

  static int[] extrema(BufferedImage src) {
    int min = 255;
    int max = 0;
    int w = src.getWidth();
    int h = src.getHeight();
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        int value = luma(src, x, y);
        min = Math.min(min, value);
        max = Math.max(max, value);
      }
    }
    return new int[] {min, max};
  }

  static int luma(BufferedImage src, int x, int y) {
    if (src.getRaster().getNumBands() == 1) {
      return src.getRaster().getSample(x, y, 0);
    }
    int rgb = src.getRGB(x, y);
    int r = (rgb >> 16) & 0xFF;
    int g = (rgb >> 8) & 0xFF;
    int b = rgb & 0xFF;
    return (r * 299 + g * 587 + b * 114) / 1000;
  }
}
