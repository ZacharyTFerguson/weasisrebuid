/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.fusion;

import java.awt.Color;
import java.util.List;

/** Named 8-bit RGB LUTs used by PET overlay (Gray, Inverse, Hot Iron, PET). */
public class FusionColorScale {

  public static final String GRAY = "Gray";
  public static final String INVERSE = "Inverse";
  public static final String HOT_IRON = "Hot Iron";
  public static final String PET = "PET";

  public List<String> names() {
    return List.of(GRAY, INVERSE, HOT_IRON, PET);
  }

  public byte[][] rgb(String name) {
    byte[][] rgb = new byte[3][256];
    String lut = name == null ? GRAY : name;
    for (int i = 0; i < 256; i++) {
      int[] c = rgbAt(lut, i);
      rgb[0][i] = (byte) c[0];
      rgb[1][i] = (byte) c[1];
      rgb[2][i] = (byte) c[2];
    }
    return rgb;
  }

  public Color color(String name, int index) {
    int i = Math.max(0, Math.min(255, index));
    int[] c = rgbAt(name == null ? GRAY : name, i);
    return new Color(c[0], c[1], c[2]);
  }

  static int[] rgbAt(String name, int i) {
    if (INVERSE.equals(name)) {
      int v = 255 - i;
      return new int[] {v, v, v};
    }
    if (HOT_IRON.equals(name)) {
      return hotIron(i);
    }
    if (PET.equals(name)) {
      return pet(i);
    }
    return new int[] {i, i, i};
  }

  /** Black → red → yellow → white. */
  static int[] hotIron(int i) {
    int r = Math.min(255, i * 3);
    int g = i < 85 ? 0 : Math.min(255, (i - 85) * 3);
    int b = i < 170 ? 0 : Math.min(255, (i - 170) * 3);
    return new int[] {r, g, b};
  }

  /** Black → blue → red → yellow. */
  static int[] pet(int i) {
    int r = Math.min(255, i * 2);
    int g = i < 128 ? 0 : Math.min(255, (i - 128) * 2);
    int b = Math.max(0, 255 - i * 2);
    return new int[] {r, g, b};
  }
}
