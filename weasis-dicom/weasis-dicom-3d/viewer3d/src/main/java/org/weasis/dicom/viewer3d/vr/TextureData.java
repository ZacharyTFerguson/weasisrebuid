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

import java.nio.ShortBuffer;

public class TextureData {

  private final int width;
  private final int height;
  private final int depth;
  private final short[] voxels;

  public TextureData(int width, int height, int depth) {
    if (width < 1 || height < 1 || depth < 1) {
      throw new IllegalArgumentException("volume size");
    }
    this.width = width;
    this.height = height;
    this.depth = depth;
    this.voxels = new short[width * height * depth];
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public int getDepth() {
    return depth;
  }

  public int index(int x, int y, int z) {
    return x + width * (y + height * z);
  }

  public void setVoxel(int x, int y, int z, short value) {
    voxels[index(x, y, z)] = value;
  }

  public short getVoxel(int x, int y, int z) {
    return voxels[index(x, y, z)];
  }

  public ShortBuffer asBuffer() {
    return ShortBuffer.wrap(voxels);
  }

  public int size() {
    return voxels.length;
  }
}
