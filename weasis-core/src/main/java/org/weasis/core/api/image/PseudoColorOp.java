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
import org.weasis.core.api.image.op.ByteLutCollection;

/** Pseudo-color LUT after filter. Gray is identity. Inverse is {@code 255-v}. */
public class PseudoColorOp extends AbstractOp {

  public static final String P_LUT = "lut";
  public static final String P_INVERT = "invert";
  public static final String GRAY = "Gray";

  public PseudoColorOp() {
    super("op.pseudocolor");
    setParam(P_LUT, GRAY);
    setParam(P_INVERT, Boolean.FALSE);
  }

  @Override
  protected void processEnabled() {
    setParam(OUTPUT_IMG, colorIfNeeded(getParam(INPUT_IMG)));
  }

  Object colorIfNeeded(Object in) {
    if (!(in instanceof BufferedImage src) || grayIdentity()) {
      return in;
    }
    if (inverted() && GRAY.equalsIgnoreCase(lutName())) {
      return invertGray(src);
    }
    return applyLut(src, lutName());
  }

  boolean grayIdentity() {
    return !inverted() && GRAY.equalsIgnoreCase(lutName());
  }

  boolean inverted() {
    Object value = getParam(P_INVERT);
    return Boolean.TRUE.equals(value) || "true".equalsIgnoreCase(String.valueOf(value));
  }

  String lutName() {
    Object value = getParam(P_LUT);
    return value == null ? GRAY : String.valueOf(value);
  }

  static BufferedImage invertGray(BufferedImage src) {
    return applyLut(src, ByteLutCollection.INVERSE);
  }

  static BufferedImage applyLut(BufferedImage src, String name) {
    byte[][] rgb = new ByteLutCollection().getLut(name);
    BufferedImage dst = BrightnessOp.canvas(src);
    int w = src.getWidth();
    int h = src.getHeight();
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        write(src, dst, rgb, x, y);
      }
    }
    return dst;
  }

  static void write(BufferedImage src, BufferedImage dst, byte[][] rgb, int x, int y) {
    int i = Math.min(255, Math.max(0, AutoLevelsOp.luma(src, x, y)));
    int r = rgb[0][i] & 0xFF;
    int g = rgb[1][i] & 0xFF;
    int b = rgb[2][i] & 0xFF;
    if (dst.getRaster().getNumBands() == 1) {
      dst.getRaster().setSample(x, y, 0, r);
      return;
    }
    dst.setRGB(x, y, 0xFF000000 | (r << 16) | (g << 8) | b);
  }
}
