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

/** Unit-capable 3-vector for oblique MPR plane axes. */
public class AxisDirection {

  private final double x;
  private final double y;
  private final double z;

  public AxisDirection(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public double x() {
    return x;
  }

  public double y() {
    return y;
  }

  public double z() {
    return z;
  }

  public double[] xyz() {
    return new double[] {x, y, z};
  }

  public double length() {
    return Math.hypot(Math.hypot(x, y), z);
  }

  public AxisDirection normalized() {
    double n = length();
    if (n <= 1e-12) {
      return new AxisDirection(1, 0, 0);
    }
    return new AxisDirection(x / n, y / n, z / n);
  }

  public double dot(AxisDirection other) {
    if (other == null) {
      return 0;
    }
    return x * other.x + y * other.y + z * other.z;
  }

  public AxisDirection cross(AxisDirection other) {
    if (other == null) {
      return new AxisDirection(0, 0, 0);
    }
    return new AxisDirection(
        y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x);
  }
}
