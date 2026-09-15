/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.codec.geometry;

import java.util.Arrays;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;

public class GeometryOfSlice {
  private final double[] row;
  private final double[] column;
  private final double[] tlhc;
  private final double[] voxelSpacing;
  private final double[] dimensions;

  public GeometryOfSlice(
      double[] row, double[] column, double[] tlhc, double[] voxelSpacing, double[] dimensions) {
    this.row = copy3(row);
    this.column = copy3(column);
    this.tlhc = copy3(tlhc);
    this.voxelSpacing = voxelSpacing == null ? new double[] {1, 1, 1} : voxelSpacing.clone();
    this.dimensions = dimensions == null ? new double[] {0, 0} : dimensions.clone();
  }

  public static GeometryOfSlice fromDataset(Attributes dcm) {
    if (dcm == null) {
      return null;
    }
    double[] iop = dcm.getDoubles(Tag.ImageOrientationPatient);
    double[] ipp = dcm.getDoubles(Tag.ImagePositionPatient);
    if (iop == null || iop.length < 6 || ipp == null || ipp.length < 3) {
      return null;
    }
    double[] row = new double[] {iop[0], iop[1], iop[2]};
    double[] col = new double[] {iop[3], iop[4], iop[5]};
    double[] ps = dcm.getDoubles(Tag.PixelSpacing);
    double rowSp = ps != null && ps.length > 0 ? ps[0] : 1.0;
    double colSp = ps != null && ps.length > 1 ? ps[1] : rowSp;
    double thick = dcm.getDouble(Tag.SliceThickness, 1.0);
    double rows = dcm.getInt(Tag.Rows, 0);
    double cols = dcm.getInt(Tag.Columns, 0);
    return new GeometryOfSlice(
        row, col, ipp, new double[] {colSp, rowSp, thick}, new double[] {cols, rows});
  }

  public double[] getRow() {
    return row.clone();
  }

  public double[] getColumn() {
    return column.clone();
  }

  public double[] getTlhc() {
    return tlhc.clone();
  }

  public double[] getVoxelSpacing() {
    return voxelSpacing.clone();
  }

  public double[] getDimensions() {
    return dimensions.clone();
  }

  public double[] getNormal() {
    return VectorUtils.normalize(VectorUtils.cross(row, column));
  }

  public Orientation getOrientation() {
    double[] iop = new double[] {row[0], row[1], row[2], column[0], column[1], column[2]};
    return ImageOrientation.getOrientation(iop);
  }

  private static double[] copy3(double[] v) {
    if (v == null || v.length < 3) {
      return new double[] {0, 0, 0};
    }
    return Arrays.copyOf(v, 3);
  }
}
