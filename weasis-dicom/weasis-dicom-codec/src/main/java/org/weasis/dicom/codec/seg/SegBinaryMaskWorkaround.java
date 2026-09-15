/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package org.weasis.dicom.codec.seg;

public final class SegBinaryMaskWorkaround {
  private SegBinaryMaskWorkaround() {}

  public static byte[] coerceBinary(byte[] src) {
    if (src == null) {
      return new byte[0];
    }
    byte[] out = new byte[src.length];
    for (int i = 0; i < src.length; i++) {
      out[i] = (byte) ((src[i] & 0xFF) == 0 ? 0 : 1);
    }
    return out;
  }
}
