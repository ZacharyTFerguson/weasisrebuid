/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.operations.impl;

import java.awt.image.BufferedImage;
import org.weasis.acquire.AcquireObject;
import org.weasis.acquire.explorer.AcquireImageValues;
import org.weasis.acquire.operations.OpValueChanged;

/**
 * Stretches brightness/contrast from the image histogram onto {@link AcquireImageValues} so {@code
 * ContrastAction} can apply the window.
 */
public class AutoLevelListener extends AcquireObject implements OpValueChanged {

  @Override
  public void apply() {
    stretch(getImage(), getImageValues());
  }

  public void stretch(BufferedImage src, AcquireImageValues values) {
    if (src == null || values == null) {
      return;
    }
    int min = 255;
    int max = 0;
    int width = src.getWidth();
    int height = src.getHeight();
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int luma = luma(src.getRGB(x, y));
        if (luma < min) {
          min = luma;
        }
        if (luma > max) {
          max = luma;
        }
      }
    }
    if (max <= min) {
      values.setContrast(1.0f);
      values.setBrightness(0f);
      return;
    }
    float contrast = 255.0f / (max - min);
    values.setContrast(contrast);
    values.setBrightness(-min * contrast);
  }

  static int luma(int rgb) {
    int r = (rgb >> 16) & 0xFF;
    int g = (rgb >> 8) & 0xFF;
    int b = rgb & 0xFF;
    return (r * 299 + g * 587 + b * 114) / 1000;
  }
}
