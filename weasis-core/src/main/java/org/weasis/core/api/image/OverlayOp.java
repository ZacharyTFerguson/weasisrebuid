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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/** Overlay (60xx) bits OR'd onto the 8-bit grey image. Missing bits is a passthrough. */
public class OverlayOp extends AbstractOp {

  public static final String P_ENABLED = "overlay.enabled";
  public static final String P_BITS = "overlay.bits";

  public OverlayOp() {
    super("op.overlay");
    setParam(P_ENABLED, Boolean.TRUE);
  }

  @Override
  protected void processEnabled() {
    setParam(OUTPUT_IMG, overlayIfNeeded(getParam(INPUT_IMG)));
  }

  Object overlayIfNeeded(Object in) {
    if (!(in instanceof BufferedImage src) || !enabled()) {
      return in;
    }
    byte[] bits = bits();
    if (bits == null || bits.length == 0) {
      return in;
    }
    return orBits(src, bits);
  }

  boolean enabled() {
    Object value = getParam(P_ENABLED);
    return !Boolean.FALSE.equals(value) && !"false".equalsIgnoreCase(String.valueOf(value));
  }

  byte[] bits() {
    Object value = getParam(P_BITS);
    return value instanceof byte[] b ? b : null;
  }

  static BufferedImage orBits(BufferedImage src, byte[] bits) {
    BufferedImage dst = copy(src);
    int w = src.getWidth();
    int h = src.getHeight();
    int n = Math.min(w * h, bits.length * 8);
    for (int i = 0; i < n; i++) {
      orBit(dst, bits, i, i % w, i / w);
    }
    return dst;
  }

  static void orBit(BufferedImage dst, byte[] bits, int i, int x, int y) {
    int bit = (bits[i / 8] >> (i % 8)) & 1;
    if (bit == 0) {
      return;
    }
    if (dst.getRaster().getNumBands() == 1) {
      dst.getRaster().setSample(x, y, 0, 255);
      return;
    }
    dst.setRGB(x, y, 0xFFFFFFFF);
  }

  static BufferedImage copy(BufferedImage src) {
    BufferedImage dst = BrightnessOp.canvas(src);
    Graphics2D g = dst.createGraphics();
    try {
      g.drawImage(src, 0, 0, null);
    } finally {
      g.dispose();
    }
    return dst;
  }
}
