/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.rt;

/** Patient-coordinate Z (mm) used as a plane map key. */
public final class KeyDouble implements Comparable<KeyDouble> {

  static final double EPSILON_MM = 1e-3;

  private final double value;

  public KeyDouble(double value) {
    this.value = value;
  }

  public double value() {
    return value;
  }

  @Override
  public int compareTo(KeyDouble other) {
    return Double.compare(value, other == null ? 0 : other.value);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof KeyDouble other)) {
      return false;
    }
    return Math.abs(value - other.value) < EPSILON_MM;
  }

  @Override
  public int hashCode() {
    return Long.hashCode(Math.round(value / EPSILON_MM));
  }
}
