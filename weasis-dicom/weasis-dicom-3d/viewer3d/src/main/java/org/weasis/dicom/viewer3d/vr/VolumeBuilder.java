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

public class VolumeBuilder {

  private final DicomVolTextureFactory factory = new DicomVolTextureFactory();

  public DicomVolTexture build(short[][] slices, int width, int height) {
    if (slices == null || slices.length == 0) {
      throw new IllegalArgumentException("slices");
    }
    DicomVolTexture texture = factory.create(width, height, slices.length);
    for (int z = 0; z < slices.length; z++) {
      TextureSliceDataBuffer buffer = new TextureSliceDataBuffer(width, height);
      short[] src = slices[z];
      if (src != null) {
        int n = Math.min(src.length, buffer.getSlice().length);
        System.arraycopy(src, 0, buffer.getSlice(), 0, n);
      }
      buffer.copyInto(texture.getData(), z);
    }
    return texture;
  }
}
