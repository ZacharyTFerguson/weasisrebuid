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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
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
  void unsigned16BitDoesNotWrapAbove32767() {
    Attributes dcm = new Attributes();
    dcm.setInt(Tag.BitsAllocated, VR.US, 16);
    dcm.setInt(Tag.BitsStored, VR.US, 16);
    dcm.setInt(Tag.PixelRepresentation, VR.US, 0);
    dcm.setInt(Tag.PixelData, VR.OW, 40000, 1000);
    int[] raw = dcm.getInts(Tag.PixelData);
    assertEquals(40000, DicomMediaUtils.storedPixel(dcm, raw[0]));
    assertEquals(1000, DicomMediaUtils.storedPixel(dcm, raw[1]));
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

  @Test
  void voiPresetsFromMultiValueWindowCenter() {
    Attributes dcm = new Attributes();
    dcm.setDouble(Tag.WindowWidth, VR.DS, 400, 1500, 80);
    dcm.setDouble(Tag.WindowCenter, VR.DS, 40, 300, 40);
    List<WindLevelParameters> presets = DicomMediaUtils.voiPresets(dcm);
    assertEquals(3, presets.size());
    assertEquals(400, presets.get(0).getWindow(), 1e-9);
    assertEquals(1500, presets.get(1).getWindow(), 1e-9);
    assertEquals(80, presets.get(2).getWindow(), 1e-9);
    assertEquals(40, presets.get(0).getLevel(), 1e-9);
    assertTrue(DicomMediaUtils.voiPresets(null).isEmpty());
    assertTrue(DicomMediaUtils.voiPresets(new Attributes()).isEmpty());
  }

  @Test
  void dataRangeWindowLevelUsesPixelMinMax() {
    Attributes dcm = new Attributes();
    dcm.setInt(Tag.BitsAllocated, VR.US, 16);
    dcm.setInt(Tag.BitsStored, VR.US, 16);
    dcm.setInt(Tag.PixelRepresentation, VR.US, 0);
    dcm.setInt(Tag.PixelData, VR.OW, 0, 50, 100, 200);
    WindLevelParameters range = DicomMediaUtils.dataRangeWindowLevel(dcm);
    assertEquals(200, range.getWindow(), 1e-9);
    assertEquals(100, range.getLevel(), 1e-9);
    dcm.setDouble(Tag.WindowWidth, VR.DS, 80);
    dcm.setDouble(Tag.WindowCenter, VR.DS, 40);
    WindLevelParameters file = DicomMediaUtils.windowLevel(dcm, 1, 0);
    assertEquals(80, file.getWindow(), 1e-9);
    assertNotNull(range);
  }
}
