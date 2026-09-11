/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.graphic.imp.seg;

/** Transparency ramp used by fusion / SEG (WP-8 / WP-10). */
public class ByteLutAlpha {

  private final byte[] lut;

  public ByteLutAlpha(byte[] lut) {
    this.lut = lut == null ? new byte[256] : lut.clone();
  }

  public byte[] getLut() {
    return lut.clone();
  }

  public int alphaAt(int index) {
    int i = Math.max(0, Math.min(255, index));
    return lut[i] & 0xFF;
  }

  /** Transparent below ~1% of the LUT, opaque from ~10% (fusion overlay ramp, one native call). */
  public static ByteLutAlpha fusionRamp() {
    byte[] lut = new byte[256];
    for (int i = 0; i < 256; i++) {
      double t = i / 255.0;
      if (t < 0.01) {
        lut[i] = 0;
      } else if (t >= 0.10) {
        lut[i] = (byte) 255;
      } else {
        lut[i] = (byte) Math.round(255.0 * (t - 0.01) / 0.09);
      }
    }
    return new ByteLutAlpha(lut);
  }
}
