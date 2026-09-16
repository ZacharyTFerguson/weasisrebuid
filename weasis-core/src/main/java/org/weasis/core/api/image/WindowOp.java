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
import java.awt.image.DataBufferByte;
import org.weasis.core.api.image.util.WindLevelParameters;

/** VOI window/level (linear or SIGMOID) onto a {@link BufferedImage}. */
public class WindowOp extends AbstractOp {

  public static final String P_WINDOW = "window";
  public static final String P_LEVEL = "level";
  public static final String P_VOI_LUT_SHAPE = "voi.shape";

  public WindowOp() {
    super("op.window");
  }

  @Override
  protected void processEnabled() {
    setParam(OUTPUT_IMG, apply(getParam(INPUT_IMG)));
  }

  Object apply(Object in) {
    if (!(in instanceof BufferedImage src) || !hasWindowOrLevel()) {
      return in;
    }
    return remap(src, parameters());
  }

  boolean hasWindowOrLevel() {
    return getParam(P_WINDOW) != null || getParam(P_LEVEL) != null;
  }

  WindLevelParameters parameters() {
    WindLevelParameters voi =
        new WindLevelParameters(
            number(getParam(P_WINDOW), 255.0), number(getParam(P_LEVEL), 127.5));
    Object shape = getParam(P_VOI_LUT_SHAPE);
    if (shape != null) {
      voi.setLutShape(String.valueOf(shape));
    }
    return voi;
  }

  static BufferedImage remap(BufferedImage src, WindLevelParameters voi) {
    int w = src.getWidth();
    int h = src.getHeight();
    BufferedImage dst = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
    byte[] out = ((DataBufferByte) dst.getRaster().getDataBuffer()).getData();
    fillGray(src, out, voi == null ? new WindLevelParameters(255, 127.5) : voi);
    return dst;
  }

  static void fillGray(BufferedImage src, byte[] out, WindLevelParameters voi) {
    int w = src.getWidth();
    for (int i = 0; i < out.length; i++) {
      out[i] = (byte) voi.toDisplay8(sample(src, i % w, i / w));
    }
  }

  static double sample(BufferedImage src, int x, int y) {
    if (src.getRaster().getNumBands() == 1) {
      return src.getRaster().getSample(x, y, 0);
    }
    return (src.getRGB(x, y) >> 16) & 0xFF;
  }

  static double number(Object value, double fallback) {
    if (value instanceof Number n) {
      return n.doubleValue();
    }
    if (!(value instanceof String s)) {
      return fallback;
    }
    try {
      return Double.parseDouble(s);
    } catch (NumberFormatException e) {
      return fallback;
    }
  }
}
