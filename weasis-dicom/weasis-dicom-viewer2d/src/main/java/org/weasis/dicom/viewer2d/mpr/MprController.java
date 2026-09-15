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

public class MprController implements VolumeProvider {

  private Volume volume;
  private final MprView axial = new MprView();
  private final MprView coronal = new MprView();
  private final MprView sagittal = new MprView();

  public MprController() {
    axial.setAxis(MprAxis.AXIAL);
    coronal.setAxis(MprAxis.CORONAL);
    sagittal.setAxis(MprAxis.SAGITTAL);
  }

  public void setVolume(Volume volume) {
    this.volume = volume;
    if (volume != null) {
      axial.setSliceIndex(volume.sizeZ() / 2);
      coronal.setSliceIndex(volume.sizeY() / 2);
      sagittal.setSliceIndex(volume.sizeX() / 2);
    }
  }

  @Override
  public Volume getVolume() {
    return volume;
  }

  public MprView getAxial() {
    return axial;
  }

  public MprView getCoronal() {
    return coronal;
  }

  public MprView getSagittal() {
    return sagittal;
  }

  public void setMip(MipView.Type type, int thickness) {
    for (MprView view : new MprView[] {axial, coronal, sagittal}) {
      view.getMip().setType(type);
      view.getMip().setThickness(thickness);
    }
  }

  public double[][] axialSlice() {
    return axial.rebuild(volume);
  }

  public double[][] coronalSlice() {
    return coronal.rebuild(volume);
  }

  public double[][] sagittalSlice() {
    return sagittal.rebuild(volume);
  }
}
