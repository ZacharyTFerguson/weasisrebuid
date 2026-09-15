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

import org.weasis.dicom.viewer2d.mip.MipView;

/**
 * MIP splat of a volume onto one MPR plane. Orthogonal requests reuse {@link Volume#slice}; oblique
 * requests accumulate along the plane normal for the MIP slab thickness (Alt+wheel).
 */
public class SplatContext {

  private Volume volume;
  private MprAxis axis = MprAxis.AXIAL;
  private int index;
  private MipView.Type mip = MipView.Type.NONE;
  private int thickness = 1;
  private AxesControl oblique;
  private int width = 1;
  private int height = 1;

  public SplatContext() {}

  public SplatContext(Volume volume, MprAxis axis, int index) {
    this(volume, axis, index, MipView.Type.NONE, 1);
  }

  public SplatContext(Volume volume, MprAxis axis, int index, MipView.Type mip, int thickness) {
    this.volume = volume;
    this.axis = axis == null ? MprAxis.AXIAL : axis;
    this.index = Math.max(0, index);
    this.mip = mip == null ? MipView.Type.NONE : mip;
    this.thickness = Math.max(1, thickness);
  }

  public SplatContext(Volume volume, AxesControl axes, int width, int height) {
    this(volume, axes, width, height, MipView.Type.NONE, 1);
  }

  public SplatContext(
      Volume volume, AxesControl axes, int width, int height, MipView.Type mip, int thickness) {
    this.volume = volume;
    this.oblique = axes;
    this.width = Math.max(1, width);
    this.height = Math.max(1, height);
    this.mip = mip == null ? MipView.Type.NONE : mip;
    this.thickness = Math.max(1, thickness);
  }

  public static SplatContext from(BuildContext context) {
    if (context == null) {
      return new SplatContext();
    }
    return new SplatContext(
        context.getVolume(),
        context.getAxis(),
        context.getIndex(),
        context.getMip(),
        context.getThickness());
  }

  public Volume getVolume() {
    return volume;
  }

  public void setVolume(Volume volume) {
    this.volume = volume;
  }

  public MprAxis getAxis() {
    return axis;
  }

  public void setAxis(MprAxis axis) {
    this.axis = axis == null ? MprAxis.AXIAL : axis;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = Math.max(0, index);
  }

  public MipView.Type getMip() {
    return mip;
  }

  public void setMip(MipView.Type mip) {
    this.mip = mip == null ? MipView.Type.NONE : mip;
  }

  public int getThickness() {
    return thickness;
  }

  public void setThickness(int thickness) {
    this.thickness = Math.max(1, thickness);
  }

  public AxesControl getOblique() {
    return oblique;
  }

  public void setOblique(AxesControl oblique) {
    this.oblique = oblique;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public void setSize(int width, int height) {
    this.width = Math.max(1, width);
    this.height = Math.max(1, height);
  }

  public boolean isOblique() {
    return oblique != null;
  }

  public BuildContext toBuildContext() {
    return new BuildContext(volume, axis, index, mip, thickness);
  }

  public VolImageIO toVolImageIO() {
    if (oblique != null) {
      return new VolImageIO(volume, oblique, width, height);
    }
    return new VolImageIO(volume, axis, index, mip, thickness);
  }

  /** 2D samples after splat / MIP reduce. */
  public double[][] samples() {
    if (volume == null) {
      return new double[0][0];
    }
    if (oblique != null) {
      return splatOblique();
    }
    return volume.slice(axis, index, mip, thickness);
  }

  private double[][] splatOblique() {
    AxisDirection normal = oblique.normal();
    int w = width;
    int h = height;
    double[] origin = oblique.origin();
    double[] u = oblique.u().xyz();
    double[] v = oblique.v().xyz();
    int half = Math.max(0, (thickness - 1) / 2);
    double[][] out = new double[h][w];
    for (int j = 0; j < h; j++) {
      for (int i = 0; i < w; i++) {
        double x = origin[0] + i * u[0] + j * v[0];
        double y = origin[1] + i * u[1] + j * v[1];
        double z = origin[2] + i * u[2] + j * v[2];
        out[j][i] = splatAlong(x, y, z, normal, half);
      }
    }
    return out;
  }

  private double splatAlong(double x, double y, double z, AxisDirection normal, int half) {
    if (mip == MipView.Type.NONE || half == 0) {
      return volume.sampleNearest(x, y, z);
    }
    if (mip == MipView.Type.MIN) {
      double m = Double.POSITIVE_INFINITY;
      for (int t = -half; t <= half; t++) {
        m = Math.min(m, sampleOffset(x, y, z, normal, t));
      }
      return m;
    }
    if (mip == MipView.Type.MAX) {
      double m = Double.NEGATIVE_INFINITY;
      for (int t = -half; t <= half; t++) {
        m = Math.max(m, sampleOffset(x, y, z, normal, t));
      }
      return m;
    }
    double sum = 0;
    int n = 0;
    for (int t = -half; t <= half; t++) {
      sum += sampleOffset(x, y, z, normal, t);
      n++;
    }
    return n == 0 ? 0 : sum / n;
  }

  private double sampleOffset(double x, double y, double z, AxisDirection normal, int t) {
    return volume.sampleNearest(x + t * normal.x(), y + t * normal.y(), z + t * normal.z());
  }
}
