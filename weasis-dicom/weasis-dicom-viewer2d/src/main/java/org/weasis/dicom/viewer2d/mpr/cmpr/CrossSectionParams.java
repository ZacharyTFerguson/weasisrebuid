/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.mpr.cmpr;

import java.awt.geom.Point2D;
import java.util.List;
import org.weasis.dicom.viewer2d.mip.MipView;

/** Plane perpendicular to a 2D CPR curve at parameter {@code t} in [0, 1]. */
public class CrossSectionParams {

  private List<Point2D.Double> curve = List.of();
  private int z;
  private double t;
  private int width = 5;
  private int height = 5;
  private int thickness = 1;
  private MipView.Type mip = MipView.Type.NONE;

  public List<Point2D.Double> getCurve() {
    return curve;
  }

  public void setCurve(List<Point2D.Double> curve) {
    this.curve = curve == null ? List.of() : List.copyOf(curve);
  }

  public int getZ() {
    return z;
  }

  public void setZ(int z) {
    this.z = z;
  }

  public double getT() {
    return t;
  }

  public void setT(double t) {
    this.t = Math.max(0, Math.min(1, t));
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = Math.max(1, width);
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = Math.max(1, height);
  }

  public int getThickness() {
    return thickness;
  }

  public void setThickness(int thickness) {
    this.thickness = Math.max(1, thickness);
  }

  public MipView.Type getMip() {
    return mip;
  }

  public void setMip(MipView.Type mip) {
    this.mip = mip == null ? MipView.Type.NONE : mip;
  }
}
