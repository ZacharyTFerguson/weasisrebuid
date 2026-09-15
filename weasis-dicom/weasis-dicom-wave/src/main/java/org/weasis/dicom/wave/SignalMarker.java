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

/** Sample index and millivolt for a caliper on one lead. */
public class SignalMarker {

  private final int sampleIndex;
  private final double millivolt;

  public SignalMarker(int sampleIndex, double millivolt) {
    this.sampleIndex = sampleIndex;
    this.millivolt = millivolt;
  }

  public int sampleIndex() {
    return sampleIndex;
  }

  public double millivolt() {
    return millivolt;
  }
}
