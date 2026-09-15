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

import org.weasis.dicom.viewer2d.View2d;
import org.weasis.dicom.viewer2d.mip.MipView;

/** One orthogonal plane of an MPR set. */
public class MprView extends View2d {

  private MprAxis axis = MprAxis.AXIAL;
  private final MipView mip = new MipView();
  private int sliceIndex;

  public MprAxis getAxis() {
    return axis;
  }

  public void setAxis(MprAxis axis) {
    this.axis = axis == null ? MprAxis.AXIAL : axis;
  }

  public MipView getMip() {
    return mip;
  }

  public int getSliceIndex() {
    return sliceIndex;
  }

  public void setSliceIndex(int sliceIndex) {
    this.sliceIndex = Math.max(0, sliceIndex);
  }

  public double[][] rebuild(Volume volume) {
    return MPRGenerator.orthogonal(volume, axis, sliceIndex, mip.getType(), mip.getThickness());
  }
}
