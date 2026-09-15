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
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.core.api.image.util.WindLevelParameters;
import org.weasis.dicom.codec.utils.DicomMediaUtils;
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

  @Test
  void unsigned8BitDxBoneIsBrighterThanSoftTissue() {
    Attributes dcm = handDxPattern();
    int[] raw = dcm.getInts(Tag.PixelData);
    assertEquals(40, DicomMediaUtils.storedPixel(dcm, raw[0]));
    assertEquals(220, DicomMediaUtils.storedPixel(dcm, raw[36]));
    WindLevelParameters wl = DicomMediaUtils.windowLevel(dcm, 400, 40);
    assertTrue(wl.getWindow() < 250, "DX without VOI must use data-range W/L, not CT 400/40");
    assertTrue(Math.abs(wl.getLevel() - 130.0) < 20);
    BufferedImage img = WindowLevelPainter.paintMonochrome2(dcm);
    int bone = img.getRaster().getSample(4, 4, 0);
    int soft = img.getRaster().getSample(0, 0, 0);
    assertTrue(bone > soft, "unsigned 8-bit bone (>127) must not sign-wrap to black");
    assertTrue(bone - soft > 80);
  }

  /** Hand DX pattern: 8-bit unsigned MONOCHROME2, no VOI tags, bone > 127. */
  static Attributes handDxPattern() {
    Attributes dcm = new Attributes();
    dcm.setString(Tag.PhotometricInterpretation, VR.CS, "MONOCHROME2");
    dcm.setString(Tag.Modality, VR.CS, "DX");
    dcm.setInt(Tag.SamplesPerPixel, VR.US, 1);
    dcm.setInt(Tag.Rows, VR.US, 8);
    dcm.setInt(Tag.Columns, VR.US, 8);
    dcm.setInt(Tag.BitsAllocated, VR.US, 8);
    dcm.setInt(Tag.BitsStored, VR.US, 8);
    dcm.setInt(Tag.HighBit, VR.US, 7);
    dcm.setInt(Tag.PixelRepresentation, VR.US, 0);
    byte[] px = new byte[64];
    for (int y = 0; y < 8; y++) {
      for (int x = 0; x < 8; x++) {
        px[y * 8 + x] = (x >= 3 && x <= 5) ? (byte) 220 : 40;
      }
    }
    dcm.setBytes(Tag.PixelData, VR.OB, px);
    return dcm;
  }
}
