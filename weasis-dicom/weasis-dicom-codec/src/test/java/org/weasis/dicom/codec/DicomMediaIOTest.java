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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.nio.file.Path;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.api.media.data.MediaSeries;
import org.weasis.core.api.media.data.TagW;
import org.weasis.dicom.codec.utils.SyntheticCtWriter;

class DicomMediaIOTest {

  @Test
  void openBuildsPreviewAndSeries(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("ct.dcm");
    SyntheticCtWriter.write(file.toFile(), 6, 40, 400);
    DicomCodec codec = new DicomCodec();
    DicomMediaIO io = DicomMediaIO.open(file.toFile(), codec);
    assertEquals(file.toUri(), io.getUri());
    assertEquals(codec, io.getCodec());
    assertEquals(UID.ExplicitVRLittleEndian, io.getTransferSyntax());
    MediaElement preview = io.getPreview();
    assertEquals(DicomMime.IMAGE_DICOM, preview.getMimeType());
    assertEquals(file.toUri(), preview.getMediaURI());
    String sop = io.getDataset().getString(Tag.SOPInstanceUID, "");
    assertEquals(sop, preview.getTagValue(TagW.SOPInstanceUID));
    MediaSeries<?> series = io.getMediaSeries();
    assertEquals(DicomMime.SERIES_DICOM, series.getMimeType());
    assertEquals(1, series.size());
  }

  @Test
  void videoTransferSyntaxMapsToVideoMime(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("video.dcm");
    DicomUnderstandingOracleCoverageTest.writeMinimalSop(
        file.toFile(), UID.VideoEndoscopicImageStorage, TransferSyntax.MPEG2_ML.uid());
    DicomMediaIO io = DicomMediaIO.open(file.toFile());
    assertEquals(DicomMime.VIDEO_DICOM, io.mimeType());
    assertFalse(io.isExplicitVrLeMonochrome2());
  }

  @Test
  void constructorWithoutUriLeavesNullUri() {
    DicomMediaIO io =
        new DicomMediaIO(new org.dcm4che3.data.Attributes(), UID.ExplicitVRLittleEndian);
    assertNull(io.getUri());
    assertNull(io.getCodec());
    assertNotNull(io.getDataset());
  }

  @Test
  void openFallsBackWhenTransferSyntaxMissingFromStream(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("ct.dcm");
    SyntheticCtWriter.write(file.toFile(), 4, 40, 400);
    DicomMediaIO io = DicomMediaIO.open(file.toFile());
    assertEquals(UID.ExplicitVRLittleEndian, io.getTransferSyntax());
    assertEquals("CT", io.getPreview().getTagValue(TagW.Modality));
  }
}
