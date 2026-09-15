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

/** Samples an arbitrary plane {@code origin + i·u + j·v}. */
public class ObliqueMpr {

  public double[][] slice(Volume volume, AxesControl axes, int width, int height) {
    if (axes == null) {
      return new double[0][0];
    }
    return slice(volume, axes.origin(), axes.u().xyz(), axes.v().xyz(), width, height);
  }

  public double[][] slice(
      Volume volume, double[] origin, double[] u, double[] v, int width, int height) {
    if (volume == null
        || origin == null
        || u == null
        || v == null
        || origin.length < 3
        || u.length < 3
        || v.length < 3) {
      return new double[0][0];
    }
    int w = Math.max(1, width);
    int h = Math.max(1, height);
    double[][] out = new double[h][w];
    for (int j = 0; j < h; j++) {
      for (int i = 0; i < w; i++) {
        out[j][i] =
            volume.sampleNearest(
                origin[0] + i * u[0] + j * v[0],
                origin[1] + i * u[1] + j * v[1],
                origin[2] + i * u[2] + j * v[2]);
      }
    }
    return out;
  }
}
