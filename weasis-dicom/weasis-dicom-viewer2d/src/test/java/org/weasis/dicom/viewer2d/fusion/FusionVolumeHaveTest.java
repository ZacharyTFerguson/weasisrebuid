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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;
import org.junit.jupiter.api.Test;
import org.weasis.dicom.viewer2d.mpr.Volume;

class FusionVolumeHaveTest {

  @Test
  void stacksSlicesByImagePositionPatientZ() {
    Attributes[] ds = new Attributes[] {axial("1.2.FOR", 10.0, 2, 2), axial("1.2.FOR", 0.0, 2, 2)};
    double[][][] pixels = new double[][][] {plane(7), plane(3)};
    FusionStack stack = new FusionVolumeBuilder().build(ds, pixels);
    assertFalse(stack.isEmpty());
    assertEquals("1.2.FOR", stack.frameOfReferenceUID());
    assertEquals(0.0, stack.zMm()[0], 1e-9);
    assertEquals(10.0, stack.zMm()[1], 1e-9);
    Volume vol = stack.volume();
    assertEquals(2, vol.sizeZ());
    assertEquals(3.0, vol.value(0, 0, 0));
    assertEquals(7.0, vol.value(0, 0, 1));
  }

  @Test
  void matchesNearestOverlaySliceAndResamplesOntoDenserCt() {
    FusionStack pet =
        new FusionVolumeBuilder()
            .build(
                new Attributes[] {axial("1.2.FOR", 0.0, 2, 2), axial("1.2.FOR", 10.0, 2, 2)},
                new double[][][] {plane(11), plane(22)});
    FusionStack ct =
        new FusionVolumeBuilder()
            .build(
                new Attributes[] {
                  axial("1.2.FOR", 0.0, 2, 2),
                  axial("1.2.FOR", 5.0, 2, 2),
                  axial("1.2.FOR", 10.0, 2, 2),
                  axial("1.2.FOR", 15.0, 2, 2)
                },
                new double[][][] {plane(1), plane(1), plane(1), plane(1)});
    assertTrue(
        new FusionCompatibility()
            .compatible(pet.frameOfReferenceUID(), ct.frameOfReferenceUID(), pet.zMm(), ct.zMm()));
    int[] idx = new FusionSliceMatcher().matchAll(ct.zMm(), pet.zMm(), 6.0);
    assertArrayEquals(new int[] {0, 0, 1, 1}, idx);
    Volume onto = new FusionVolumeResampler().onto(pet, ct, FusionRegistration.identity());
    assertEquals(4, onto.sizeZ());
    assertEquals(11.0, onto.value(0, 0, 0));
    assertEquals(11.0, onto.value(0, 0, 1));
    assertEquals(22.0, onto.value(0, 0, 2));
    assertEquals(22.0, onto.value(0, 0, 3));
  }

  @Test
  void voxelShiftMovesOverlayOnTheReferenceGrid() {
    FusionStack pet =
        new FusionVolumeBuilder()
            .build(new Attributes[] {axial("1.2.FOR", 0.0, 2, 2)}, new double[][][] {hotAt(0, 0)});
    FusionStack ct =
        new FusionVolumeBuilder()
            .build(new Attributes[] {axial("1.2.FOR", 0.0, 2, 2)}, new double[][][] {plane(0)});
    Volume identity = new FusionVolumeResampler().onto(pet, ct, FusionRegistration.identity());
    assertEquals(9.0, identity.value(0, 0, 0));
    assertEquals(0.0, identity.value(1, 0, 0));
    Volume shifted =
        new FusionVolumeResampler().onto(pet, ct, FusionRegistration.voxelShift(1, 0, 0));
    assertEquals(0.0, shifted.value(0, 0, 0));
    assertEquals(9.0, shifted.value(1, 0, 0));
    assertEquals(1.0, FusionRegistration.voxelShift(1, 0, 0).matrix4()[3]);
  }

  @Test
  void rejectsDifferentFrameOfReference() {
    assertFalse(new FusionCompatibility().sameFrameOfReference("1.2.A", "1.2.B"));
    assertFalse(new FusionCompatibility().overlappingZ(new double[] {0, 1}, new double[] {8, 9}));
    assertEquals(0.7, new FusionOp().blend(1.0, 0.0, 0.3), 1e-9);
  }

  private static Attributes axial(String forUid, double z, int rows, int cols) {
    Attributes a = new Attributes();
    a.setString(Tag.FrameOfReferenceUID, VR.UI, forUid);
    a.setDouble(Tag.ImagePositionPatient, VR.DS, 0.0, 0.0, z);
    a.setDouble(Tag.ImageOrientationPatient, VR.DS, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0);
    a.setDouble(Tag.PixelSpacing, VR.DS, 1.0, 1.0);
    a.setDouble(Tag.SliceThickness, VR.DS, 1.0);
    a.setInt(Tag.Rows, VR.US, rows);
    a.setInt(Tag.Columns, VR.US, cols);
    return a;
  }

  private static double[][] plane(double v) {
    return new double[][] {{v, v}, {v, v}};
  }

  private static double[][] hotAt(int x, int y) {
    double[][] p = plane(0);
    p[y][x] = 9;
    return p;
  }
}
