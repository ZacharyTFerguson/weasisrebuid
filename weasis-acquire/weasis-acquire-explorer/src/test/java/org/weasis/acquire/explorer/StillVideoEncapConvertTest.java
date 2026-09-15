/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.explorer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import javax.imageio.ImageIO;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.io.DicomInputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class StillVideoEncapConvertTest {

  @Test
  void stillBecomesJpegLossy(@TempDir Path dir) throws Exception {
    Path png = dir.resolve("still.png");
    BufferedImage img = new BufferedImage(8, 8, BufferedImage.TYPE_INT_RGB);
    img.setRGB(0, 0, 0xFF0000);
    ImageIO.write(img, "png", png.toFile());
    Path dcm = dir.resolve("still.dcm");
    StillToDicom.convert(
        png, dcm, new PatientDemographics("SYNTHETIC^STILL", "SYN-STILL-1", "", "", ""));
    try (DicomInputStream in = new DicomInputStream(dcm.toFile())) {
      Attributes fmi = in.readFileMetaInformation();
      Attributes ds = in.readDataset();
      assertEquals(UID.JPEGBaseline8Bit, fmi.getString(Tag.TransferSyntaxUID));
      assertEquals("01", ds.getString(Tag.LossyImageCompression));
      assertEquals("ISO_10918_1", ds.getString(Tag.LossyImageCompressionMethod));
      assertEquals("SYN-STILL-1", ds.getString(Tag.PatientID));
    }
    assertTrue(StillFormats.isStill(png));
    assertFalse(StillFormats.isStill(dir.resolve("nope.xyz")));
  }

  @Test
  void pdfAndStlBecomeEncapsulated(@TempDir Path dir) throws Exception {
    Path pdf = dir.resolve("doc.pdf");
    Files.write(pdf, new byte[] {'%', 'P', 'D', 'F', '-', '1', '.', '4'});
    Path pdfDcm = dir.resolve("pdf.dcm");
    EncapToDicom.convert(pdf, pdfDcm, PatientDemographics.empty());
    try (DicomInputStream in = new DicomInputStream(pdfDcm.toFile())) {
      Attributes ds = in.readDataset();
      assertEquals(UID.EncapsulatedPDFStorage, ds.getString(Tag.SOPClassUID));
      assertEquals("application/pdf", ds.getString(Tag.MIMETypeOfEncapsulatedDocument));
    }
    Path stl = dir.resolve("box.stl");
    Files.writeString(stl, "solid synthetic\nendsolid synthetic\n");
    Path stlDcm = dir.resolve("stl.dcm");
    EncapToDicom.convert(stl, stlDcm, PatientDemographics.empty());
    try (DicomInputStream in = new DicomInputStream(stlDcm.toFile())) {
      Attributes ds = in.readDataset();
      assertEquals(UID.EncapsulatedSTLStorage, ds.getString(Tag.SOPClassUID));
    }
  }

  @Test
  void videoMpeg2AcceptedAndBaselineAvcRejected(@TempDir Path dir) throws Exception {
    Path vid = dir.resolve("clip.mpg");
    Files.write(vid, new byte[] {0, 0, 1, (byte) 0xBA, 1, 2, 3, 4});
    Path dcm = dir.resolve("vid.dcm");
    VideoToDicom.convert(
        vid, dcm, "video/mpeg", "", "", new Properties(), PatientDemographics.empty());
    try (DicomInputStream in = new DicomInputStream(dcm.toFile())) {
      Attributes fmi = in.readFileMetaInformation();
      assertEquals(UID.MPEG2MPML, fmi.getString(Tag.TransferSyntaxUID));
    }
    assertTrue(VideoProfileGate.accept("video/mpeg", null, null));
    assertTrue(VideoProfileGate.accept("video/mp4", "High", "4.2"));
    assertFalse(VideoProfileGate.accept("video/mp4", "High", "5.1"));
    assertFalse(VideoProfileGate.accept("video/mp4", "Baseline", "3.1"));
    assertTrue(VideoProfileGate.accept("video/hevc", "Main", "5.1"));
    assertTrue(VideoProfileGate.accept("video/hevc", "Main10", "5.1"));
    Properties prefs = new Properties();
    prefs.setProperty("weasis.acquire.video.max.size", "0");
    assertTrue(
        VideoSizeLimit.allowed(10L * 1024 * 1024 * 1024, VideoSizeLimit.maxMegabytes(prefs)));
    prefs.setProperty("weasis.acquire.video.max.size", "1");
    assertFalse(VideoSizeLimit.allowed(2L * 1024 * 1024, VideoSizeLimit.maxMegabytes(prefs)));
    assertEquals(1024, VideoSizeLimit.maxMegabytes(new Properties()));
    assertThrows(
        Exception.class,
        () ->
            VideoToDicom.convert(
                vid, dir.resolve("bad.dcm"), "video/mp4", "Baseline", "3.0", prefs, null));
  }
}
