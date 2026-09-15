/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;
import org.dcm4che3.io.DicomOutputStream;
import org.dcm4che3.util.UIDUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.dicom.codec.DicomMime;
import org.weasis.dicom.explorer.DicomModel;
import org.weasis.dicom.explorer.DicomSeriesHandler;
import org.weasis.dicom.explorer.ImportedInstance;
import org.weasis.dicom.explorer.LoadLocalDicom;
import org.weasis.dicom.explorer.SkipUnsupportedSopNotifier;

class SplitSeriesManagerHaveTest {

  @Test
  void mrEchoesWithSameSeriesUidBecomeTwoBuckets() {
    ImportedInstance echo1 = mr("2.25.mr", "2.25.i1", "1", "");
    ImportedInstance echo2 = mr("2.25.mr", "2.25.i2", "2", "");
    List<DicomSeriesHandler.SeriesBucket> buckets =
        new DicomSeriesHandler().group(List.of(echo1, echo2));
    assertEquals(2, buckets.size());
    assertNotEquals(buckets.get(0).seriesUid(), buckets.get(1).seriesUid());
    assertTrue(buckets.get(0).seriesUid().startsWith("2.25.mr."));
    assertTrue(buckets.get(1).seriesUid().startsWith("2.25.mr."));
    assertEquals(1, buckets.get(0).instances().size());
    assertEquals(1, buckets.get(1).instances().size());
  }

  @Test
  void ctWithoutSplitKeysStaysOneBucket() {
    ImportedInstance a = ct("2.25.s1", "2.25.i1");
    ImportedInstance b = ct("2.25.s1", "2.25.i2");
    List<DicomSeriesHandler.SeriesBucket> buckets = new DicomSeriesHandler().group(List.of(a, b));
    assertEquals(1, buckets.size());
    assertEquals("2.25.s1", buckets.getFirst().seriesUid());
    assertEquals(2, buckets.getFirst().instances().size());
  }

  @Test
  void usDoesNotSplitOnFakeEchoFields() {
    ImportedInstance a = us("2.25.us", "2.25.i1", "1");
    ImportedInstance b = us("2.25.us", "2.25.i2", "2");
    List<ImportedInstance> rewritten = new SplitSeriesManager().rewrite(List.of(a, b));
    assertEquals("2.25.us", rewritten.get(0).seriesUid());
    assertEquals("2.25.us", rewritten.get(1).seriesUid());
    assertEquals(1, new DicomSeriesHandler().group(List.of(a, b)).size());
  }

  @Test
  void loadLocalMrEchoesSplitAfterImport(@TempDir Path dir) throws Exception {
    String series = UIDUtils.createUID("2.25");
    String study = UIDUtils.createUID("2.25");
    writeMr(dir.resolve("e1.dcm").toFile(), study, series, "1");
    writeMr(dir.resolve("e2.dcm").toFile(), study, series, "2");
    DicomModel model = new DicomModel();
    LoadLocalDicom.importFolder(dir.toFile(), model, new SkipUnsupportedSopNotifier());
    assertEquals(2, model.getInstances().size());
    assertEquals(
        Set.of("1", "2"),
        model.getInstances().stream().map(ImportedInstance::echoNumber).collect(Collectors.toSet()));
    assertEquals(2, new DicomSeriesHandler().group(model).size());
  }

  static ImportedInstance mr(String seriesUid, String sopUid, String echo, String contrast) {
    return new ImportedInstance(
        "SYNTHETIC^MR",
        "SYN-MR",
        "2.25.study",
        seriesUid,
        sopUid,
        UID.MRImageStorage,
        "MR",
        "echo",
        "20260101",
        1,
        sopUid.hashCode() & 0xff,
        null,
        DicomMime.IMAGE_DICOM,
        echo,
        "",
        contrast);
  }

  static ImportedInstance ct(String seriesUid, String sopUid) {
    return new ImportedInstance(
        "SYNTHETIC^A",
        "SYN-1",
        "2.25.study",
        seriesUid,
        sopUid,
        UID.CTImageStorage,
        "CT",
        "series",
        "20260101",
        1,
        sopUid.hashCode() & 0xff,
        null,
        DicomMime.IMAGE_DICOM);
  }

  static ImportedInstance us(String seriesUid, String sopUid, String echo) {
    return new ImportedInstance(
        "SYNTHETIC^US",
        "SYN-US",
        "2.25.study",
        seriesUid,
        sopUid,
        UID.UltrasoundImageStorage,
        "US",
        "us",
        "20260101",
        1,
        sopUid.hashCode() & 0xff,
        null,
        DicomMime.IMAGE_DICOM,
        echo,
        "",
        "");
  }

  static void writeMr(File dest, String study, String series, String echo) throws Exception {
    String sop = UIDUtils.createUID("2.25");
    Attributes fmi = new Attributes();
    fmi.setBytes(Tag.FileMetaInformationVersion, VR.OB, new byte[] {0, 1});
    fmi.setString(Tag.MediaStorageSOPClassUID, VR.UI, UID.MRImageStorage);
    fmi.setString(Tag.MediaStorageSOPInstanceUID, VR.UI, sop);
    fmi.setString(Tag.TransferSyntaxUID, VR.UI, UID.ExplicitVRLittleEndian);
    fmi.setString(Tag.ImplementationClassUID, VR.UI, "2.25.1918");
    Attributes dcm = new Attributes();
    dcm.setString(Tag.SOPClassUID, VR.UI, UID.MRImageStorage);
    dcm.setString(Tag.SOPInstanceUID, VR.UI, sop);
    dcm.setString(Tag.StudyInstanceUID, VR.UI, study);
    dcm.setString(Tag.SeriesInstanceUID, VR.UI, series);
    dcm.setString(Tag.Modality, VR.CS, "MR");
    dcm.setString(Tag.EchoNumbers, VR.IS, echo);
    dcm.setString(Tag.PhotometricInterpretation, VR.CS, "MONOCHROME2");
    dcm.setInt(Tag.SamplesPerPixel, VR.US, 1);
    dcm.setInt(Tag.Rows, VR.US, 8);
    dcm.setInt(Tag.Columns, VR.US, 8);
    dcm.setInt(Tag.BitsAllocated, VR.US, 16);
    dcm.setInt(Tag.BitsStored, VR.US, 16);
    dcm.setInt(Tag.HighBit, VR.US, 15);
    dcm.setInt(Tag.PixelRepresentation, VR.US, 1);
    dcm.setString(Tag.PatientName, VR.PN, "SYNTHETIC^MR");
    dcm.setString(Tag.PatientID, VR.LO, "SYN-MR-0001");
    dcm.setInt(Tag.PixelData, VR.OW, new int[64]);
    try (DicomOutputStream out = new DicomOutputStream(dest)) {
      out.writeDataset(fmi, dcm);
    }
  }
}
