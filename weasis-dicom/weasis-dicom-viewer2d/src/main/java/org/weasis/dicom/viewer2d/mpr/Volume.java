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

/** Dense 3D block used by orthogonal MPR and MIP. */
public abstract class Volume {

  protected final int sizeX;
  protected final int sizeY;
  protected final int sizeZ;

  protected Volume(int sizeX, int sizeY, int sizeZ) {
    this.sizeX = Math.max(1, sizeX);
    this.sizeY = Math.max(1, sizeY);
    this.sizeZ = Math.max(1, sizeZ);
  }

  public int sizeX() {
    return sizeX;
  }

  public int sizeY() {
    return sizeY;
  }

  public int sizeZ() {
    return sizeZ;
  }

  public abstract double value(int x, int y, int z);

  public abstract void setValue(int x, int y, int z, double value);

  protected int idx(int x, int y, int z) {
    x = Math.max(0, Math.min(sizeX - 1, x));
    y = Math.max(0, Math.min(sizeY - 1, y));
    z = Math.max(0, Math.min(sizeZ - 1, z));
    return z * sizeX * sizeY + y * sizeX + x;
  }

  public double projectZ(int x, int y, int z0, int z1, MipView.Type type) {
    int[] r = ordered(z0, z1, sizeZ);
    return reduce(type, r[0], r[1], z -> value(x, y, z));
  }

  public double projectY(int x, int y0, int y1, int z, MipView.Type type) {
    int[] r = ordered(y0, y1, sizeY);
    return reduce(type, r[0], r[1], y -> value(x, y, z));
  }

  public double projectX(int x0, int x1, int y, int z, MipView.Type type) {
    int[] r = ordered(x0, x1, sizeX);
    return reduce(type, r[0], r[1], x -> value(x, y, z));
  }

  public double[][] slice(MprAxis axis, int index, MipView.Type mip, int thickness) {
    int w = axis == MprAxis.SAGITTAL ? sizeY : sizeX;
    int h = axis == MprAxis.AXIAL ? sizeY : sizeZ;
    double[][] out = new double[h][w];
    int half = Math.max(0, (Math.max(1, thickness) - 1) / 2);
    for (int j = 0; j < h; j++) {
      for (int i = 0; i < w; i++) {
        out[j][i] = sample(axis, index, i, j, mip, half);
      }
    }
    return out;
  }

  double sample(MprAxis axis, int index, int i, int j, MipView.Type mip, int half) {
    int a0 = index - half;
    int a1 = index + half;
    if (axis == MprAxis.AXIAL) {
      return projectZ(i, j, a0, a1, mip);
    }
    if (axis == MprAxis.CORONAL) {
      return projectY(i, a0, a1, j, mip);
    }
    return projectX(a0, a1, i, j, mip);
  }

  private static int[] ordered(int a, int b, int size) {
    if (b < a) {
      int t = a;
      a = b;
      b = t;
    }
    a = Math.max(0, a);
    b = Math.min(size - 1, b);
    return new int[] {a, b};
  }

  private static double reduce(MipView.Type type, int from, int to, IntToDouble fn) {
    if (type == null || type == MipView.Type.NONE) {
      return fn.apply((from + to) / 2);
    }
    if (type == MipView.Type.MIN) {
      double m = Double.POSITIVE_INFINITY;
      for (int i = from; i <= to; i++) {
        m = Math.min(m, fn.apply(i));
      }
      return m;
    }
    if (type == MipView.Type.MAX) {
      double m = Double.NEGATIVE_INFINITY;
      for (int i = from; i <= to; i++) {
        m = Math.max(m, fn.apply(i));
      }
      return m;
    }
    double s = 0;
    int n = 0;
    for (int i = from; i <= to; i++) {
      s += fn.apply(i);
      n++;
    }
    return n == 0 ? 0 : s / n;
  }

  @FunctionalInterface
  interface IntToDouble {
    double apply(int i);
  }
}
