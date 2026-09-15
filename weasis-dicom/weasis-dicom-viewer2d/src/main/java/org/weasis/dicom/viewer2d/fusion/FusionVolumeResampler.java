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

import org.weasis.dicom.viewer2d.mpr.Volume;
import org.weasis.dicom.viewer2d.mpr.VolumeShort;

/** Resamples overlay onto a reference grid by nearest IPP Z and optional voxel shift. */
public class FusionVolumeResampler {

  public VolumeShort onto(
      FusionStack overlay, FusionStack reference, FusionRegistration registration) {
    if (overlay == null || overlay.isEmpty() || reference == null || reference.isEmpty()) {
      return new VolumeShort(1, 1, 1);
    }
    Volume ref = reference.volume();
    return onto(
        overlay.volume(), overlay.zMm(), ref.sizeX(), ref.sizeY(), reference.zMm(), registration);
  }

  public VolumeShort onto(
      Volume overlay,
      double[] overlayZ,
      int sizeX,
      int sizeY,
      double[] referenceZ,
      FusionRegistration registration) {
    int sx = Math.max(1, sizeX);
    int sy = Math.max(1, sizeY);
    int sz = referenceZ == null ? 0 : referenceZ.length;
    if (sz == 0) {
      return new VolumeShort(sx, sy, 1);
    }
    VolumeShort out = new VolumeShort(sx, sy, sz);
    if (overlay == null) {
      return out;
    }
    FusionRegistration shift = registration == null ? FusionRegistration.identity() : registration;
    FusionSliceMatcher matcher = new FusionSliceMatcher();
    for (int z = 0; z < sz; z++) {
      int oz = matcher.nearestIndex(overlayZ, referenceZ[z]);
      if (oz < 0) {
        continue;
      }
      for (int y = 0; y < sy; y++) {
        for (int x = 0; x < sx; x++) {
          double ox = scale(x, sx, overlay.sizeX()) - shift.dx();
          double oy = scale(y, sy, overlay.sizeY()) - shift.dy();
          out.setValue(x, y, z, overlay.sampleNearest(ox, oy, oz - shift.dz()));
        }
      }
    }
    return out;
  }

  static double scale(int i, int dst, int src) {
    if (dst <= 1 || src <= 1) {
      return 0;
    }
    return i * (src - 1.0) / (dst - 1.0);
  }
}
