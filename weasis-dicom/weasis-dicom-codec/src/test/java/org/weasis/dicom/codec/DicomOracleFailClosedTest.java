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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;
import org.dcm4che3.io.DicomOutputStream;
import org.dcm4che3.util.UIDUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Cross-oracle must not false-green when Weasis cannot decode. The CLI exits 0 only when {@code
 * understood} is true; decode/metadata gaps exit 1; missing/unopenable paths exit 2.
 */
class DicomOracleFailClosedTest {

  @Test
  void cliExitsOneWhenOpenedButNotUnderstood_rgb(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("rgb.dcm");
    DicomUnderstandingOracleTest.writeRgb(file.toFile());
    assertEquals(1, runCli(file));
  }

  @Test
  void cliExitsOneWhenJpegTransferSyntax(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("jpeg.dcm");
    DicomUnderstandingOracleTest.writeWithTransferSyntax(file.toFile(), UID.JPEGBaseline8Bit);
    assertEquals(1, runCli(file));
  }

  @Test
  void cliExitsTwoWhenPathMissing(@TempDir Path dir) {
    Path missing = dir.resolve("absent.dcm");
    assertEquals(2, runCli(missing));
  }

  @Test
  void cliExitsOneWhenEvLeMonochrome2PixelDataTooShort(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("truncated.dcm");
    writeTruncatedMonochrome2(file.toFile());
    var verdict = DicomUnderstandingOracle.evaluate(file);
    assertTrue(verdict.opened());
    assertFalse(verdict.understood());
    assertEquals(DicomUnderstandingOracle.SKIPPED, verdict.disposition());
    assertTrue(verdict.reason().startsWith(DicomUnderstandingOracle.NOT_UNDERSTOOD));
    assertEquals(1, runCli(file));
  }

  static int runCli(Path file) {
    ByteArrayOutputStream stdout = new ByteArrayOutputStream();
    ByteArrayOutputStream stderr = new ByteArrayOutputStream();
    return DicomUnderstandingOracle.run(
        new String[] {file.toString()},
        new PrintStream(stdout, true, StandardCharsets.UTF_8),
        new PrintStream(stderr, true, StandardCharsets.UTF_8));
  }

  static void writeTruncatedMonochrome2(File dest) throws Exception {
    String sop = UIDUtils.createUID("2.25");
    Attributes fmi = new Attributes();
    fmi.setBytes(Tag.FileMetaInformationVersion, VR.OB, new byte[] {0, 1});
    fmi.setString(Tag.MediaStorageSOPClassUID, VR.UI, UID.CTImageStorage);
    fmi.setString(Tag.MediaStorageSOPInstanceUID, VR.UI, sop);
    fmi.setString(Tag.TransferSyntaxUID, VR.UI, UID.ExplicitVRLittleEndian);
    Attributes dcm = new Attributes();
    dcm.setString(Tag.SOPClassUID, VR.UI, UID.CTImageStorage);
    dcm.setString(Tag.SOPInstanceUID, VR.UI, sop);
    dcm.setString(Tag.StudyInstanceUID, VR.UI, UIDUtils.createUID("2.25"));
    dcm.setString(Tag.SeriesInstanceUID, VR.UI, UIDUtils.createUID("2.25"));
    dcm.setString(Tag.Modality, VR.CS, "CT");
    dcm.setString(Tag.PhotometricInterpretation, VR.CS, "MONOCHROME2");
    dcm.setInt(Tag.SamplesPerPixel, VR.US, 1);
    dcm.setInt(Tag.Rows, VR.US, 8);
    dcm.setInt(Tag.Columns, VR.US, 8);
    dcm.setInt(Tag.BitsAllocated, VR.US, 16);
    dcm.setInt(Tag.BitsStored, VR.US, 16);
    dcm.setInt(Tag.HighBit, VR.US, 15);
    dcm.setInt(Tag.PixelRepresentation, VR.US, 1);
    dcm.setDouble(Tag.WindowCenter, VR.DS, 40);
    dcm.setDouble(Tag.WindowWidth, VR.DS, 400);
    dcm.setInt(Tag.PixelData, VR.OW, new int[] {0, 1, 2});
    try (DicomOutputStream out = new DicomOutputStream(dest)) {
      out.writeDataset(fmi, dcm);
    }
  }
}
