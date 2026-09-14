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
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;
import org.dcm4che3.io.DicomOutputStream;
import org.dcm4che3.util.UIDUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.dicom.codec.DicomUnderstandingOracle.Verdict;
import org.weasis.dicom.codec.utils.SyntheticCtWriter;

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
    assertTrue(v.reason().startsWith(DicomUnderstandingOracle.NOT_UNDERSTOOD));
    assertTrue(v.reason().contains("missing"));
    assertTrue(v.toJson().contains("\"disposition\":\"skipped\""));
  }

  @Test
  void garbageBytesAreNotOpened(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("trash.dcm");
    Files.writeString(file, "not a dicom file");
    Verdict v = DicomUnderstandingOracle.evaluate(file);
    assertFalse(v.opened());
    assertTrue(v.reason().contains("cannot parse Part-10"));
  }

  @Test
  void encapsulatedPdfIsSkippedNotUnderstood(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("doc.dcm");
    writeEncapsulatedPdf(file.toFile());
    Verdict v = DicomUnderstandingOracle.evaluate(file);
    assertTrue(v.opened());
    assertFalse(v.understood());
    assertEquals(DicomUnderstandingOracle.SKIPPED, v.disposition());
    assertEquals(DicomMime.ENCAP_DICOM, v.mime());
    assertEquals(UID.EncapsulatedPDFStorage, v.sopClassUid());
    assertTrue(v.reason().contains("encapsulated document"));
    assertNull(v.samples());
  }

  @Test
  void rgbPhotometricIsOpenedButNotUnderstood(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("rgb.dcm");
    writeRgb(file.toFile());
    Verdict v = DicomUnderstandingOracle.evaluate(file);
    assertTrue(v.opened());
    assertFalse(v.understood());
    assertEquals(DicomUnderstandingOracle.NOT_UNDERSTOOD_DISPOSITION, v.disposition());
    assertEquals("RGB", v.photometric());
    assertEquals(UID.ExplicitVRLittleEndian, v.transferSyntaxUid());
    assertTrue(v.reason().contains("MONOCHROME2"));
    assertFalse(v.toJson().contains("\"disposition\":\"accepted\""));
    assertNull(v.samples());
  }

  @Test
  void implicitVrLeMonochrome2IsNotUnderstood(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("impl.dcm");
    writeImplicitMonochrome2(file.toFile());
    Verdict v = DicomUnderstandingOracle.evaluate(file);
    assertTrue(v.opened());
    assertFalse(v.understood());
    assertEquals(UID.ImplicitVRLittleEndian, v.transferSyntaxUid());
    assertEquals("IMPLICIT_VR_LE", v.transferSyntax());
    assertEquals("MONOCHROME2", v.photometric());
    assertEquals(DicomUnderstandingOracle.NOT_UNDERSTOOD_DISPOSITION, v.disposition());
    assertTrue(v.reason().contains("IMPLICIT_VR_LE"));
  }

  @Test
  void jpegTransferSyntaxIsNotUnderstood(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("jpeg-ts.dcm");
    writeWithTransferSyntax(file.toFile(), UID.JPEGBaseline8Bit);
    Verdict v = DicomUnderstandingOracle.evaluate(file);
    assertTrue(v.opened());
    assertFalse(v.understood());
    assertEquals("JPEG_BASELINE", v.transferSyntax());
    assertEquals(DicomUnderstandingOracle.NOT_UNDERSTOOD_DISPOSITION, v.disposition());
    assertTrue(v.reason().contains("JPEG_BASELINE"));
  }

  @Test
  void acceptedDispositionRequiresUnderstood(@TempDir Path dir) throws Exception {
    Path rgb = dir.resolve("rgb.dcm");
    writeRgb(rgb.toFile());
    Verdict rgbVerdict = DicomUnderstandingOracle.evaluate(rgb);
    assertFalse(rgbVerdict.understood());
    assertFalse(DicomUnderstandingOracle.ACCEPTED.equals(rgbVerdict.disposition()));

    Path ct = dir.resolve("ct.dcm");
    SyntheticCtWriter.write(ct.toFile(), 8, 40, 400);
    Verdict ctVerdict = DicomUnderstandingOracle.evaluate(ct);
    assertTrue(ctVerdict.understood());
    assertEquals(DicomUnderstandingOracle.ACCEPTED, ctVerdict.disposition());
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

  static void writeEncapsulatedPdf(File dest) throws Exception {
    String sop = UIDUtils.createUID("2.25");
    Attributes fmi = fmi(UID.EncapsulatedPDFStorage, sop, UID.ExplicitVRLittleEndian);
    Attributes dcm = new Attributes();
    dcm.setString(Tag.SOPClassUID, VR.UI, UID.EncapsulatedPDFStorage);
    dcm.setString(Tag.SOPInstanceUID, VR.UI, sop);
    dcm.setString(Tag.StudyInstanceUID, VR.UI, UIDUtils.createUID("2.25"));
    dcm.setString(Tag.SeriesInstanceUID, VR.UI, UIDUtils.createUID("2.25"));
    dcm.setString(Tag.Modality, VR.CS, "DOC");
    dcm.setString(Tag.MIMETypeOfEncapsulatedDocument, VR.LO, "application/pdf");
    dcm.setBytes(Tag.EncapsulatedDocument, VR.OB, "%PDF-1.4 SYNTHETIC".getBytes());
    write(dest, fmi, dcm, UID.ExplicitVRLittleEndian);
  }

  static void writeRgb(File dest) throws Exception {
    String sop = UIDUtils.createUID("2.25");
    Attributes fmi = fmi(UID.SecondaryCaptureImageStorage, sop, UID.ExplicitVRLittleEndian);
    Attributes dcm = new Attributes();
    dcm.setString(Tag.SOPClassUID, VR.UI, UID.SecondaryCaptureImageStorage);
    dcm.setString(Tag.SOPInstanceUID, VR.UI, sop);
    dcm.setString(Tag.StudyInstanceUID, VR.UI, UIDUtils.createUID("2.25"));
    dcm.setString(Tag.SeriesInstanceUID, VR.UI, UIDUtils.createUID("2.25"));
    dcm.setString(Tag.Modality, VR.CS, "OT");
    dcm.setString(Tag.PhotometricInterpretation, VR.CS, "RGB");
    dcm.setInt(Tag.SamplesPerPixel, VR.US, 3);
    dcm.setInt(Tag.PlanarConfiguration, VR.US, 0);
    dcm.setInt(Tag.Rows, VR.US, 4);
    dcm.setInt(Tag.Columns, VR.US, 4);
    dcm.setInt(Tag.BitsAllocated, VR.US, 8);
    dcm.setInt(Tag.BitsStored, VR.US, 8);
    dcm.setInt(Tag.HighBit, VR.US, 7);
    dcm.setInt(Tag.PixelRepresentation, VR.US, 0);
    dcm.setBytes(Tag.PixelData, VR.OB, new byte[4 * 4 * 3]);
    write(dest, fmi, dcm, UID.ExplicitVRLittleEndian);
  }

  static void writeImplicitMonochrome2(File dest) throws Exception {
    writeWithTransferSyntax(dest, UID.ImplicitVRLittleEndian);
  }

  static void writeWithTransferSyntax(File dest, String ts) throws Exception {
    String sop = UIDUtils.createUID("2.25");
    Attributes fmi = fmi(UID.CTImageStorage, sop, ts);
    Attributes dcm = new Attributes();
    dcm.setString(Tag.SOPClassUID, VR.UI, UID.CTImageStorage);
    dcm.setString(Tag.SOPInstanceUID, VR.UI, sop);
    dcm.setString(Tag.StudyInstanceUID, VR.UI, UIDUtils.createUID("2.25"));
    dcm.setString(Tag.SeriesInstanceUID, VR.UI, UIDUtils.createUID("2.25"));
    dcm.setString(Tag.Modality, VR.CS, "CT");
    dcm.setString(Tag.PhotometricInterpretation, VR.CS, "MONOCHROME2");
    dcm.setInt(Tag.SamplesPerPixel, VR.US, 1);
    dcm.setInt(Tag.Rows, VR.US, 4);
    dcm.setInt(Tag.Columns, VR.US, 4);
    dcm.setInt(Tag.BitsAllocated, VR.US, 16);
    dcm.setInt(Tag.BitsStored, VR.US, 16);
    dcm.setInt(Tag.HighBit, VR.US, 15);
    dcm.setInt(Tag.PixelRepresentation, VR.US, 1);
    dcm.setDouble(Tag.WindowCenter, VR.DS, 40);
    dcm.setDouble(Tag.WindowWidth, VR.DS, 400);
    dcm.setInt(Tag.PixelData, VR.OW, new int[16]);
    write(dest, fmi, dcm, ts);
  }

  static Attributes fmi(String sopClass, String sop, String ts) {
    Attributes fmi = new Attributes();
    fmi.setBytes(Tag.FileMetaInformationVersion, VR.OB, new byte[] {0, 1});
    fmi.setString(Tag.MediaStorageSOPClassUID, VR.UI, sopClass);
    fmi.setString(Tag.MediaStorageSOPInstanceUID, VR.UI, sop);
    fmi.setString(Tag.TransferSyntaxUID, VR.UI, ts);
    fmi.setString(Tag.ImplementationClassUID, VR.UI, "2.25.1918");
    fmi.setString(Tag.ImplementationVersionName, VR.SH, "WEASISREBUILD");
    return fmi;
  }

  static void write(File dest, Attributes fmi, Attributes dcm, String ts) throws Exception {
    // Part-10 FMI is always EVR LE; dataset encoding follows FMI TransferSyntaxUID.
    try (DicomOutputStream out =
        new DicomOutputStream(new FileOutputStream(dest), UID.ExplicitVRLittleEndian)) {
      out.writeDataset(fmi, dcm);
    }
  }
}
