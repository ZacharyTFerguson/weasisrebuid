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

/** Min/max auto-window over a PET overlay volume. */
public class FusionWindowEstimator {

  public FusionWindow estimate(Volume volume) {
    if (volume == null) {
      return new FusionWindow(1, 0);
    }
    double min = Double.POSITIVE_INFINITY;
    double max = Double.NEGATIVE_INFINITY;
    for (int z = 0; z < volume.sizeZ(); z++) {
      for (int y = 0; y < volume.sizeY(); y++) {
        for (int x = 0; x < volume.sizeX(); x++) {
          double v = volume.value(x, y, z);
          min = Math.min(min, v);
          max = Math.max(max, v);
        }
      }
    }
    if (min > max) {
      return new FusionWindow(1, 0);
    }
    double window = Math.max(1.0, max - min);
    return new FusionWindow(window, min + window / 2.0);
  }
}
