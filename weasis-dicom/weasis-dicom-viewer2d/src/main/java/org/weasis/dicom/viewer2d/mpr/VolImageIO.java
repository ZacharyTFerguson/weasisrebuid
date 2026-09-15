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

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferUShort;
import org.weasis.dicom.viewer2d.mip.MipView;

/** Reconstructs one MPR plane from a volume for the 2D viewer. */
public class VolImageIO {

  private Volume volume;
  private MprAxis axis = MprAxis.AXIAL;
  private int index;
  private MipView.Type mip = MipView.Type.NONE;
  private int thickness = 1;
  private AxesControl oblique;
  private int obliqueWidth;
  private int obliqueHeight;

  public VolImageIO() {}

  public VolImageIO(Volume volume, MprAxis axis, int index) {
    this(volume, axis, index, MipView.Type.NONE, 1);
  }

  public VolImageIO(Volume volume, MprAxis axis, int index, MipView.Type mip, int thickness) {
    this.volume = volume;
    this.axis = axis == null ? MprAxis.AXIAL : axis;
    this.index = Math.max(0, index);
    this.mip = mip == null ? MipView.Type.NONE : mip;
    this.thickness = Math.max(1, thickness);
  }

  public VolImageIO(BuildContext context) {
    this(
        context == null ? null : context.getVolume(),
        context == null ? MprAxis.AXIAL : context.getAxis(),
        context == null ? 0 : context.getIndex(),
        context == null ? MipView.Type.NONE : context.getMip(),
        context == null ? 1 : context.getThickness());
  }

  public VolImageIO(Volume volume, AxesControl axes, int width, int height) {
    this.volume = volume;
    this.oblique = axes;
    this.obliqueWidth = Math.max(1, width);
    this.obliqueHeight = Math.max(1, height);
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

  public double[][] samples() {
    if (volume == null) {
      return new double[0][0];
    }
    if (oblique != null) {
      return new ObliqueMpr().slice(volume, oblique, obliqueWidth, obliqueHeight);
    }
    return MPRGenerator.orthogonal(volume, axis, index, mip, thickness);
  }

  public BufferedImage getImage() {
    double[][] samples = samples();
    if (samples.length == 0 || samples[0].length == 0) {
      return null;
    }
    int height = samples.length;
    int width = samples[0].length;
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_GRAY);
    short[] data = ((DataBufferUShort) image.getRaster().getDataBuffer()).getData();
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int v = (int) Math.round(samples[y][x]);
        if (v < 0) {
          v = 0;
        } else if (v > 65535) {
          v = 65535;
        }
        data[y * width + x] = (short) v;
      }
    }
    return image;
  }

  public RawImageIO toRaw() {
    RawImageIO raw = new RawImageIO();
    new CopyPixelsTask().copyTo(samples(), raw);
    return raw;
  }
}
