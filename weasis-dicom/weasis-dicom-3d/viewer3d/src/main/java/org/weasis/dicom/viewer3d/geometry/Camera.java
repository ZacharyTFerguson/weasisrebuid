/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer3d.geometry;

/** Orbit camera around a volume origin. Coordinates are patient-space millimetres. */
public final class Camera {

  private double eyeX;
  private double eyeY;
  private double eyeZ;
  private double targetX;
  private double targetY;
  private double targetZ;
  private double upX;
  private double upY;
  private double upZ;

  public Camera(double eyeX, double eyeY, double eyeZ, double upX, double upY, double upZ) {
    this.eyeX = eyeX;
    this.eyeY = eyeY;
    this.eyeZ = eyeZ;
    this.upX = upX;
    this.upY = upY;
    this.upZ = upZ;
  }

  public static Camera axial() {
    return new Camera(0, 0, 1, 0, 1, 0);
  }

  public static Camera coronal() {
    return new Camera(0, -1, 0, 0, 0, 1);
  }

  public static Camera sagittal() {
    return new Camera(1, 0, 0, 0, 0, 1);
  }

  public void orbit(double yawRadians, double pitchRadians) {
    double cosY = Math.cos(yawRadians);
    double sinY = Math.sin(yawRadians);
    double x = eyeX * cosY - eyeZ * sinY;
    double z = eyeX * sinY + eyeZ * cosY;
    eyeX = x;
    eyeZ = z;
    double cosP = Math.cos(pitchRadians);
    double sinP = Math.sin(pitchRadians);
    double y = eyeY * cosP - eyeZ * sinP;
    z = eyeY * sinP + eyeZ * cosP;
    eyeY = y;
    eyeZ = z;
  }

  public double getEyeX() {
    return eyeX;
  }

  public double getEyeY() {
    return eyeY;
  }

  public double getEyeZ() {
    return eyeZ;
  }

  public double getTargetX() {
    return targetX;
  }

  public double getTargetY() {
    return targetY;
  }

  public double getTargetZ() {
    return targetZ;
  }

  public double getUpX() {
    return upX;
  }

  public double getUpY() {
    return upY;
  }

  public double getUpZ() {
    return upZ;
  }
}
