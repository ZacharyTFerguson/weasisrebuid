/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.fusion;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.weasis.dicom.codec.geometry.GeometryOfSlice;
import org.weasis.dicom.viewer2d.mpr.VolumeShort;

/** Stacks 2D rasters into a volume ordered by ImagePositionPatient Z. */
public class FusionVolumeBuilder {

  public FusionStack build(Attributes[] datasets, double[][][] pixels) {
    if (datasets == null || pixels == null || datasets.length != pixels.length) {
      return FusionStack.empty();
    }
    List<Slice> slices = new ArrayList<>();
    String forUid = "";
    for (int i = 0; i < datasets.length; i++) {
      if (pixels[i] == null || pixels[i].length == 0 || pixels[i][0] == null) {
        continue;
      }
      Attributes dcm = datasets[i];
      GeometryOfSlice geom = GeometryOfSlice.fromDataset(dcm);
      double z = geom == null ? i : geom.getTlhc()[2];
      if (forUid.isEmpty() && dcm != null) {
        String uid = dcm.getString(Tag.FrameOfReferenceUID, "");
        if (uid != null) {
          forUid = uid;
        }
      }
      slices.add(new Slice(z, pixels[i]));
    }
    if (slices.isEmpty()) {
      return FusionStack.empty();
    }
    slices.sort(Comparator.comparingDouble(s -> s.z));
    int sizeZ = slices.size();
    int sizeY = slices.get(0).plane.length;
    int sizeX = slices.get(0).plane[0].length;
    VolumeShort volume = new VolumeShort(sizeX, sizeY, sizeZ);
    double[] zMm = new double[sizeZ];
    for (int z = 0; z < sizeZ; z++) {
      Slice slice = slices.get(z);
      zMm[z] = slice.z;
      double[][] plane = slice.plane;
      for (int y = 0; y < sizeY && y < plane.length; y++) {
        double[] row = plane[y];
        if (row == null) {
          continue;
        }
        for (int x = 0; x < sizeX && x < row.length; x++) {
          volume.setValue(x, y, z, row[x]);
        }
      }
    }
    return new FusionStack(volume, zMm, forUid);
  }

  private static final class Slice {
    private final double z;
    private final double[][] plane;

    private Slice(double z, double[][] plane) {
      this.z = z;
      this.plane = plane;
    }
  }
}
