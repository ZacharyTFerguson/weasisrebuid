/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.mpr;

/**
 * Dense sample grid in isotropic slice space. Geometry lives on {@link MprVolume}; this type is the
 * voxel payload CHECKLIST names {@code Volume}.
 */
public final class Volume {

  private final int size;
  private final double[] voxels;

  public Volume(int size) {
    this.size = Math.max(1, size);
    this.voxels = new double[this.size * this.size * this.size];
  }

  public int size() {
    return size;
  }

  public void set(int x, int y, int z, double value) {
    voxels[index(x, y, z)] = value;
  }

  public double get(int x, int y, int z) {
    if (x < 0 || y < 0 || z < 0 || x >= size || y >= size || z >= size) {
      return Double.NaN;
    }
    return voxels[index(x, y, z)];
  }

  public double sample(double x, double y, double z) {
    return GantryTilt.trilinear(this, x, y, z);
  }

  private int index(int x, int y, int z) {
    return (z * size + y) * size + x;
  }
}
