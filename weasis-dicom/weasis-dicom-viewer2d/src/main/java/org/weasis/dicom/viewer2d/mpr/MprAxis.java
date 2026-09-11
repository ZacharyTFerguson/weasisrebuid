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

/** One MPR axis (the line perpendicular to a plane). Coupling cannot be turned off. */
public final class MprAxis {

  private final Plane plane;
  private double offset;

  public MprAxis(Plane plane) {
    this.plane = plane == null ? Plane.AXIAL : plane;
  }

  public Plane plane() {
    return plane;
  }

  public String color() {
    return plane.perpendicularColor();
  }

  public double offset() {
    return offset;
  }

  public void setOffset(double offset) {
    this.offset = offset;
  }
}
