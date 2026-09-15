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

import org.dcm4che3.data.UID;
import org.junit.jupiter.api.Test;

class DicomMimeTest {

  @Test
  void routesSopClassToMimeFamily() {
    assertEquals(DicomMime.IMAGE_DICOM, DicomMime.fromSopClass(UID.CTImageStorage));
    assertEquals(
        DicomMime.PR_DICOM, DicomMime.fromSopClass(UID.GrayscaleSoftcopyPresentationStateStorage));
    assertEquals(DicomMime.KO_DICOM, DicomMime.fromSopClass(UID.KeyObjectSelectionDocumentStorage));
    assertEquals(DicomMime.SEG_DICOM, DicomMime.fromSopClass(UID.SegmentationStorage));
    assertEquals(DicomMime.ENCAP_DICOM, DicomMime.fromSopClass(UID.EncapsulatedPDFStorage));
    assertEquals(DicomMime.VIDEO_DICOM, DicomMime.fromSopClass(UID.VideoEndoscopicImageStorage));
    assertEquals(DicomMime.SR_DICOM, DicomMime.fromSopClass(UID.BasicTextSRStorage));
    assertEquals(DicomMime.AU_DICOM, DicomMime.fromSopClass(UID.BasicVoiceAudioWaveformStorage));
    assertEquals(DicomMime.WAVE_DICOM, DicomMime.fromSopClass(UID.TwelveLeadECGWaveformStorage));
    assertEquals(DicomMime.RT_DICOM, DicomMime.fromSopClass(UID.RTStructureSetStorage));
    assertEquals(DicomMime.UNREADABLE_DICOM, DicomMime.fromSopClass(null));
  }
}
