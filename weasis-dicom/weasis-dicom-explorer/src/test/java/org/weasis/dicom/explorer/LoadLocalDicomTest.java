/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;
import org.dcm4che3.io.DicomOutputStream;
import org.dcm4che3.media.DicomDirWriter;
import org.dcm4che3.media.RecordFactory;
import org.dcm4che3.util.UIDUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.dicom.codec.DicomMime;

class LoadLocalDicomTest {

  @Test
  void folderRecurseImportsSyntheticCt(@TempDir Path dir) throws Exception {
    File ct = dir.resolve("a.dcm").toFile();
    writeCt(ct);
    DicomModel model = new DicomModel();
    var result = LoadLocalDicom.importFolder(dir.toFile(), model, new SkipUnsupportedSopNotifier());
    assertEquals(1, result.imported().size());
    assertEquals(1, model.getInstances().size());
    assertTrue(DicomModel.samePatient(model.getInstances().get(0), model.getInstances().get(0)));
  }

  @Test
  void importPageLoadsFolderWithoutShowingChooser(@TempDir Path dir) throws Exception {
    writeCt(dir.resolve("a.dcm").toFile());
    DicomModel model = new DicomModel();
    ImportDicomPage page = new ImportDicomPage("DICOM", 0, model, new SkipUnsupportedSopNotifier());
    page.importFiles(List.of(dir.toFile()), null);
    assertEquals(1, model.getInstances().size());
  }

  @Test
  void passwordZipImports(@TempDir Path dir) throws Exception {
    File ct = dir.resolve("ct.dcm").toFile();
    writeCt(ct);
    File zip = dir.resolve("p.zip").toFile();
    ZipParameters params = new ZipParameters();
    params.setEncryptFiles(true);
    params.setEncryptionMethod(EncryptionMethod.AES);
    try (ZipFile zf = new ZipFile(zip, "secret".toCharArray())) {
      zf.addFile(ct, params);
    }
    DicomModel model = new DicomModel();
    var result = LoadLocalDicom.importZip(zip, "secret", model, new SkipUnsupportedSopNotifier());
    assertEquals(1, result.imported().size());
  }

  @Test
  void dicomdirResolvesReferencedFile(@TempDir Path dir) throws Exception {
    File ct = dir.resolve("CT0001.dcm").toFile();
    writeCt(ct);
    File dicomdir = dir.resolve("DICOMDIR").toFile();
    String fsUid = UIDUtils.createUID("2.25");
    DicomDirWriter.createEmptyDirectory(dicomdir, fsUid, "SYNTHETIC", null, "ISO_IR 100");
    RecordFactory factory = new RecordFactory();
    factory.loadDefaultConfiguration();
    try (DicomDirWriter writer = DicomDirWriter.open(dicomdir)) {
      Attributes ds = readDs(ct);
      Attributes fmi = new Attributes();
      fmi.setString(Tag.MediaStorageSOPClassUID, VR.UI, UID.CTImageStorage);
      fmi.setString(Tag.MediaStorageSOPInstanceUID, VR.UI, ds.getString(Tag.SOPInstanceUID));
      fmi.setString(Tag.TransferSyntaxUID, VR.UI, UID.ExplicitVRLittleEndian);
      String[] fileIDs = writer.toFileIDs(ct);
      Attributes rec = factory.createRecord(ds, fmi, fileIDs);
      Attributes patient = writer.findOrAddPatientRecord(ds);
      Attributes study = writer.findOrAddStudyRecord(patient, ds);
      Attributes series = writer.findOrAddSeriesRecord(study, ds);
      writer.addLowerDirectoryRecord(series, rec);
      writer.commit();
    }
    DicomModel model = new DicomModel();
    var result = LoadLocalDicom.importDicomDir(dicomdir, model, new SkipUnsupportedSopNotifier());
    assertTrue(result.imported().size() >= 1, "DICOMDIR should resolve CT");
    DicomModel viaType = new DicomModel();
    var again =
        org.weasis.dicom.explorer.imp.DicomDirImport.read(
            dicomdir, viaType, new SkipUnsupportedSopNotifier());
    assertTrue(again.imported().size() >= 1);
  }

  @Test
  void skipUnsupportedEncapsulatedPdf() {
    SkipUnsupportedSopNotifier skip = new SkipUnsupportedSopNotifier();
    ImportedInstance pdf =
        new ImportedInstance(
            "SYNTHETIC^A",
            "SYN-1",
            "2.25.1",
            "2.25.2",
            "2.25.3",
            UID.EncapsulatedPDFStorage,
            "DOC",
            "pdf",
            "20260101",
            1,
            1,
            null,
            DicomMime.ENCAP_DICOM);
    assertFalse(skip.offer(pdf));
    assertFalse(skip.informationMessage().isBlank());
  }

  @Test
  void dragDropClassifiesZipAndDir(@TempDir Path dir) throws Exception {
    File folder = dir.toFile();
    assertEquals(DragDropRules.Kind.FOLDER, DragDropRules.classify(folder));
    File zip = dir.resolve("x.zip").toFile();
    zip.createNewFile();
    assertEquals(DragDropRules.Kind.ZIP, DragDropRules.classify(zip));
  }

  @Test
  void dragDropClassifiesPart10MagicWithoutExtension(@TempDir Path dir) throws Exception {
    File raw = dir.resolve("NOEXT").toFile();
    writeCt(raw);
    assertEquals(DragDropRules.Kind.DICOM_FILE, DragDropRules.classify(raw));
  }

  @Test
  void samePatientRequiresNameAndId() {
    ImportedInstance a =
        new ImportedInstance(
            "SYNTHETIC^A",
            "SYN-1",
            "2.25.1",
            "2.25.2",
            "2.25.3",
            UID.CTImageStorage,
            "CT",
            "ct",
            "20260101",
            1,
            1,
            null,
            DicomMime.IMAGE_DICOM);
    ImportedInstance b =
        new ImportedInstance(
            "SYNTHETIC^A",
            "SYN-OTHER",
            "2.25.1",
            "2.25.2",
            "2.25.4",
            UID.CTImageStorage,
            "CT",
            "ct",
            "20260101",
            1,
            2,
            null,
            DicomMime.IMAGE_DICOM);
    assertFalse(DicomModel.samePatient(a, b));
    assertTrue(DicomModel.samePatient(a, a));
  }

  static void writeCt(File dest) throws Exception {
    String sop = UIDUtils.createUID("2.25");
    Attributes fmi = new Attributes();
    fmi.setBytes(Tag.FileMetaInformationVersion, VR.OB, new byte[] {0, 1});
    fmi.setString(Tag.MediaStorageSOPClassUID, VR.UI, UID.CTImageStorage);
    fmi.setString(Tag.MediaStorageSOPInstanceUID, VR.UI, sop);
    fmi.setString(Tag.TransferSyntaxUID, VR.UI, UID.ExplicitVRLittleEndian);
    fmi.setString(Tag.ImplementationClassUID, VR.UI, "2.25.1918");
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
    dcm.setString(Tag.PatientName, VR.PN, "SYNTHETIC^CT");
    dcm.setString(Tag.PatientID, VR.LO, "SYN-CT-0001");
    int[] px = new int[64];
    dcm.setInt(Tag.PixelData, VR.OW, px);
    try (DicomOutputStream out = new DicomOutputStream(dest)) {
      out.writeDataset(fmi, dcm);
    }
  }

  static Attributes readDs(File file) throws Exception {
    try (org.dcm4che3.io.DicomInputStream in = new org.dcm4che3.io.DicomInputStream(file)) {
      in.setIncludeBulkData(org.dcm4che3.io.DicomInputStream.IncludeBulkData.YES);
      return in.readDataset();
    }
  }
}
