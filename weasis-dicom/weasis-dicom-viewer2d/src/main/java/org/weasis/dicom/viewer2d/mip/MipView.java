/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.mip;

import org.weasis.dicom.viewer2d.mpr.Volume;

/** MIP along a stack. Types from the 4.7 MPR tutorial: None / Min / Mean / Max. */
public class MipView {

  public enum Type {
    NONE,
    MIN,
    MEAN,
    MAX
  }

  private Type type = Type.NONE;
  private int thickness = 1;

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type == null ? Type.NONE : type;
  }

  public int getThickness() {
    return thickness;
  }

  public void setThickness(int thickness) {
    this.thickness = Math.max(1, thickness);
  }

  public double project(Volume volume, int x, int y, int z) {
    if (volume == null) {
      return 0;
    }
    if (type == Type.NONE) {
      return volume.value(x, y, z);
    }
    int z0 = Math.max(0, z - thickness / 2);
    int z1 = Math.min(volume.sizeZ() - 1, z0 + thickness - 1);
    return volume.projectZ(x, y, z0, z1, type);
  }
}
