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
import org.dcm4che3.util.UIDUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.weasis.dicom.codec.DicomUnderstandingOracle.Verdict;
import org.weasis.dicom.codec.utils.SyntheticCtWriter;
import org.weasis.dicom.codec.utils.SyntheticDicomFixtures;

/** Extra oracle branches: JSON escaping, disposition, paint failures, CLI exit codes. */
class DicomUnderstandingOracleCoverageTest {

  @Test
  void nullPathIsClosed() {
    Verdict v = DicomUnderstandingOracle.evaluate(null);
    assertFalse(v.opened());
    assertEquals(DicomUnderstandingOracle.SKIPPED, v.disposition());
    assertTrue(v.toJson().contains("\"samples\":null"));
  }

  @ParameterizedTest
  @CsvSource({
    "unreadable/dicom, true",
    "video/dicom, true",
    "pr/dicom, true",
    "ko/dicom, true",
    "seg/dicom, true",
    "encap/dicom, true",
    "image/dicom, false",
    ", true"
  })
  void skippedMimeFamilies(String mime, boolean skipped) {
    assertEquals(skipped, DicomUnderstandingOracle.isSkippedMime(mime));
  }

  @Test
  void quoteEscapesJsonControlCharacters() {
    String escaped = DicomUnderstandingOracle.quote("tab\there\nline\rreturn\"quote\\slash\u0007");
    assertTrue(escaped.contains("\\t"));
    assertTrue(escaped.contains("\\n"));
    assertTrue(escaped.contains("\\r"));
    assertTrue(escaped.contains("\\\""));
    assertTrue(escaped.contains("\\\\"));
    assertTrue(escaped.contains("\\u0007"));
  }

  @Test
  void jsonEscapesControlCharactersInPath(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("line\"feed.dcm");
    SyntheticCtWriter.write(file.toFile(), 4, 40, 400);
    Verdict v = DicomUnderstandingOracle.evaluate(file);
    String json = v.toJson();
    assertTrue(json.contains("\\\""));
    assertTrue(v.opened());
  }

  @Test
  void corruptPixelDataYieldsSkippedVerdict(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("short-pixels.dcm");
    writeMonochrome2WithShortPixels(file.toFile());
    Verdict v = DicomUnderstandingOracle.evaluate(file);
    assertTrue(v.opened());
    assertFalse(v.understood());
    assertEquals(DicomUnderstandingOracle.SKIPPED, v.disposition());
    assertTrue(v.reason().startsWith(DicomUnderstandingOracle.NOT_UNDERSTOOD));
    assertEquals(400.0, v.window(), 1e-9);
    assertEquals(40.0, v.level(), 1e-9);
  }

  @Test
  void keyObjectDocumentIsSkipped(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("ko.dcm");
    writeMinimalSop(
        file.toFile(), UID.KeyObjectSelectionDocumentStorage, UID.ExplicitVRLittleEndian);
    Verdict v = DicomUnderstandingOracle.evaluate(file);
    assertTrue(v.opened());
    assertEquals(DicomMime.KO_DICOM, v.mime());
    assertEquals(DicomUnderstandingOracle.SKIPPED, v.disposition());
    assertFalse(v.understood());
  }

  @Test
  void presentationStateIsSkipped(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("pr.dcm");
    writeMinimalSop(
        file.toFile(), UID.GrayscaleSoftcopyPresentationStateStorage, UID.ExplicitVRLittleEndian);
    Verdict v = DicomUnderstandingOracle.evaluate(file);
    assertEquals(DicomMime.PR_DICOM, v.mime());
    assertEquals(DicomUnderstandingOracle.SKIPPED, v.disposition());
  }

  @Test
  void videoSopIsSkipped(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("video.dcm");
    writeMinimalSop(file.toFile(), UID.VideoEndoscopicImageStorage, TransferSyntax.MPEG2_ML.uid());
    Verdict v = DicomUnderstandingOracle.evaluate(file);
    assertEquals(DicomMime.VIDEO_DICOM, v.mime());
    assertEquals(DicomUnderstandingOracle.SKIPPED, v.disposition());
    assertEquals("MPEG2_ML", v.transferSyntax());
  }

  @Test
  void cliReturnsOneWhenOpenedButNotUnderstood(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("rgb.dcm");
    SyntheticDicomFixtures.writeRgb(file.toFile());
    ByteArrayOutputStream stdout = new ByteArrayOutputStream();
    int code =
        DicomUnderstandingOracle.run(
            new String[] {file.toString()},
            new PrintStream(stdout, true, StandardCharsets.UTF_8),
            new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8));
    assertEquals(1, code);
    assertTrue(stdout.toString(StandardCharsets.UTF_8).contains("\"understood\":false"));
  }

  @Test
  void cliBlankPathReturnsUsage() {
    ByteArrayOutputStream stderr = new ByteArrayOutputStream();
    int code =
        DicomUnderstandingOracle.run(
            new String[] {"  "},
            new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8),
            new PrintStream(stderr, true, StandardCharsets.UTF_8));
    assertEquals(2, code);
    assertTrue(stderr.toString(StandardCharsets.UTF_8).contains("usage:"));
  }

  static void writeMonochrome2WithShortPixels(File dest) throws Exception {
    String sop = UIDUtils.createUID("2.25");
    Attributes fmi =
        SyntheticDicomFixtures.fmi(UID.CTImageStorage, sop, UID.ExplicitVRLittleEndian);
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
    dcm.setInt(Tag.PixelData, VR.OW, new int[] {0, 1, 2, 3});
    SyntheticDicomFixtures.write(dest, fmi, dcm, UID.ExplicitVRLittleEndian);
  }

  static void writeMinimalSop(File dest, String sopClass, String ts) throws Exception {
    String sop = UIDUtils.createUID("2.25");
    Attributes fmi = SyntheticDicomFixtures.fmi(sopClass, sop, ts);
    Attributes dcm = new Attributes();
    dcm.setString(Tag.SOPClassUID, VR.UI, sopClass);
    dcm.setString(Tag.SOPInstanceUID, VR.UI, sop);
    dcm.setString(Tag.StudyInstanceUID, VR.UI, UIDUtils.createUID("2.25"));
    dcm.setString(Tag.SeriesInstanceUID, VR.UI, UIDUtils.createUID("2.25"));
    dcm.setString(Tag.Modality, VR.CS, "OT");
    SyntheticDicomFixtures.write(dest, fmi, dcm, ts);
  }
}
