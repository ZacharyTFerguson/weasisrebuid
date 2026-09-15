/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.mpr.cmpr;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import org.weasis.dicom.viewer2d.mpr.Volume;

/** Samples a volume along a curve (CPR). */
public class CurvedMprBuilder {

  public double[] sample(Volume volume, List<Point2D.Double> curve, int z) {
    if (volume == null || curve == null || curve.isEmpty()) {
      return new double[0];
    }
    List<Double> values = new ArrayList<>();
    for (Point2D.Double p : curve) {
      values.add(volume.value((int) Math.round(p.x), (int) Math.round(p.y), z));
    }
    double[] out = new double[values.size()];
    for (int i = 0; i < values.size(); i++) {
      out[i] = values.get(i);
    }
    return out;
  }

  /**
   * Straightened CPR: rows follow arc-length along {@code curve}; columns sample the in-plane
   * perpendicular at ±{@code halfWidth} voxels (nearest neighbor).
   */
  public double[][] build(Volume volume, List<Point2D.Double> curve, int z, int halfWidth) {
    if (volume == null || curve == null || curve.isEmpty()) {
      return new double[0][0];
    }
    List<Point2D.Double> samples = new CurveSampler().resample(curve, 1.0);
    int hw = Math.max(0, halfWidth);
    int cols = 2 * hw + 1;
    double[][] out = new double[samples.size()][cols];
    for (int r = 0; r < samples.size(); r++) {
      Point2D.Double tan = tangentAt(samples, r);
      double nx = -tan.y;
      double ny = tan.x;
      Point2D.Double p = samples.get(r);
      for (int c = 0; c < cols; c++) {
        int d = c - hw;
        out[r][c] = volume.sampleNearest(p.x + d * nx, p.y + d * ny, z);
      }
    }
    return out;
  }

  static Point2D.Double tangentAt(List<Point2D.Double> samples, int index) {
    Point2D.Double a = samples.get(Math.max(0, index - 1));
    Point2D.Double b = samples.get(Math.min(samples.size() - 1, index + 1));
    double tx = b.x - a.x;
    double ty = b.y - a.y;
    double n = Math.hypot(tx, ty);
    if (n <= 1e-12) {
      return new Point2D.Double(1, 0);
    }
    return new Point2D.Double(tx / n, ty / n);
  }
}
