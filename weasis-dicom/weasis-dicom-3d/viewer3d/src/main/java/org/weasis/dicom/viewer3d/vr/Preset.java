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

import org.weasis.dicom.viewer3d.vr.lut.VolumePreset;

public class Preset {

  private VolumePreset volumePreset = VolumePreset.ctSoftTissue();

  public VolumePreset getVolumePreset() {
    return volumePreset;
  }

  public void setVolumePreset(VolumePreset volumePreset) {
    if (volumePreset != null) {
      this.volumePreset = volumePreset;
    }
  }

  public String getName() {
    return volumePreset.getName();
  }
}
