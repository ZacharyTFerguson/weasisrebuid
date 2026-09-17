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

/** Quarter rotation (Alt+R/L, toolbar 0/90/180/270). {@code P_ROTATION=0} is a passthrough. */
public class RotationOp extends AbstractOp {

  public static final String P_ROTATION = AffineTransformOp.P_ROTATION;

  public RotationOp() {
    super("op.rotation");
    setParam(P_ROTATION, 0.0);
  }

  @Override
  protected void processEnabled() {
    setParam(OUTPUT_IMG, rotateIfNeeded(getParam(INPUT_IMG)));
  }

  Object rotateIfNeeded(Object in) {
    if (!(in instanceof BufferedImage src)) {
      return in;
    }
    int quarter = quarter();
    if (quarter == 0) {
      return in;
    }
    return rotate(src, quarter);
  }

  int quarter() {
    int turns = (int) Math.round(WindowOp.number(getParam(P_ROTATION), 0.0) / 90.0) % 4;
    return turns < 0 ? turns + 4 : turns;
  }

  static BufferedImage rotate(BufferedImage src, int quarter) {
    if (quarter == 1) {
      return rotate90(src);
    }
    if (quarter == 2) {
      return rotate180(src);
    }
    return rotate270(src);
  }

  static BufferedImage rotate90(BufferedImage src) {
    int w = src.getWidth();
    int h = src.getHeight();
    BufferedImage dst = canvas(src, h, w);
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        dst.setRGB(h - 1 - y, x, src.getRGB(x, y));
      }
    }
    return dst;
  }

  static BufferedImage rotate180(BufferedImage src) {
    int w = src.getWidth();
    int h = src.getHeight();
    BufferedImage dst = canvas(src, w, h);
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        dst.setRGB(w - 1 - x, h - 1 - y, src.getRGB(x, y));
      }
    }
    return dst;
  }

  static BufferedImage rotate270(BufferedImage src) {
    int w = src.getWidth();
    int h = src.getHeight();
    BufferedImage dst = canvas(src, h, w);
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        dst.setRGB(y, w - 1 - x, src.getRGB(x, y));
      }
    }
    return dst;
  }

  static BufferedImage canvas(BufferedImage src, int width, int height) {
    int type =
        src.getType() == BufferedImage.TYPE_CUSTOM ? BufferedImage.TYPE_INT_ARGB : src.getType();
    return new BufferedImage(width, height, type);
  }
}
