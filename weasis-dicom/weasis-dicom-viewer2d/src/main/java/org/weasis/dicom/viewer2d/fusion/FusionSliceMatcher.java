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

/** Nearest ImagePositionPatient Z (mm) pairing between reference and overlay stacks. */
public class FusionSliceMatcher {

  public int nearestIndex(double[] zs, double z) {
    return nearestIndex(zs, z, Double.POSITIVE_INFINITY);
  }

  public int nearestIndex(double[] zs, double z, double maxDeltaMm) {
    if (zs == null || zs.length == 0 || Double.isNaN(z)) {
      return -1;
    }
    int best = -1;
    double bestD = Double.POSITIVE_INFINITY;
    for (int i = 0; i < zs.length; i++) {
      double d = Math.abs(zs[i] - z);
      if (d < bestD) {
        bestD = d;
        best = i;
      }
    }
    if (best >= 0 && bestD > maxDeltaMm + 1e-9) {
      return -1;
    }
    return best;
  }

  public int[] matchAll(double[] referenceZ, double[] overlayZ, double maxDeltaMm) {
    if (referenceZ == null) {
      return new int[0];
    }
    int[] out = new int[referenceZ.length];
    for (int i = 0; i < referenceZ.length; i++) {
      out[i] = nearestIndex(overlayZ, referenceZ[i], maxDeltaMm);
    }
    return out;
  }
}
