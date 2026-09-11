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
 * Curved MPR: no dedicated tool. User traces a measurement polyline (≥ 2 points). Panoramic is
 * uncalibrated (pixels only). Cross-sectional slices are calibrated. Axial curve at fixed Z only.
 */
public final class CprBuilder {

  public static final double DEFAULT_HEIGHT_MM = 40.0;

  private double heightMm = DEFAULT_HEIGHT_MM;
  private double stepMm;
  private boolean calibratedCrossSection = true;

  public CprBuilder(double voxelSpacingMm) {
    this.stepMm = voxelSpacingMm <= 0 ? 1.0 : voxelSpacingMm;
  }

  public double heightMm() {
    return heightMm;
  }

  public double stepMm() {
    return stepMm;
  }

  public void setHeightMm(double heightMm) {
    this.heightMm = heightMm;
  }

  public void setStepMm(double stepMm) {
    this.stepMm = stepMm;
  }

  public void resetToDefaults(double voxelSpacingMm) {
    this.heightMm = DEFAULT_HEIGHT_MM;
    this.stepMm = voxelSpacingMm <= 0 ? 1.0 : voxelSpacingMm;
  }

  public boolean canReconstruct(CprPath path) {
    return path != null && path.size() >= 2 && path.axialFixedZ();
  }

  public boolean writesMillimetrePixelSpacingOnPanoramic() {
    return false;
  }

  public boolean panoramicCalibrated() {
    return false;
  }

  public boolean crossSectionCalibrated() {
    return calibratedCrossSection;
  }

  public String buildPanoramic(CprPath path) {
    if (!canReconstruct(path)) {
      return null;
    }
    return "panoramic:" + path.size() + ":h" + heightMm + ":step" + stepMm;
  }

  public String buildCrossSection(CprPath path) {
    if (!canReconstruct(path)) {
      return null;
    }
    return "cross:" + path.size() + ":calibrated";
  }
}
