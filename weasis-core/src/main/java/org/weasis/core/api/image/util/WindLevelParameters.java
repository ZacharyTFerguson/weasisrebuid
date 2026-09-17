/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.image.util;

/** Window / level pair used by {@code WindowOp}. Optional VOI LUT table is Sequence, not linear. */
public class WindLevelParameters {

  private double window;
  private double level;
  private String lutShape = "LINEAR";
  private int[] voiLut;
  private int voiLutFirst;

  public WindLevelParameters(double window, double level) {
    this.window = window;
    this.level = level;
  }

  public double getWindow() {
    return window;
  }

  public void setWindow(double window) {
    this.window = window;
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

  public String getLutShape() {
    return lutShape == null || lutShape.isBlank() ? "LINEAR" : lutShape;
  }

  public void setLutShape(String lutShape) {
    this.lutShape = lutShape == null || lutShape.isBlank() ? "LINEAR" : lutShape;
  }

  public boolean hasVoiLut() {
    return voiLut != null && voiLut.length > 0;
  }

  public int[] getVoiLut() {
    return voiLut;
  }

  public int getVoiLutFirst() {
    return voiLutFirst;
  }

  public void setVoiLut(int[] lut, int firstMapped) {
    this.voiLut = lut == null ? null : lut.clone();
    this.voiLutFirst = firstMapped;
  }

  /** Map a stored/modality sample to 8-bit display (linear, SIGMOID, or VOI LUT table). */
  public int toDisplay8(double value) {
    if (hasVoiLut()) {
      return lutSample(value);
    }
    if ("SIGMOID".equalsIgnoreCase(getLutShape())) {
      return sigmoid8(value);
    }
    return linear8(value);
  }

  int linear8(double value) {
    if (window <= 0) {
      return 0;
    }
    double n = (value - getLower()) / window;
    return clamp8((int) Math.round(n * 255.0));
  }

  /** DICOM VOI LUT Function SIGMOID: {@code ymax / (1 + exp(-4 * (x - xc) / ww))}. */
  int sigmoid8(double value) {
    if (window <= 0) {
      return 0;
    }
    double n = 1.0 / (1.0 + Math.exp(-4.0 * (value - level) / window));
    return clamp8((int) Math.round(n * 255.0));
  }

  int lutSample(double value) {
    int[] lut = voiLut;
    int i = (int) Math.floor(value) - voiLutFirst;
    if (i < 0) {
      i = 0;
    } else if (i >= lut.length) {
      i = lut.length - 1;
    }
    return clamp8(lut[i]);
  }

  static int clamp8(int v) {
    if (v < 0) {
      return 0;
    }
    return v > 255 ? 255 : v;
  }
}
