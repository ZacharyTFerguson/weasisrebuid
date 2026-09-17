/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.wave;

/** Channel sensitivity unit; ECG samples convert to millivolts. */
public class Unit {

  public static final String MILLIVOLT = "mV";
  public static final String MICROVOLT = "uV";

  private final String code;

  public Unit(String code) {
    this.code = code == null || code.isBlank() ? MILLIVOLT : code;
  }

  public String code() {
    return code;
  }

  public boolean isMicrovolt() {
    String c = code.toLowerCase();
    return "uv".equals(c) || "µv".equals(c) || "microvolt".equals(c) || "microvolts".equals(c);
  }

  public double toMillivolt(double value) {
    return isMicrovolt() ? value / 1000.0 : value;
  }
}
