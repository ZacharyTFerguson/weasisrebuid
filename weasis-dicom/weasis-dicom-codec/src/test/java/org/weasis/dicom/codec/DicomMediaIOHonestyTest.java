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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.dicom.codec.utils.SyntheticCtWriter;
import org.weasis.dicom.codec.utils.SyntheticDicomFixtures;

/** Clean-room {@link DicomMediaIO} gates align with oracle honesty (EVR LE MONOCHROME2 only). */
class DicomMediaIOHonestyTest {

  @Test
  void explicitVrLeMonochrome2PassesGate(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("ct.dcm");
    SyntheticCtWriter.write(file.toFile(), 8, 40, 400);
    DicomMediaIO io = DicomMediaIO.open(file.toFile());
    assertEquals(UID.ExplicitVRLittleEndian, io.getTransferSyntax());
    assertTrue(io.isExplicitVrLeMonochrome2());
    assertEquals(DicomMime.IMAGE_DICOM, io.mimeType());
  }

  @Test
  void implicitVrLeMonochrome2FailsGate(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("impl.dcm");
    SyntheticDicomFixtures.writeMonochrome2WithTransferSyntax(
        file.toFile(), UID.ImplicitVRLittleEndian);
    DicomMediaIO io = DicomMediaIO.open(file.toFile());
    assertEquals(UID.ImplicitVRLittleEndian, io.getTransferSyntax());
    assertFalse(io.isExplicitVrLeMonochrome2());
  }

  @Test
  void rgbFailsMonochrome2Gate(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("rgb.dcm");
    SyntheticDicomFixtures.writeRgb(file.toFile());
    DicomMediaIO io = DicomMediaIO.open(file.toFile());
    assertFalse(io.isExplicitVrLeMonochrome2());
    assertEquals(DicomMime.IMAGE_DICOM, io.mimeType());
  }

  @Test
  void oracleAndMediaIoAgreeOnSupportedCt(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("ct.dcm");
    SyntheticCtWriter.write(file.toFile(), 4, 40, 400);
    DicomMediaIO io = DicomMediaIO.open(file.toFile());
    DicomUnderstandingOracle.Verdict v = DicomUnderstandingOracle.evaluate(file);
    assertEquals(io.isExplicitVrLeMonochrome2(), v.understood());
  }
}
