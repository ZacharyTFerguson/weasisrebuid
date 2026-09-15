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
 * Shoemake arc-ball: drag on the unit sphere rotates the oblique MPR plane axes {@code u} and
 * {@code v}.
 */
public class ArcBallController {

  private AxisDirection u = new AxisDirection(1, 0, 0);
  private AxisDirection v = new AxisDirection(0, 1, 0);
  private double[] last;

  public void begin(double nx, double ny) {
    last = project(nx, ny);
  }

  public void drag(double nx, double ny) {
    double[] cur = project(nx, ny);
    if (last == null) {
      last = cur;
      return;
    }
    rotateFromTo(last, cur);
    last = cur;
  }

  public void end() {
    last = null;
  }

  public void fromPixel(int x, int y, int width, int height) {
    double nx = width <= 0 ? 0 : 2.0 * x / width - 1.0;
    double ny = height <= 0 ? 0 : 1.0 - 2.0 * y / height;
    if (last == null) {
      begin(nx, ny);
    } else {
      drag(nx, ny);
    }
  }

  public AxisDirection u() {
    return u;
  }

  public AxisDirection v() {
    return v;
  }

  public AxesControl axes() {
    AxesControl axes = new AxesControl();
    axes.setU(u);
    axes.setV(v);
    return axes;
  }

  static double[] project(double nx, double ny) {
    double x = nx;
    double y = ny;
    double d2 = x * x + y * y;
    if (d2 > 1) {
      double n = Math.sqrt(d2);
      return new double[] {x / n, y / n, 0};
    }
    return new double[] {x, y, Math.sqrt(1 - d2)};
  }

  void rotateFromTo(double[] from, double[] to) {
    AxisDirection a = new AxisDirection(from[0], from[1], from[2]);
    AxisDirection b = new AxisDirection(to[0], to[1], to[2]);
    AxisDirection axis = a.cross(b);
    if (axis.length() <= 1e-12) {
      return;
    }
    double dot = Math.max(-1, Math.min(1, a.normalized().dot(b.normalized())));
    double angle = Math.acos(dot);
    AxisDirection k = axis.normalized();
    u = rotate(u, k, angle);
    v = rotate(v, k, angle);
  }

  static AxisDirection rotate(AxisDirection vec, AxisDirection k, double angle) {
    double c = Math.cos(angle);
    double s = Math.sin(angle);
    AxisDirection kxv = k.cross(vec);
    double kdv = k.dot(vec);
    double o = 1 - c;
    return new AxisDirection(
            vec.x() * c + kxv.x() * s + k.x() * kdv * o,
            vec.y() * c + kxv.y() * s + k.y() * kdv * o,
            vec.z() * c + kxv.z() * s + k.z() * kdv * o)
        .normalized();
  }
}
