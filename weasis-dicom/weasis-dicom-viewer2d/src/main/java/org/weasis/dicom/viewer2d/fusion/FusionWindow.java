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

import org.weasis.dicom.codec.utils.LutPipeline;

/** Linear VOI window/level for a PET overlay (SUV or stored activity). */
public class FusionWindow {

  private double window = 1;
  private double level;

  public FusionWindow() {}

  public FusionWindow(double window, double level) {
    setWindow(window);
    setLevel(level);
  }

  public double getWindow() {
    return window;
  }

  public void setWindow(double window) {
    this.window = Math.max(1, window);
  }

  public double getLevel() {
    return level;
  }

  public void setLevel(double level) {
    this.level = level;
  }

  public double getLower() {
    return level - window / 2.0;
  }

  public double getUpper() {
    return level + window / 2.0;
  }

  public int indexOf(double value) {
    return LutPipeline.applyVoiLinear(value, window, level);
  }
}
