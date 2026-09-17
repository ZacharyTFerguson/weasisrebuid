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
 * Photo-editor contrast/brightness rescale ({@code out = in * contrast + brightness}). Identity
 * {@code contrast=1, brightness=0} is a passthrough.
 */
public class BrightnessOp extends AbstractOp {

  public static final String P_CONTRAST = "contrast";
  public static final String P_BRIGHTNESS = "brightness";

  public BrightnessOp() {
    super("op.brightness");
    setParam(P_CONTRAST, 1.0);
    setParam(P_BRIGHTNESS, 0.0);
  }

  @Override
  protected void processEnabled() {
    setParam(OUTPUT_IMG, rescaleIfNeeded(getParam(INPUT_IMG)));
  }

  Object rescaleIfNeeded(Object in) {
    if (!(in instanceof BufferedImage src) || identity()) {
      return in;
    }
    return rescale(src, contrast(), brightness());
  }

  boolean identity() {
    return Math.abs(contrast() - 1.0) < 1e-6 && Math.abs(brightness()) < 1e-6;
  }

  float contrast() {
    return (float) WindowOp.number(getParam(P_CONTRAST), 1.0);
  }

  float brightness() {
    return (float) WindowOp.number(getParam(P_BRIGHTNESS), 0.0);
  }

  static BufferedImage rescale(BufferedImage src, float contrast, float brightness) {
    BufferedImage dst = canvas(src);
    if (src.getRaster().getNumBands() == 1 && dst.getRaster().getNumBands() == 1) {
      fillGray(src, dst, contrast, brightness);
    } else {
      fillRgb(src, dst, contrast, brightness);
    }
    return dst;
  }

  static void fillGray(BufferedImage src, BufferedImage dst, float contrast, float brightness) {
    int w = src.getWidth();
    int h = src.getHeight();
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        int sample = src.getRaster().getSample(x, y, 0);
        dst.getRaster().setSample(x, y, 0, clamp(sample * contrast + brightness));
      }
    }
  }

  static void fillRgb(BufferedImage src, BufferedImage dst, float contrast, float brightness) {
    int w = src.getWidth();
    int h = src.getHeight();
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        dst.setRGB(x, y, scaleRgb(src.getRGB(x, y), contrast, brightness));
      }
    }
  }

  static int scaleRgb(int rgb, float contrast, float brightness) {
    int r = clamp(((rgb >> 16) & 0xFF) * contrast + brightness);
    int g = clamp(((rgb >> 8) & 0xFF) * contrast + brightness);
    int b = clamp((rgb & 0xFF) * contrast + brightness);
    return 0xFF000000 | (r << 16) | (g << 8) | b;
  }

  static int clamp(float value) {
    int rounded = Math.round(value);
    if (rounded < 0) {
      return 0;
    }
    if (rounded > 255) {
      return 255;
    }
    return rounded;
  }

  static BufferedImage canvas(BufferedImage src) {
    int type =
        src.getType() == BufferedImage.TYPE_CUSTOM ? BufferedImage.TYPE_INT_ARGB : src.getType();
    return new BufferedImage(src.getWidth(), src.getHeight(), type);
  }
}
