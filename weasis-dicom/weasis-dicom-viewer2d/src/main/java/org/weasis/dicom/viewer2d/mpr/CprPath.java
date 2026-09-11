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

import java.util.ArrayList;
import java.util.List;

/** Curved MPR: sample along a polyline path in isotropic space. */
public final class CprPath {

  private final List<double[]> points = new ArrayList<>();

  public void add(double x, double y, double z) {
    points.add(new double[] {x, y, z});
  }

  public int size() {
    return points.size();
  }

  /** True when every vertex shares the same Z (axial curve at a fixed level). */
  public boolean axialFixedZ() {
    if (points.size() < 2) {
      return false;
    }
    double z0 = points.get(0)[2];
    for (double[] p : points) {
      if (Math.abs(p[2] - z0) > 1e-6) {
        return false;
      }
    }
    return true;
  }

  /** Spline-smooth before sampling: Chaikin corner-cutting, ≥ 2 points. */
  public CprPath splineSmoothed() {
    if (points.size() < 2) {
      return this;
    }
    CprPath out = new CprPath();
    out.add(points.get(0)[0], points.get(0)[1], points.get(0)[2]);
    for (int i = 0; i < points.size() - 1; i++) {
      double[] a = points.get(i);
      double[] b = points.get(i + 1);
      out.add(0.75 * a[0] + 0.25 * b[0], 0.75 * a[1] + 0.25 * b[1], 0.75 * a[2] + 0.25 * b[2]);
      out.add(0.25 * a[0] + 0.75 * b[0], 0.25 * a[1] + 0.75 * b[1], 0.25 * a[2] + 0.75 * b[2]);
    }
    double[] last = points.get(points.size() - 1);
    out.add(last[0], last[1], last[2]);
    return out;
  }

  public double length() {
    double sum = 0;
    for (int i = 1; i < points.size(); i++) {
      double[] a = points.get(i - 1);
      double[] b = points.get(i);
      sum += Math.hypot(Math.hypot(b[0] - a[0], b[1] - a[1]), b[2] - a[2]);
    }
    return sum;
  }

  public double[] sample(double t) {
    if (points.isEmpty()) {
      return new double[] {0, 0, 0};
    }
    if (points.size() == 1 || t <= 0) {
      return points.get(0).clone();
    }
    double total = length();
    if (total == 0 || t >= 1) {
      return points.get(points.size() - 1).clone();
    }
    double target = t * total;
    double acc = 0;
    for (int i = 1; i < points.size(); i++) {
      double[] a = points.get(i - 1);
      double[] b = points.get(i);
      double seg = Math.hypot(Math.hypot(b[0] - a[0], b[1] - a[1]), b[2] - a[2]);
      if (acc + seg >= target) {
        double u = seg == 0 ? 0 : (target - acc) / seg;
        return new double[] {
          a[0] + u * (b[0] - a[0]), a[1] + u * (b[1] - a[1]), a[2] + u * (b[2] - a[2])
        };
      }
      acc += seg;
    }
    return points.get(points.size() - 1).clone();
  }
}
