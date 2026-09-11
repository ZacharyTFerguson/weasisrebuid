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

/** MIP / MinIP / MeanIP. Thickness is N slices before AND after current (CPU, not GPU). */
public final class MipProjector {

  public enum Mode {
    NONE,
    MIN,
    MEAN,
    MAX
  }

  private Mode mode = Mode.NONE;
  private int thickness;

  public Mode mode() {
    return mode;
  }

  public void setMode(Mode mode) {
    Mode prev = this.mode;
    this.mode = mode == null ? Mode.NONE : mode;
    if (prev == Mode.NONE && this.mode != Mode.NONE && thickness == 0) {
      this.thickness = 2;
    }
  }

  /** Ctrl+Alt+B: cycle None/Min/Mean/Max. */
  public void cycle() {
    Mode[] all = Mode.values();
    setMode(all[(mode.ordinal() + 1) % all.length]);
  }

  public int thickness() {
    return thickness;
  }

  public void setThickness(int thickness) {
    this.thickness = Math.max(0, thickness);
  }

  /** N=3 → 7 slices (3 before + current + 3 after). */
  public int slabSize() {
    if (mode == Mode.NONE) {
      return 1;
    }
    return 2 * thickness + 1;
  }

  public double project(double[] samples) {
    if (samples == null || samples.length == 0) {
      return 0;
    }
    return switch (mode) {
      case MIN -> min(samples);
      case MAX, NONE -> max(samples);
      case MEAN -> mean(samples);
    };
  }

  static double min(double[] s) {
    double m = s[0];
    for (double v : s) {
      m = Math.min(m, v);
    }
    return m;
  }

  static double max(double[] s) {
    double m = s[0];
    for (double v : s) {
      m = Math.max(m, v);
    }
    return m;
  }

  static double mean(double[] s) {
    double t = 0;
    for (double v : s) {
      t += v;
    }
    return t / s.length;
  }
}
