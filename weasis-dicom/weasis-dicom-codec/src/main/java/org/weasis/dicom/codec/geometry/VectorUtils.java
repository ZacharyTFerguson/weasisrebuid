/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package org.weasis.dicom.codec.geometry;

public final class VectorUtils {
  private VectorUtils() {}

  public static double[] cross(double[] a, double[] b) {
    if (a == null || b == null || a.length < 3 || b.length < 3) {
      return new double[] {0, 0, 0};
    }
    return new double[] {
      a[1] * b[2] - a[2] * b[1], a[2] * b[0] - a[0] * b[2], a[0] * b[1] - a[1] * b[0]
    };
  }

  public static double norm(double[] v) {
    if (v == null) {
      return 0;
    }
    double s = 0;
    for (double d : v) {
      s += d * d;
    }
    return Math.sqrt(s);
  }

  public static double[] normalize(double[] v) {
    double n = norm(v);
    if (n == 0 || v == null) {
      return new double[] {0, 0, 0};
    }
    return new double[] {v[0] / n, v[1] / n, v[2] / n};
  }

  public static double dot(double[] a, double[] b) {
    if (a == null || b == null) {
      return 0;
    }
    int n = Math.min(a.length, b.length);
    double s = 0;
    for (int i = 0; i < n; i++) {
      s += a[i] * b[i];
    }
    return s;
  }
}
