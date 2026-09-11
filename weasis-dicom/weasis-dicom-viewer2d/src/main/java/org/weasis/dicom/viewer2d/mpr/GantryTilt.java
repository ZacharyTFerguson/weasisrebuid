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
 * Oblique MPR with gantry-tilt correction: backward-map the output sample into volume space, then
 * trilinear interpolate (ARCHITECTURE §6.4 / SRS §4.5).
 */
public final class GantryTilt {

  private GantryTilt() {}

  public static double[] backwardMap(double x, double y, double z, double tiltDeg) {
    double rad = Math.toRadians(tiltDeg);
    double cos = Math.cos(rad);
    double sin = Math.sin(rad);
    return new double[] {x, y * cos - z * sin, y * sin + z * cos};
  }

  public static double trilinear(Volume volume, double x, double y, double z) {
    int n = volume.size();
    if (x < 0 || y < 0 || z < 0 || x > n - 1 || y > n - 1 || z > n - 1) {
      return Double.NaN;
    }
    int x0 = (int) Math.floor(x);
    int y0 = (int) Math.floor(y);
    int z0 = (int) Math.floor(z);
    int x1 = Math.min(x0 + 1, n - 1);
    int y1 = Math.min(y0 + 1, n - 1);
    int z1 = Math.min(z0 + 1, n - 1);
    double xd = x - x0;
    double yd = y - y0;
    double zd = z - z0;
    double c000 = volume.get(x0, y0, z0);
    double c100 = volume.get(x1, y0, z0);
    double c010 = volume.get(x0, y1, z0);
    double c110 = volume.get(x1, y1, z0);
    double c001 = volume.get(x0, y0, z1);
    double c101 = volume.get(x1, y0, z1);
    double c011 = volume.get(x0, y1, z1);
    double c111 = volume.get(x1, y1, z1);
    double c00 = lerp(c000, c100, xd);
    double c10 = lerp(c010, c110, xd);
    double c01 = lerp(c001, c101, xd);
    double c11 = lerp(c011, c111, xd);
    double c0 = lerp(c00, c10, yd);
    double c1 = lerp(c01, c11, yd);
    return lerp(c0, c1, zd);
  }

  static double lerp(double a, double b, double t) {
    return a + (b - a) * t;
  }
}
