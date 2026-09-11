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
 * Crosshair stays active regardless of mouse action: move center, drag one line (Move axis),
 * rotate, scroll depth. Plane coupling cannot be turned off.
 */
public final class AxesControl {

  public enum Mode {
    MOVE_CENTER,
    MOVE_AXIS,
    ROTATE,
    SCROLL_DEPTH
  }

  private final MprVolume volume;
  private Mode mode = Mode.MOVE_CENTER;
  private double rotationDeg;
  private double depth;
  private boolean coupling = true;

  public AxesControl(MprVolume volume) {
    this.volume = volume;
  }

  public Mode mode() {
    return mode;
  }

  public void setMode(Mode mode) {
    this.mode = mode == null ? Mode.MOVE_CENTER : mode;
  }

  public boolean couplingAlwaysOn() {
    return coupling;
  }

  public void moveCenter(double dx, double dy, double dz) {
    mode = Mode.MOVE_CENTER;
    double[] c = volume.volumeCenter();
    volume.setVolumeCenter(c[0] + dx, c[1] + dy, c[2] + dz);
  }

  public void moveAxis(double delta) {
    mode = Mode.MOVE_AXIS;
    depth += delta;
  }

  public void rotate(double deltaDeg) {
    rotationDeg += deltaDeg;
  }

  public double rotationDeg() {
    return rotationDeg;
  }

  public void scrollDepth(double delta) {
    depth += delta;
  }

  public double depth() {
    return depth;
  }

  public MprVolume volume() {
    return volume;
  }
}
