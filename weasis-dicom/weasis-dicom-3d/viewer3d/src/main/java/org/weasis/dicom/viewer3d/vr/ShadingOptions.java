/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer3d.vr;

public class ShadingOptions {

  private float ambient = 0.2f;
  private float diffuse = 0.6f;
  private float specular = 0.2f;
  private boolean enabled;

  public float getAmbient() {
    return ambient;
  }

  public void setAmbient(float ambient) {
    this.ambient = clamp(ambient);
  }

  public float getDiffuse() {
    return diffuse;
  }

  public void setDiffuse(float diffuse) {
    this.diffuse = clamp(diffuse);
  }

  public float getSpecular() {
    return specular;
  }

  public void setSpecular(float specular) {
    this.specular = clamp(specular);
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  private static float clamp(float v) {
    return Math.max(0f, Math.min(1f, v));
  }
}
