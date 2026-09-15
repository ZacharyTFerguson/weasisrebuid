/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.explorer;

import java.awt.Rectangle;

/** Pending photo-editor values applied before dicomize. */
public class AcquireImageValues {

  private int rotation;
  private float brightness;
  private float contrast = 1.0f;
  private Rectangle crop;

  public int getRotation() {
    return rotation;
  }

  public void setRotation(int rotation) {
    int r = rotation % 360;
    if (r < 0) {
      r += 360;
    }
    this.rotation = r / 90 * 90;
  }

  public float getBrightness() {
    return brightness;
  }

  public void setBrightness(float brightness) {
    this.brightness = brightness;
  }

  public float getContrast() {
    return contrast;
  }

  public void setContrast(float contrast) {
    this.contrast = Math.max(0.01f, contrast);
  }

  public Rectangle getCrop() {
    return crop == null ? null : new Rectangle(crop);
  }

  public void setCrop(Rectangle crop) {
    this.crop = crop == null ? null : new Rectangle(crop);
  }
}
