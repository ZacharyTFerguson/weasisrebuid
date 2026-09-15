/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.mpr.pr;

import org.weasis.dicom.viewer2d.mip.MipView;
import org.weasis.dicom.viewer2d.mpr.AxesControl;
import org.weasis.dicom.viewer2d.mpr.AxisDirection;
import org.weasis.dicom.viewer2d.mpr.BuildContext;
import org.weasis.dicom.viewer2d.mpr.MprAxis;
import org.weasis.dicom.viewer2d.mpr.Volume;

/**
 * Presentation-state MPR geometry: Image Position/Orientation plus the selected plane, index, and
 * MIP thickness used to rebuild a {@link BuildContext}.
 */
public class MprGeometryModule {

  private MprAxis axis = MprAxis.AXIAL;
  private final double[] origin = new double[] {0, 0, 0};
  private final double[] row = new double[] {1, 0, 0};
  private final double[] column = new double[] {0, 1, 0};
  private int index;
  private MipView.Type mip = MipView.Type.NONE;
  private int thickness = 1;

  public MprAxis getAxis() {
    return axis;
  }

  public void setAxis(MprAxis axis) {
    this.axis = axis == null ? MprAxis.AXIAL : axis;
  }

  public double[] getOrigin() {
    return origin.clone();
  }

  public void setOrigin(double x, double y, double z) {
    origin[0] = x;
    origin[1] = y;
    origin[2] = z;
  }

  public double[] getRow() {
    return row.clone();
  }

  public void setRow(double x, double y, double z) {
    row[0] = x;
    row[1] = y;
    row[2] = z;
  }

  public double[] getColumn() {
    return column.clone();
  }

  public void setColumn(double x, double y, double z) {
    column[0] = x;
    column[1] = y;
    column[2] = z;
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

  public BuildContext toBuildContext(Volume volume) {
    return new BuildContext(volume, axis, index, mip, thickness);
  }

  public AxesControl toAxes() {
    AxesControl axes = new AxesControl();
    axes.setOrigin(origin[0], origin[1], origin[2]);
    axes.setU(new AxisDirection(row[0], row[1], row[2]));
    axes.setV(new AxisDirection(column[0], column[1], column[2]));
    return axes;
  }

  public void apply(AxesControl axes) {
    if (axes == null) {
      return;
    }
    setOrigin(axes.origin()[0], axes.origin()[1], axes.origin()[2]);
    AxisDirection u = axes.u();
    AxisDirection v = axes.v();
    setRow(u.x(), u.y(), u.z());
    setColumn(v.x(), v.y(), v.z());
  }

  public static MprGeometryModule from(BuildContext context) {
    MprGeometryModule module = new MprGeometryModule();
    if (context != null) {
      module.setAxis(context.getAxis());
      module.setIndex(context.getIndex());
      module.setMip(context.getMip());
      module.setThickness(context.getThickness());
    }
    return module;
  }

  public static MprGeometryModule from(AxesControl axes) {
    MprGeometryModule module = new MprGeometryModule();
    module.apply(axes);
    return module;
  }
}
