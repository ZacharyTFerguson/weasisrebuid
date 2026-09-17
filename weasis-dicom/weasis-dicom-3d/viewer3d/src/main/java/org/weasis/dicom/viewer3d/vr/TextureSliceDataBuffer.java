/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer3d.vr;

public class TextureSliceDataBuffer {

  private final short[] slice;

  public TextureSliceDataBuffer(int width, int height) {
    this.slice = new short[width * height];
  }

  public short[] getSlice() {
    return slice;
  }

  public void copyInto(TextureData volume, int z) {
    int width = volume.getWidth();
    int height = volume.getHeight();
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        volume.setVoxel(x, y, z, slice[x + y * width]);
      }
    }
  }
}
