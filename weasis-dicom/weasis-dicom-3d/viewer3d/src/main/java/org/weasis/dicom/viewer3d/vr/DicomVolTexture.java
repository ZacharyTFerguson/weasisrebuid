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

public class DicomVolTexture extends VolumeTexture {

  private double pixelSpacingX = 1.0;
  private double pixelSpacingY = 1.0;
  private double sliceSpacing = 1.0;
  private String modality = "CT";

  public DicomVolTexture() {}

  public DicomVolTexture(TextureData data) {
    super(data);
  }

  public double getPixelSpacingX() {
    return pixelSpacingX;
  }

  public void setPixelSpacingX(double pixelSpacingX) {
    this.pixelSpacingX = pixelSpacingX;
  }

  public double getPixelSpacingY() {
    return pixelSpacingY;
  }

  public void setPixelSpacingY(double pixelSpacingY) {
    this.pixelSpacingY = pixelSpacingY;
  }

  public double getSliceSpacing() {
    return sliceSpacing;
  }

  public void setSliceSpacing(double sliceSpacing) {
    this.sliceSpacing = sliceSpacing;
  }

  public String getModality() {
    return modality;
  }

  public void setModality(String modality) {
    this.modality = modality == null ? "OT" : modality;
  }
}
