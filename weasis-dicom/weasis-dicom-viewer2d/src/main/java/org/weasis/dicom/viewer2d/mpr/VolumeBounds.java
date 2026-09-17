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

public class VolumeBounds {

  private int minX;
  private int minY;
  private int minZ;
  private int maxX;
  private int maxY;
  private int maxZ;

  public VolumeBounds(Volume volume) {
    if (volume != null) {
      maxX = volume.sizeX() - 1;
      maxY = volume.sizeY() - 1;
      maxZ = volume.sizeZ() - 1;
    }
  }

  public int getMinX() {
    return minX;
  }

  public int getMinY() {
    return minY;
  }

  public int getMinZ() {
    return minZ;
  }

  public int getMaxX() {
    return maxX;
  }

  public int getMaxY() {
    return maxY;
  }

  public int getMaxZ() {
    return maxZ;
  }
}
