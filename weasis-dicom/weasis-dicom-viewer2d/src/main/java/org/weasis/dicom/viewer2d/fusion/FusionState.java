/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.fusion;

/** Overlay window, LUT name, and opacity applied to {@code FusionController.targetViews}. */
public class FusionState {

  private FusionWindow window = new FusionWindow();
  private String lut = FusionColorScale.HOT_IRON;
  private double opacity = 0.4;

  public FusionWindow getWindow() {
    return window;
  }

  public void setWindow(FusionWindow window) {
    this.window = window == null ? new FusionWindow() : window;
  }

  public String getLut() {
    return lut;
  }

  public void setLut(String lut) {
    this.lut = lut == null || lut.isBlank() ? FusionColorScale.HOT_IRON : lut;
  }

  public double getOpacity() {
    return opacity;
  }

  public void setOpacity(double opacity) {
    this.opacity = Math.max(0, Math.min(1, opacity));
  }
}
