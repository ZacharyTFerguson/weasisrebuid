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
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;
import org.junit.jupiter.api.Test;

class AnonymizationProfileTest {

  @Test
  void replacesPatientIdentifiersAndKeepsPixelsWithoutMutatingSource() {
    Attributes src = new Attributes();
    src.setString(Tag.PatientName, VR.PN, "SYNTHETIC^CASE");
    src.setString(Tag.PatientID, VR.LO, "SYN-0001");
    src.setInt(Tag.PixelData, VR.OW, 1, 2, 3);
    Attributes out = AnonymizationProfile.apply(src);
    assertEquals("ANONYMIZED", out.getString(Tag.PatientName));
    assertEquals("ANONYMIZED", out.getString(Tag.PatientID));
    assertEquals("SYNTHETIC^CASE", src.getString(Tag.PatientName));
    assertTrue(AnonymizationProfile.retainsPixelData());
    assertEquals(3, out.getInts(Tag.PixelData).length);
  }
}
