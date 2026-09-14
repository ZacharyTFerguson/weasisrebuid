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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.dcm4che3.data.UID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.dicom.codec.DicomUnderstandingOracle.Verdict;
import org.weasis.dicom.codec.utils.SyntheticCtWriter;
import org.weasis.dicom.codec.utils.SyntheticDicomFixtures;

class DicomUnderstandingOracleTest {

  @Test
  void explicitVrLeMonochrome2IsAcceptedWithWindowLevelSamples(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("ct.dcm");
    SyntheticCtWriter.write(file.toFile(), 8, 40, 400);
    Verdict v = DicomUnderstandingOracle.evaluate(file);
    assertTrue(v.opened());
    assertTrue(v.understood());
    assertEquals(DicomUnderstandingOracle.ACCEPTED, v.disposition());
    assertEquals(UID.ExplicitVRLittleEndian, v.transferSyntaxUid());
    assertEquals("EXPLICIT_VR_LE", v.transferSyntax());
    assertEquals(UID.CTImageStorage, v.sopClassUid());
    assertEquals(DicomMime.IMAGE_DICOM, v.mime());
    assertEquals("MONOCHROME2", v.photometric());
    assertEquals(8, v.rows());
    assertEquals(8, v.columns());
    assertEquals(400.0, v.window(), 1e-9);
    assertEquals(40.0, v.level(), 1e-9);
    assertNotNull(v.samples());
    assertTrue(
        v.samples().center() > v.samples().x0y0(),
        "W/L should map higher HU brighter on MONOCHROME2");
    assertNull(v.reason());
    String json = v.toJson();
    assertTrue(json.contains("\"opened\":true"));
    assertTrue(json.contains("\"understood\":true"));
    assertTrue(json.contains("\"disposition\":\"accepted\""));
    assertTrue(json.contains("\"photometric\":\"MONOCHROME2\""));
    assertTrue(json.contains("\"samples\":{"));
    assertFalse(json.contains("SYNTHETIC"));
    assertFalse(json.contains("Patient"));
  }

  @Test
  void missingPathIsNotOpenedAndNotUnderstood(@TempDir Path dir) {
    Path missing = dir.resolve("no-such.dcm");
    Verdict v = DicomUnderstandingOracle.evaluate(missing);
    assertFalse(v.opened());
    assertFalse(v.understood());
    assertEquals(DicomUnderstandingOracle.SKIPPED, v.disposition());
    assertEquals(DicomUnderstandingOracle.NOT_UNDERSTOOD, v.reason());
    assertTrue(v.toJson().contains("\"reason\":\"not understood\""));
  }

  @Test
  void garbageBytesAreNotOpened(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("trash.dcm");
    Files.writeString(file, "not a dicom file");
    Verdict v = DicomUnderstandingOracle.evaluate(file);
    assertFalse(v.opened());
    assertEquals(DicomUnderstandingOracle.NOT_UNDERSTOOD, v.reason());
  }

  @Test
  void encapsulatedPdfIsSkippedNotUnderstood(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("doc.dcm");
    SyntheticDicomFixtures.writeEncapsulatedPdf(file.toFile());
    Verdict v = DicomUnderstandingOracle.evaluate(file);
    assertTrue(v.opened());
    assertFalse(v.understood());
    assertEquals(DicomUnderstandingOracle.SKIPPED, v.disposition());
    assertEquals(DicomMime.ENCAP_DICOM, v.mime());
    assertEquals(UID.EncapsulatedPDFStorage, v.sopClassUid());
    assertEquals(DicomUnderstandingOracle.NOT_UNDERSTOOD, v.reason());
    assertNull(v.samples());
  }

  @Test
  void rgbPhotometricIsOpenedButNotUnderstood(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("rgb.dcm");
    SyntheticDicomFixtures.writeRgb(file.toFile());
    Verdict v = DicomUnderstandingOracle.evaluate(file);
    assertTrue(v.opened());
    assertFalse(v.understood());
    assertEquals(DicomUnderstandingOracle.ACCEPTED, v.disposition());
    assertEquals("RGB", v.photometric());
    assertEquals(UID.ExplicitVRLittleEndian, v.transferSyntaxUid());
    assertEquals(DicomUnderstandingOracle.NOT_UNDERSTOOD, v.reason());
    assertNull(v.samples());
  }

  @Test
  void implicitVrLeMonochrome2IsNotUnderstood(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("impl.dcm");
    SyntheticDicomFixtures.writeMonochrome2WithTransferSyntax(
        file.toFile(), UID.ImplicitVRLittleEndian);
    Verdict v = DicomUnderstandingOracle.evaluate(file);
    assertTrue(v.opened());
    assertFalse(v.understood());
    assertEquals(UID.ImplicitVRLittleEndian, v.transferSyntaxUid());
    assertEquals("IMPLICIT_VR_LE", v.transferSyntax());
    assertEquals("MONOCHROME2", v.photometric());
    assertEquals(DicomUnderstandingOracle.NOT_UNDERSTOOD, v.reason());
  }

  @Test
  void jpegTransferSyntaxIsNotUnderstood(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("jpeg-ts.dcm");
    SyntheticDicomFixtures.writeMonochrome2WithTransferSyntax(file.toFile(), UID.JPEGBaseline8Bit);
    Verdict v = DicomUnderstandingOracle.evaluate(file);
    assertTrue(v.opened());
    assertFalse(v.understood());
    assertEquals("JPEG_BASELINE", v.transferSyntax());
    assertEquals(DicomUnderstandingOracle.NOT_UNDERSTOOD, v.reason());
  }

  @Test
  void cliPrintsJsonToStdout(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("ct.dcm");
    SyntheticCtWriter.write(file.toFile(), 8, 40, 400);
    ByteArrayOutputStream stdout = new ByteArrayOutputStream();
    ByteArrayOutputStream stderr = new ByteArrayOutputStream();
    int code =
        DicomUnderstandingOracle.run(
            new String[] {file.toString()},
            new PrintStream(stdout, true, StandardCharsets.UTF_8),
            new PrintStream(stderr, true, StandardCharsets.UTF_8));
    assertEquals(0, code);
    String json = stdout.toString(StandardCharsets.UTF_8).trim();
    assertTrue(json.startsWith("{"));
    assertTrue(json.contains("\"understood\":true"));
    assertTrue(stderr.toString(StandardCharsets.UTF_8).isEmpty());
  }

  @Test
  void cliUsageWithoutPath() {
    ByteArrayOutputStream stdout = new ByteArrayOutputStream();
    ByteArrayOutputStream stderr = new ByteArrayOutputStream();
    int code =
        DicomUnderstandingOracle.run(
            new String[0],
            new PrintStream(stdout, true, StandardCharsets.UTF_8),
            new PrintStream(stderr, true, StandardCharsets.UTF_8));
    assertEquals(2, code);
    assertTrue(stderr.toString(StandardCharsets.UTF_8).contains("usage:"));
  }
}
