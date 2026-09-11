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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import org.dcm4che3.data.UID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.dicom.codec.utils.SyntheticCtWriter;

class WindowLevelPaintTest {

  @Test
  void explicitVrLeMonochrome2PaintsWindowLevel(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("ct.dcm");
    SyntheticCtWriter.write(file.toFile(), 8, 40, 400);
    DicomMediaIO io = DicomMediaIO.open(file.toFile());
    assertEquals(UID.ExplicitVRLittleEndian, io.getTransferSyntax());
    assertTrue(io.isExplicitVrLeMonochrome2());
    assertEquals(DicomMime.IMAGE_DICOM, io.mimeType());
    BufferedImage img = io.paintWindowLevel();
    assertEquals(8, img.getWidth());
    assertEquals(8, img.getHeight());
    int center = img.getRaster().getSample(4, 4, 0);
    int black = img.getRaster().getSample(0, 0, 0);
    assertTrue(center > black, "W/L should map higher HU brighter on MONOCHROME2");
    assertTrue(Files.size(file) > 128);
  }
}
