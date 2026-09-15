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
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.weasis.dicom.viewer2d.mip.MipView;

class MprMipTest {

  @Test
  void maxMinMeanNone() {
    VolumeShort vol = new VolumeShort(2, 2, 3);
    vol.setValue(0, 0, 0, 1);
    vol.setValue(0, 0, 1, 8);
    vol.setValue(0, 0, 2, 3);
    assertEquals(8.0, vol.projectZ(0, 0, 0, 2, MipView.Type.MAX));
    assertEquals(1.0, vol.projectZ(0, 0, 0, 2, MipView.Type.MIN));
    assertEquals(4.0, vol.projectZ(0, 0, 0, 2, MipView.Type.MEAN));
    assertEquals(8.0, vol.projectZ(0, 0, 0, 2, MipView.Type.NONE));
  }

  @Test
  void orthogonalAxesHaveExpectedShape() {
    VolumeShort vol = new VolumeShort(4, 5, 6);
    double[][] axial = MPRGenerator.orthogonal(vol, MprAxis.AXIAL, 2);
    assertEquals(5, axial.length);
    assertEquals(4, axial[0].length);
    double[][] coronal = MPRGenerator.orthogonal(vol, MprAxis.CORONAL, 1);
    assertEquals(6, coronal.length);
    assertEquals(4, coronal[0].length);
    double[][] sagittal = MPRGenerator.orthogonal(vol, MprAxis.SAGITTAL, 1);
    assertEquals(6, sagittal.length);
    assertEquals(5, sagittal[0].length);
  }

  @Test
  void mprFactoryDoesNotStealDicomMime() {
    assertFalse(new MprFactory().canReadMimeType("image/dicom"));
    assertEquals(80, new MprFactory().getLevel());
  }
}
