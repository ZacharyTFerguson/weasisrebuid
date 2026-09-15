/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.editor.image;

import org.weasis.core.api.image.op.ByteLutCollection;

/** Named 8-bit RGB LUT used by the 2D display pipeline. */
public class DisplayByteLut {

  private final String name;
  private final byte[][] rgb;

  public DisplayByteLut(String name) {
    this.name = blank(name) ? ByteLutCollection.GRAY : name;
    this.rgb = new ByteLutCollection().getLut(this.name);
  }

  public String getName() {
    return name;
  }

  public byte[][] getLutTable() {
    return rgb;
  }

  public int grayAt(int index) {
    int i = clampIndex(index);
    return rgb[0][i] & 0xFF;
  }

  static boolean blank(String name) {
    return name == null || name.isBlank();
  }

  static int clampIndex(int index) {
    if (index < 0) {
      return 0;
    }
    if (index > 255) {
      return 255;
    }
    return index;
  }
}
