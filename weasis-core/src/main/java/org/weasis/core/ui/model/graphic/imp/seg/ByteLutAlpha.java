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

/** Transparency ramp for SEG / fusion overlays (ARCHITECTURE §5.2 / §6.5). */
public final class ByteLutAlpha {

  private final byte[] alpha;

  public ByteLutAlpha(byte[] alpha) {
    this.alpha = alpha == null ? identityBytes() : alpha.clone();
  }

  public static ByteLutAlpha identity() {
    return new ByteLutAlpha(identityBytes());
  }

  public byte[] table() {
    return alpha.clone();
  }

  public int alphaAt(int index) {
    int i = Math.max(0, Math.min(alpha.length - 1, index));
    return alpha[i] & 0xff;
  }

  static byte[] identityBytes() {
    byte[] t = new byte[256];
    for (int i = 0; i < 256; i++) {
      t[i] = (byte) i;
    }
    return t;
  }
}
