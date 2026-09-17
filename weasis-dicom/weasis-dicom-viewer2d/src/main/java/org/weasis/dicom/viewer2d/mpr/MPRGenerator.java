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

import org.weasis.dicom.viewer2d.mip.MipView;

/** Orthogonal MPR / MIP slice builder. CPR is {@code cmpr}. */
public final class MPRGenerator {

  private MPRGenerator() {}

  public static double[][] orthogonal(Volume volume, MprAxis axis, int index) {
    return orthogonal(volume, axis, index, MipView.Type.NONE, 1);
  }

  public static double[][] orthogonal(
      Volume volume, MprAxis axis, int index, MipView.Type mip, int thickness) {
    if (volume == null) {
      return new double[0][0];
    }
    return volume.slice(axis == null ? MprAxis.AXIAL : axis, index, mip, thickness);
  }
}
