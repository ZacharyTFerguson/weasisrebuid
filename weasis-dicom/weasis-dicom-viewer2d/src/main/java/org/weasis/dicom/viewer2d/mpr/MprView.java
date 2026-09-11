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

/**
 * One MPR pane. Each pane may later hold its own FusionOp (WP-8); this WP does not implement fusion
 * Have.
 */
public final class MprView {

  private final Plane plane;
  private final MprAxis axis;
  private Object fusionOp;
  private boolean mipIndicator;

  public MprView(Plane plane) {
    this.plane = plane == null ? Plane.AXIAL : plane;
    this.axis = new MprAxis(this.plane);
  }

  public Plane plane() {
    return plane;
  }

  public MprAxis axis() {
    return axis;
  }

  public String label() {
    return plane.name().toLowerCase();
  }

  public Object fusionOp() {
    return fusionOp;
  }

  public void setFusionOp(Object fusionOp) {
    this.fusionOp = fusionOp;
  }

  public boolean mipIndicator() {
    return mipIndicator;
  }

  public void setMipIndicator(boolean mipIndicator) {
    this.mipIndicator = mipIndicator;
  }
}
