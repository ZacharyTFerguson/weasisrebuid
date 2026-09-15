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

/** Origin plus in-plane axes for an oblique MPR slice. */
public class AxesControl {

  private double originX;
  private double originY;
  private double originZ;
  private AxisDirection u = new AxisDirection(1, 0, 0);
  private AxisDirection v = new AxisDirection(0, 1, 0);

  public double[] origin() {
    return new double[] {originX, originY, originZ};
  }

  public void setOrigin(double x, double y, double z) {
    this.originX = x;
    this.originY = y;
    this.originZ = z;
  }

  public AxisDirection u() {
    return u;
  }

  public void setU(AxisDirection u) {
    this.u = u == null ? new AxisDirection(1, 0, 0) : u;
  }

  public AxisDirection v() {
    return v;
  }

  public void setV(AxisDirection v) {
    this.v = v == null ? new AxisDirection(0, 1, 0) : v;
  }

  public AxisDirection normal() {
    return u.cross(v).normalized();
  }
}
