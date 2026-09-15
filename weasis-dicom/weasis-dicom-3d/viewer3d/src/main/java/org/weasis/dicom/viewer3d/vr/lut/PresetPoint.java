/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer3d.vr.lut;

public final class PresetPoint {

  private final float intensity;
  private final float red;
  private final float green;
  private final float blue;
  private final float opacity;

  public PresetPoint(float intensity, float red, float green, float blue, float opacity) {
    this.intensity = intensity;
    this.red = red;
    this.green = green;
    this.blue = blue;
    this.opacity = opacity;
  }

  public float getIntensity() {
    return intensity;
  }

  public float getRed() {
    return red;
  }

  public float getGreen() {
    return green;
  }

  public float getBlue() {
    return blue;
  }

  public float getOpacity() {
    return opacity;
  }
}
