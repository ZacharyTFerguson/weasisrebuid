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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.dicom.viewer2d.mip.MipView;

class VolImageIOHaveTest {

  @TempDir Path tmp;

  @Test
  void reconstructsOrthogonalSliceMatchingGenerator() {
    VolumeShort vol = new VolumeShort(3, 2, 2);
    vol.setValue(1, 0, 1, 42);
    VolImageIO io = new VolImageIO(vol, MprAxis.AXIAL, 1);
    double[][] samples = io.samples();
    double[][] expected = MPRGenerator.orthogonal(vol, MprAxis.AXIAL, 1);
    assertEquals(expected.length, samples.length);
    assertEquals(expected[0].length, samples[0].length);
    assertEquals(42.0, samples[0][1], 1e-9);
    BufferedImage image = io.getImage();
    assertNotNull(image);
    assertEquals(3, image.getWidth());
    assertEquals(2, image.getHeight());
    assertEquals(42, image.getRaster().getSample(1, 0, 0));
  }

  @Test
  void appliesMipThicknessOnVolumeIo() {
    VolumeShort vol = new VolumeShort(1, 1, 3);
    vol.setValue(0, 0, 0, 1);
    vol.setValue(0, 0, 1, 8);
    vol.setValue(0, 0, 2, 3);
    VolImageIO io = new VolImageIO(vol, MprAxis.AXIAL, 1, MipView.Type.MAX, 3);
    assertEquals(8.0, io.samples()[0][0], 1e-9);
  }

  @Test
  void copyPixelsIsIndependentThenRawRoundTrip() throws Exception {
    VolumeShort vol = new VolumeShort(2, 1, 1);
    vol.setValue(0, 0, 0, 7);
    vol.setValue(1, 0, 0, -3);
    double[][] src = new VolImageIO(vol, MprAxis.AXIAL, 0).samples();
    double[][] copied = new CopyPixelsTask().copy(src);
    assertNotSame(src, copied);
    src[0][0] = 99;
    assertEquals(7.0, copied[0][0], 1e-9);

    RawImageIO raw = new VolImageIO(vol, MprAxis.AXIAL, 0).toRaw();
    assertEquals(7.0, raw.samples()[0][0], 1e-9);
    assertEquals(-3.0, raw.samples()[0][1], 1e-9);
    Path file = tmp.resolve("slice.raw");
    raw.write(file);
    RawImageIO loaded = RawImageIO.read(file);
    assertEquals(7.0, loaded.samples()[0][0], 1e-9);
    assertEquals(-3.0, loaded.samples()[0][1], 1e-9);
  }

  @Test
  void derivedStackMaterializesRawSlice() {
    VolumeShort vol = new VolumeShort(2, 2, 1);
    vol.setValue(0, 1, 0, 11);
    RawImageIO raw = new DerivedStack(vol).rawSlice(MprAxis.AXIAL, 0);
    assertEquals(11.0, raw.samples()[1][0], 1e-9);
  }

  @Test
  void mprViewRebuildImageUsesVolImageIo() {
    VolumeShort vol = new VolumeShort(2, 1, 1);
    vol.setValue(1, 0, 0, 19);
    MprView view = new MprView();
    view.setAxis(MprAxis.AXIAL);
    view.setSliceIndex(0);
    BufferedImage image = view.rebuildImage(vol);
    assertEquals(19, image.getRaster().getSample(1, 0, 0));
  }
}
