/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.mpr.cmpr;

import java.awt.geom.Point2D;
import java.util.List;
import org.weasis.dicom.viewer2d.mpr.Volume;

/** Curve + slice index used to rebuild a straightened CPR image. */
public class CurvedMprAxis {

  private List<Point2D.Double> curve = List.of();
  private int z;
  private int halfWidth = 8;

  public List<Point2D.Double> getCurve() {
    return curve;
  }

  public void setCurve(List<Point2D.Double> curve) {
    this.curve = curve == null ? List.of() : List.copyOf(curve);
  }

  public int getZ() {
    return z;
  }

  public void setZ(int z) {
    this.z = z;
  }

  public int getHalfWidth() {
    return halfWidth;
  }

  public void setHalfWidth(int halfWidth) {
    this.halfWidth = Math.max(0, halfWidth);
  }

  public double[][] rebuild(Volume volume) {
    return new CurvedMprBuilder().build(volume, curve, z, halfWidth);
  }
}
