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

/** Voxel-space translation of overlay into the reference FoR (identity when all zeros). */
public class FusionRegistration {

  private final int dx;
  private final int dy;
  private final int dz;

  public FusionRegistration(int dx, int dy, int dz) {
    this.dx = dx;
    this.dy = dy;
    this.dz = dz;
  }

  public static FusionRegistration identity() {
    return new FusionRegistration(0, 0, 0);
  }

  public static FusionRegistration voxelShift(int dx, int dy, int dz) {
    return new FusionRegistration(dx, dy, dz);
  }

  public int dx() {
    return dx;
  }

  public int dy() {
    return dy;
  }

  public int dz() {
    return dz;
  }

  public boolean isIdentity() {
    return dx == 0 && dy == 0 && dz == 0;
  }

  /** Row-major 4×4 affine (voxel translation in the last column). */
  public double[] matrix4() {
    return new double[] {1, 0, 0, dx, 0, 1, 0, dy, 0, 0, 1, dz, 0, 0, 0, 1};
  }
}
