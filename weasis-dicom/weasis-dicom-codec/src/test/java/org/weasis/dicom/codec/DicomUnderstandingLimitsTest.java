/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.codec;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;
import org.junit.jupiter.api.Test;

class DicomUnderstandingLimitsTest {

  @Test
  void explicitVrLeUncompressedIsTheOnlyUnderstoodTransferSyntax() {
    assertTrue(DicomUnderstandingLimits.isUncompressedExplicitVrLe(UID.ExplicitVRLittleEndian));
    assertFalse(DicomUnderstandingLimits.isUncompressedExplicitVrLe(UID.ImplicitVRLittleEndian));
    assertFalse(DicomUnderstandingLimits.isUncompressedExplicitVrLe(UID.JPEGBaseline8Bit));
    assertFalse(DicomUnderstandingLimits.isUncompressedExplicitVrLe(null));
  }

  @Test
  void gateRequiresMonochrome2AndEvrLe() {
    Attributes mono2 = dataset("MONOCHROME2");
    assertTrue(DicomUnderstandingLimits.canPaintWindowLevel(UID.ExplicitVRLittleEndian, mono2));
    assertFalse(DicomUnderstandingLimits.canPaintWindowLevel(UID.ImplicitVRLittleEndian, mono2));
    assertFalse(
        DicomUnderstandingLimits.canPaintWindowLevel(UID.ExplicitVRLittleEndian, dataset("RGB")));
    assertEquals(
        DicomUnderstandingLimits.UNDERSTOOD_TRANSFER_SYNTAX_UID,
        TransferSyntax.EXPLICIT_VR_LE.uid());
  }

  static Attributes dataset(String photometric) {
    Attributes dcm = new Attributes();
    dcm.setString(Tag.PhotometricInterpretation, VR.CS, photometric);
    return dcm;
  }
}
