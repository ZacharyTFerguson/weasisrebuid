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

/** Volume plane request used by {@link VolImageIO} (axis, index, MIP type and thickness). */
public class BuildContext {

  private Volume volume;
  private MprAxis axis = MprAxis.AXIAL;
  private int index;
  private MipView.Type mip = MipView.Type.NONE;
  private int thickness = 1;

  public BuildContext() {}

  public BuildContext(Volume volume, MprAxis axis, int index) {
    this(volume, axis, index, MipView.Type.NONE, 1);
  }

  public BuildContext(Volume volume, MprAxis axis, int index, MipView.Type mip, int thickness) {
    this.volume = volume;
    this.axis = axis == null ? MprAxis.AXIAL : axis;
    this.index = Math.max(0, index);
    this.mip = mip == null ? MipView.Type.NONE : mip;
    this.thickness = Math.max(1, thickness);
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

  public VolImageIO toVolImageIO() {
    return new VolImageIO(this);
  }
}
