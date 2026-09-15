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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import org.dcm4che3.data.UID;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.dicom.codec.DicomUnderstandingOracle.Verdict;
import org.weasis.dicom.codec.utils.SyntheticDicomFixtures;

/**
 * Failing tests that encode rebuild roadmap honesty. Tagged {@code honesty-fail}: excluded from
 * default {@code mvn test}; run with {@code mvn test -Phonesty-gaps}.
 */
@Tag("honesty-fail")
class DicomUnderstandingRebuildGapsTest {

  @Test
  void implicitVrLeMonochrome2ShouldEventuallyBeUnderstood(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("impl.dcm");
    SyntheticDicomFixtures.writeMonochrome2WithTransferSyntax(
        file.toFile(), UID.ImplicitVRLittleEndian);
    Verdict v = DicomUnderstandingOracle.evaluate(file);
    assertTrue(
        v.understood(),
        "Rebuild gap: implicit VR LE MONOCHROME2 W/L is not implemented yet — flip when shipped");
  }

  @Test
  void jpegBaselineMonochrome2ShouldEventuallyBeUnderstood(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("jpeg.dcm");
    SyntheticDicomFixtures.writeMonochrome2WithTransferSyntax(file.toFile(), UID.JPEGBaseline8Bit);
    Verdict v = DicomUnderstandingOracle.evaluate(file);
    assertTrue(
        v.understood(),
        "Rebuild gap: JPEG baseline MONOCHROME2 decode is not implemented yet — flip when shipped");
  }

  @Test
  void explicitVrLeMonochrome1ShouldEventuallyBeUnderstood(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("mono1.dcm");
    SyntheticDicomFixtures.writeMonochrome1ExplicitVrLe(file.toFile());
    Verdict v = DicomUnderstandingOracle.evaluate(file);
    assertTrue(
        v.understood(), "Rebuild gap: MONOCHROME1 W/L is not implemented yet — flip when shipped");
  }
}
