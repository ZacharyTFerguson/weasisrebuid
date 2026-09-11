/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.codec.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.image.util.WindLevelParameters;

class DicomMediaUtilsTest {

  @Test
  void signedPixelsAreNotTreatedAsUnsigned() {
    Attributes dcm = new Attributes();
    dcm.setInt(Tag.PixelRepresentation, VR.US, 1);
    assertTrue(DicomMediaUtils.isSignedPixel(dcm));
    dcm.setInt(Tag.PixelRepresentation, VR.US, 0);
    assertFalse(DicomMediaUtils.isSignedPixel(dcm));
  }

  @Test
  void windowLevelFromHeader() {
    Attributes dcm = new Attributes();
    dcm.setString(Tag.PhotometricInterpretation, VR.CS, "MONOCHROME2");
    dcm.setDouble(Tag.WindowWidth, VR.DS, 400);
    dcm.setDouble(Tag.WindowCenter, VR.DS, 40);
    WindLevelParameters wl = DicomMediaUtils.windowLevel(dcm, 1, 0);
    assertEquals(400, wl.getWindow(), 1e-9);
    assertEquals(40, wl.getLevel(), 1e-9);
    assertTrue(DicomMediaUtils.isMonochrome2(dcm));
  }

  @Test
  void missingPaddingIsSentinel() {
    assertEquals(Integer.MIN_VALUE, DicomMediaUtils.pixelPaddingValue(new Attributes()));
  }

  @Test
  void axialPlanFromImageOrientationPatient() {
    Attributes dcm = new Attributes();
    dcm.setDouble(Tag.ImageOrientationPatient, VR.DS, 1, 0, 0, 0, 1, 0);
    assertEquals("TRANSVERSE", DicomMediaUtils.planLabel(dcm));
  }
}
