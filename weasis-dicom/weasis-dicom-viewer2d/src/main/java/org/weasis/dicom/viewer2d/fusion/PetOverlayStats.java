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

/** Closed ROI on fused PET: Min/Max/Mean from original PET voxels, not the resampled overlay. */
public final class PetOverlayStats {

  private PetOverlayStats() {}

  public static double maxPt(double[] originalPetVoxels) {
    return reduce(originalPetVoxels, Double.NEGATIVE_INFINITY, Math::max);
  }

  public static double minPt(double[] originalPetVoxels) {
    return reduce(originalPetVoxels, Double.POSITIVE_INFINITY, Math::min);
  }

  public static double meanPt(double[] originalPetVoxels) {
    if (originalPetVoxels == null || originalPetVoxels.length == 0) {
      return 0;
    }
    double s = 0;
    for (double v : originalPetVoxels) {
      s += v;
    }
    return s / originalPetVoxels.length;
  }

  private static double reduce(
      double[] values, double seed, java.util.function.DoubleBinaryOperator op) {
    if (values == null || values.length == 0) {
      return 0;
    }
    double m = seed;
    for (double v : values) {
      m = op.applyAsDouble(m, v);
    }
    return m;
  }
}
