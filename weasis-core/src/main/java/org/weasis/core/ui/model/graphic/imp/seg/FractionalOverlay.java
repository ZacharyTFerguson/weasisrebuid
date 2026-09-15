/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.graphic.imp.seg;

/** Fractional SEG overlay (probability map) attached to a region. */
public class FractionalOverlay {

  private float[] values = new float[0];
  private int width;
  private int height;

  public float[] getValues() {
    return values;
  }

  public void setValues(float[] values, int width, int height) {
    this.values = values == null ? new float[0] : values;
    this.width = width;
    this.height = height;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }
}
