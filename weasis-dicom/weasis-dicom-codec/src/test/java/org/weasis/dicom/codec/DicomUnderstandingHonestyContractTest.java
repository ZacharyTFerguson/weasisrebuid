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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.dicom.codec.DicomUnderstandingOracle.Verdict;
import org.weasis.dicom.codec.utils.SyntheticCtWriter;
import org.weasis.dicom.codec.utils.SyntheticDicomFixtures;

/** Verdict invariants: honest {@code not understood} vs {@code understood} semantics. */
class DicomUnderstandingHonestyContractTest {

  @Test
  void nullPathIsSkippedNotUnderstood() {
    Verdict v = DicomUnderstandingOracle.evaluate(null);
    assertFalse(v.opened());
    assertFalse(v.understood());
    assertEquals(DicomUnderstandingOracle.SKIPPED, v.disposition());
    assertTrue(v.reason().startsWith(DicomUnderstandingOracle.NOT_UNDERSTOOD));
  }

  @Test
  void understoodVerdictHasNullReasonAndAcceptedDisposition(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("ct.dcm");
    SyntheticCtWriter.write(file.toFile(), 8, 40, 400);
    Verdict v = DicomUnderstandingOracle.evaluate(file);
    assertTrue(v.understood());
    assertNull(v.reason());
    assertEquals(DicomUnderstandingOracle.ACCEPTED, v.disposition());
    assertTrue(v.samples() != null);
  }

  @Test
  void notUnderstoodVerdictAlwaysCarriesNotUnderstoodReason(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("rgb.dcm");
    SyntheticDicomFixtures.writeRgb(file.toFile());
    Verdict v = DicomUnderstandingOracle.evaluate(file);
    assertTrue(v.opened());
    assertFalse(v.understood());
    assertTrue(v.reason().startsWith(DicomUnderstandingOracle.NOT_UNDERSTOOD));
    assertNull(v.samples());
  }

  @Test
  void jsonNeverContainsPatientOrNameTags(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("ct.dcm");
    SyntheticCtWriter.write(file.toFile(), 4, 40, 400);
    String json = DicomUnderstandingOracle.evaluate(file).toJson();
    assertFalse(json.contains("Patient"));
    assertFalse(json.contains("patient"));
    assertFalse(json.contains("PatientName"));
    assertFalse(json.contains("00100010"));
  }

  @Test
  void skippedMimeFamiliesExcludeRasterImageDicom() {
    assertTrue(DicomUnderstandingOracle.isSkippedMime(null));
    assertTrue(DicomUnderstandingOracle.isSkippedMime(DicomMime.UNREADABLE_DICOM));
    assertTrue(DicomUnderstandingOracle.isSkippedMime(DicomMime.ENCAP_DICOM));
    assertTrue(DicomUnderstandingOracle.isSkippedMime(DicomMime.VIDEO_DICOM));
    assertTrue(DicomUnderstandingOracle.isSkippedMime(DicomMime.PR_DICOM));
    assertTrue(DicomUnderstandingOracle.isSkippedMime(DicomMime.KO_DICOM));
    assertTrue(DicomUnderstandingOracle.isSkippedMime(DicomMime.SEG_DICOM));
    assertFalse(DicomUnderstandingOracle.isSkippedMime(DicomMime.IMAGE_DICOM));
  }

  @Test
  void quoteEscapesControlCharactersInJsonPath() {
    String quoted = DicomUnderstandingOracle.quote("line\nbreak\"slash\\");
    assertTrue(quoted.contains("\\n"));
    assertTrue(quoted.contains("\\\""));
    assertTrue(quoted.contains("\\\\"));
  }

  @Test
  void cliExitCodeOneWhenOpenedButNotUnderstood(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("rgb.dcm");
    SyntheticDicomFixtures.writeRgb(file.toFile());
    ByteArrayOutputStream stdout = new ByteArrayOutputStream();
    ByteArrayOutputStream stderr = new ByteArrayOutputStream();
    int code =
        DicomUnderstandingOracle.run(
            new String[] {file.toString()},
            new PrintStream(stdout, true, StandardCharsets.UTF_8),
            new PrintStream(stderr, true, StandardCharsets.UTF_8));
    assertEquals(1, code);
  }
}
