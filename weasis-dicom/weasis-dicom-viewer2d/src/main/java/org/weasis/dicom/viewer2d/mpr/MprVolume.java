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
 * Working space is isotropic slice space; volume center at {@code (halfSlice)³}. Distinguishes
 * volume center vs canvas projection (ARCHITECTURE §6.4).
 */
public final class MprVolume {

  private final int slices;
  private final double[] spacingMm; // row, col, slice
  private double[] volumeCenter;
  private double[] canvasProjection;

  public MprVolume(int slices, double rowMm, double colMm, double sliceMm) {
    this.slices = Math.max(1, slices);
    this.spacingMm = new double[] {rowMm, colMm, sliceMm};
    double half = (this.slices - 1) / 2.0;
    this.volumeCenter = new double[] {half, half, half};
    this.canvasProjection = volumeCenter.clone();
  }

  public int slices() {
    return slices;
  }

  public boolean isotropic() {
    return Math.abs(spacingMm[0] - spacingMm[1]) < 1e-6
        && Math.abs(spacingMm[1] - spacingMm[2]) < 1e-6;
  }

  public double[] volumeCenter() {
    return volumeCenter.clone();
  }

  public double[] canvasProjection() {
    return canvasProjection.clone();
  }

  public void setVolumeCenter(double x, double y, double z) {
    volumeCenter = new double[] {x, y, z};
  }

  public void setCanvasProjection(double x, double y, double z) {
    canvasProjection = new double[] {x, y, z};
  }

  public double[] toIsotropic(double[] voxel) {
    return new double[] {voxel[0] * spacingMm[0], voxel[1] * spacingMm[1], voxel[2] * spacingMm[2]};
  }
}
