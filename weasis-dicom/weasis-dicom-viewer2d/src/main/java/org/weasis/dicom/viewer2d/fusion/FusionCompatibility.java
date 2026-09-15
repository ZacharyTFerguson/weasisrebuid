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

/** Same Frame of Reference UID and overlapping IPP Z range. */
public class FusionCompatibility {

  public boolean sameFrameOfReference(String a, String b) {
    return a != null && !a.isBlank() && a.equals(b);
  }

  public boolean overlappingZ(double[] a, double[] b) {
    if (a == null || b == null || a.length == 0 || b.length == 0) {
      return false;
    }
    double a0 = min(a);
    double a1 = max(a);
    double b0 = min(b);
    double b1 = max(b);
    return a0 <= b1 + 1e-9 && b0 <= a1 + 1e-9;
  }

  public boolean compatible(
      String frameOfReferenceA, String frameOfReferenceB, double[] zA, double[] zB) {
    return sameFrameOfReference(frameOfReferenceA, frameOfReferenceB) && overlappingZ(zA, zB);
  }

  private static double min(double[] v) {
    double m = v[0];
    for (double x : v) {
      m = Math.min(m, x);
    }
    return m;
  }

  private static double max(double[] v) {
    double m = v[0];
    for (double x : v) {
      m = Math.max(m, x);
    }
    return m;
  }
}
