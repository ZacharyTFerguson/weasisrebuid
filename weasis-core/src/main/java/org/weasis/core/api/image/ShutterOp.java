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

/** Rectangular shutter; black outside the box. {@code P_ENABLED=false} is a passthrough. */
public class ShutterOp extends AbstractOp {

  public static final String P_ENABLED = "shutter.enabled";
  public static final String P_LEFT = "shutter.left";
  public static final String P_RIGHT = "shutter.right";
  public static final String P_UPPER = "shutter.upper";
  public static final String P_LOWER = "shutter.lower";

  public ShutterOp() {
    super("op.shutter");
    setParam(P_ENABLED, Boolean.FALSE);
  }

  @Override
  protected void processEnabled() {
    setParam(OUTPUT_IMG, shutterIfNeeded(getParam(INPUT_IMG)));
  }

  Object shutterIfNeeded(Object in) {
    if (!(in instanceof BufferedImage src) || !enabled()) {
      return in;
    }
    return apply(src);
  }

  boolean enabled() {
    Object value = getParam(P_ENABLED);
    return Boolean.TRUE.equals(value) || "true".equalsIgnoreCase(String.valueOf(value));
  }

  BufferedImage apply(BufferedImage src) {
    int left = (int) WindowOp.number(getParam(P_LEFT), 0.0);
    int right = edge(P_RIGHT, src.getWidth() - 1);
    int upper = (int) WindowOp.number(getParam(P_UPPER), 0.0);
    int lower = edge(P_LOWER, src.getHeight() - 1);
    return blacken(src, left, right, upper, lower);
  }

  int edge(String key, int fallback) {
    Object value = getParam(key);
    if (value == null) {
      return fallback;
    }
    return (int) WindowOp.number(value, fallback);
  }

  static BufferedImage blacken(BufferedImage src, int left, int right, int upper, int lower) {
    BufferedImage dst = BrightnessOp.canvas(src);
    int w = src.getWidth();
    int h = src.getHeight();
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        write(src, dst, x, y, left, right, upper, lower);
      }
    }
    return dst;
  }

  static void write(
      BufferedImage src,
      BufferedImage dst,
      int x,
      int y,
      int left,
      int right,
      int upper,
      int lower) {
    if (outside(x, y, left, right, upper, lower)) {
      MaskOp.blacken(dst, x, y);
      return;
    }
    MaskOp.copyPixel(src, dst, x, y);
  }

  static boolean outside(int x, int y, int left, int right, int upper, int lower) {
    return x < left || x > right || y < upper || y > lower;
  }
}
