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

public class VolumeByte extends Volume {

  private final byte[] data;

  public VolumeByte(int sizeX, int sizeY, int sizeZ) {
    super(sizeX, sizeY, sizeZ);
    this.data = new byte[this.sizeX * this.sizeY * this.sizeZ];
  }

  @Override
  public double value(int x, int y, int z) {
    return data[idx(x, y, z)] & 0xff;
  }

  @Override
  public void setValue(int x, int y, int z, double value) {
    data[idx(x, y, z)] = (byte) Math.round(value);
  }
}
