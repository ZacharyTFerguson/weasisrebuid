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

import java.nio.file.Path;
import org.dcm4che3.data.UID;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.weasis.dicom.codec.DicomUnderstandingOracle.Verdict;
import org.weasis.dicom.codec.utils.SyntheticDicomFixtures;

/**
 * Synthetic matrix: opened cases that must stay {@code not understood} until rebuild ships decode.
 */
class DicomUnderstandingHonestyMatrixTest {

  record Case(String label, Writer writer, String expectedMime, String expectedDisposition) {
    void write(Path file) throws Exception {
      writer.write(file);
    }
  }

  @FunctionalInterface
  interface Writer {
    void write(Path file) throws Exception;
  }

  static java.util.stream.Stream<Case> notUnderstoodCases() {
    return java.util.stream.Stream.of(
        new Case(
            "implicit VR LE MONOCHROME2",
            f ->
                SyntheticDicomFixtures.writeMonochrome2WithTransferSyntax(
                    f.toFile(), UID.ImplicitVRLittleEndian),
            DicomMime.IMAGE_DICOM,
            DicomUnderstandingOracle.NOT_UNDERSTOOD_DISPOSITION),
        new Case(
            "JPEG baseline MONOCHROME2",
            f ->
                SyntheticDicomFixtures.writeMonochrome2WithTransferSyntax(
                    f.toFile(), UID.JPEGBaseline8Bit),
            DicomMime.IMAGE_DICOM,
            DicomUnderstandingOracle.NOT_UNDERSTOOD_DISPOSITION),
        new Case(
            "explicit VR BE MONOCHROME2",
            f -> SyntheticDicomFixtures.writeMonochrome2ExplicitVrBe(f.toFile()),
            DicomMime.IMAGE_DICOM,
            DicomUnderstandingOracle.NOT_UNDERSTOOD_DISPOSITION),
        new Case(
            "RLE lossless MONOCHROME2",
            f -> SyntheticDicomFixtures.writeMonochrome2Rle(f.toFile()),
            DicomMime.IMAGE_DICOM,
            DicomUnderstandingOracle.NOT_UNDERSTOOD_DISPOSITION),
        new Case(
            "explicit VR LE MONOCHROME1",
            f -> SyntheticDicomFixtures.writeMonochrome1ExplicitVrLe(f.toFile()),
            DicomMime.IMAGE_DICOM,
            DicomUnderstandingOracle.NOT_UNDERSTOOD_DISPOSITION),
        new Case(
            "explicit VR LE RGB",
            f -> SyntheticDicomFixtures.writeRgb(f.toFile()),
            DicomMime.IMAGE_DICOM,
            DicomUnderstandingOracle.NOT_UNDERSTOOD_DISPOSITION),
        new Case(
            "encapsulated PDF",
            f -> SyntheticDicomFixtures.writeEncapsulatedPdf(f.toFile()),
            DicomMime.ENCAP_DICOM,
            DicomUnderstandingOracle.SKIPPED),
        new Case(
            "grayscale presentation state",
            f -> SyntheticDicomFixtures.writePresentationState(f.toFile()),
            DicomMime.PR_DICOM,
            DicomUnderstandingOracle.SKIPPED),
        new Case(
            "segmentation storage",
            f -> SyntheticDicomFixtures.writeSegmentation(f.toFile()),
            DicomMime.SEG_DICOM,
            DicomUnderstandingOracle.SKIPPED),
        new Case(
            "key object selection",
            f -> SyntheticDicomFixtures.writeKeyObject(f.toFile()),
            DicomMime.KO_DICOM,
            DicomUnderstandingOracle.SKIPPED));
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("notUnderstoodCases")
  void syntheticCaseIsHonestlyNotUnderstood(Case case_, @TempDir Path dir) throws Exception {
    Path file = dir.resolve(case_.label.replace(' ', '-') + ".dcm");
    case_.write(file);
    Verdict v = DicomUnderstandingOracle.evaluate(file);
    assertTrue(v.opened(), case_.label + " should parse as Part-10");
    assertFalse(v.understood(), case_.label + " must not claim pixel understanding");
    assertTrue(
        v.reason() != null && v.reason().startsWith(DicomUnderstandingOracle.NOT_UNDERSTOOD),
        () -> case_.label + " reason: " + v.reason());
    assertEquals(case_.expectedMime, v.mime());
    assertEquals(case_.expectedDisposition, v.disposition());
  }
}
