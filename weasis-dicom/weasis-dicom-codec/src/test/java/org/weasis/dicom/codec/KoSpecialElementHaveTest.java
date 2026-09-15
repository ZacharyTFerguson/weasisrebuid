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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;
import org.junit.jupiter.api.Test;

class KoSpecialElementHaveTest {

  @Test
  void previewBuildsKeyObjectNotRejection() {
    Attributes dcm = ko("2.25.img", "113000", "Of Interest");
    dcm.setString(Tag.ContentDescription, VR.LO, "Key images");
    KOSpecialElement ko =
        (KOSpecialElement) new DicomMediaIO(dcm, UID.ExplicitVRLittleEndian).getPreview();
    assertFalse(ko.isRejectionNote());
    assertTrue(ko.isKeyObjectSelection());
    assertEquals("Key images", ko.getDocumentTitle());
    assertTrue(ko.isSopInstanceReferenced("2.25.img"));
    assertEquals(DicomMime.KO_DICOM, ko.getMimeType());
  }

  @Test
  void rejectionConceptBecomesRejectedKoAndHidesReferencedSop() {
    Attributes dcm = ko("2.25.hide", "113001", "Rejected for Quality Reasons");
    DicomMediaIO io = new DicomMediaIO(dcm, UID.ExplicitVRLittleEndian);
    assertInstanceOf(RejectedKOSpecialElement.class, io.getPreview());
    RejectedKOSpecialElement rejected = (RejectedKOSpecialElement) io.getPreview();
    assertTrue(rejected.isRejectionNote());
    assertTrue(rejected.hides("2.25.hide"));
    assertFalse(rejected.hides("2.25.other"));
    assertEquals("Rejected for Quality Reasons", rejected.getDocumentTitle());
    assertTrue(RejectedKOSpecialElement.isRejection(dcm));
    assertFalse(RejectedKOSpecialElement.isRejection(ko("2.25.img", "113000", "Of Interest")));
  }

  static Attributes ko(String sop, String code, String meaning) {
    Attributes dcm = new Attributes();
    dcm.setString(Tag.SOPClassUID, VR.UI, UID.KeyObjectSelectionDocumentStorage);
    dcm.setString(Tag.SOPInstanceUID, VR.UI, "2.25.ko");
    dcm.setString(Tag.Modality, VR.CS, "KO");
    Sequence name = dcm.newSequence(Tag.ConceptNameCodeSequence, 1);
    Attributes concept = new Attributes();
    concept.setString(Tag.CodeValue, VR.SH, code);
    concept.setString(Tag.CodingSchemeDesignator, VR.SH, "DCM");
    concept.setString(Tag.CodeMeaning, VR.LO, meaning);
    name.add(concept);
    Sequence series = dcm.newSequence(Tag.ReferencedSeriesSequence, 1);
    Attributes seriesItem = new Attributes();
    Sequence sops = seriesItem.newSequence(Tag.ReferencedSOPSequence, 1);
    Attributes ref = new Attributes();
    ref.setString(Tag.ReferencedSOPClassUID, VR.UI, UID.CTImageStorage);
    ref.setString(Tag.ReferencedSOPInstanceUID, VR.UI, sop);
    sops.add(ref);
    series.add(seriesItem);
    return dcm;
  }
}
