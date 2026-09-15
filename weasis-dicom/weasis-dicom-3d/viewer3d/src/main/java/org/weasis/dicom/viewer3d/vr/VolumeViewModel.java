/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer3d.vr;

import org.weasis.dicom.viewer3d.geometry.ViewData;
import org.weasis.dicom.viewer3d.vr.lut.VolumePreset;

public class VolumeViewModel {

  private DicomVolTexture volume;
  private VolumePreset preset = VolumePreset.ctSoftTissue();
  private final ViewData viewData = new ViewData();
  private final RenderingLayer layer = new RenderingLayer();
  private final ShadingOptions shading = new ShadingOptions();

  public DicomVolTexture getVolume() {
    return volume;
  }

  public void setVolume(DicomVolTexture volume) {
    this.volume = volume;
  }

  public VolumePreset getPreset() {
    return preset;
  }

  public void setPreset(VolumePreset preset) {
    if (preset != null) {
      this.preset = preset;
    }
  }

  public ViewData getViewData() {
    return viewData;
  }

  public RenderingLayer getLayer() {
    return layer;
  }

  public ShadingOptions getShading() {
    return shading;
  }
}
