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

import org.weasis.dicom.viewer2d.mpr.Volume;

/** Overlay volume plus ImagePositionPatient Z (mm) for each stacked slice. */
public class FusionStack {

  private final Volume volume;
  private final double[] zMm;
  private final String frameOfReferenceUID;

  public FusionStack(Volume volume, double[] zMm, String frameOfReferenceUID) {
    this.volume = volume;
    this.zMm = zMm == null ? new double[0] : zMm.clone();
    this.frameOfReferenceUID = frameOfReferenceUID == null ? "" : frameOfReferenceUID;
  }

  public static FusionStack empty() {
    return new FusionStack(null, new double[0], "");
  }

  public Volume volume() {
    return volume;
  }

  public double[] zMm() {
    return zMm.clone();
  }

  public String frameOfReferenceUID() {
    return frameOfReferenceUID;
  }

  public boolean isEmpty() {
    return volume == null || zMm.length == 0;
  }
}
