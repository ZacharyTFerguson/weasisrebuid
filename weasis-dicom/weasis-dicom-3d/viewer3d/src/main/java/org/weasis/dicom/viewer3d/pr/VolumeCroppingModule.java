/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer3d.pr;

public class VolumeCroppingModule {

  private float minX;
  private float maxX = 1f;
  private float minY;
  private float maxY = 1f;
  private float minZ;
  private float maxZ = 1f;

  public float getMinX() {
    return minX;
  }

  public void setMinX(float minX) {
    this.minX = minX;
  }

  public float getMaxX() {
    return maxX;
  }

  public void setMaxX(float maxX) {
    this.maxX = maxX;
  }

  public float getMinY() {
    return minY;
  }

  public void setMinY(float minY) {
    this.minY = minY;
  }

  public float getMaxY() {
    return maxY;
  }

  public void setMaxY(float maxY) {
    this.maxY = maxY;
  }

  public float getMinZ() {
    return minZ;
  }

  public void setMinZ(float minZ) {
    this.minZ = minZ;
  }

  public float getMaxZ() {
    return maxZ;
  }

  public void setMaxZ(float maxZ) {
    this.maxZ = maxZ;
  }
}
