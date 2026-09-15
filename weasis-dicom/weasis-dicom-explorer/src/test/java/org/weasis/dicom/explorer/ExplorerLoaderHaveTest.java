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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;
import org.dcm4che3.media.DicomDirWriter;
import org.dcm4che3.media.RecordFactory;
import org.dcm4che3.util.UIDUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.core.api.media.data.MediaSeries;
import org.weasis.core.api.service.UICore;
import org.weasis.core.ui.editor.MimeSystemAppViewer;
import org.weasis.core.ui.editor.image.ViewerPlugin;
import org.weasis.dicom.codec.DicomMime;
import org.weasis.dicom.explorer.exp.ExplorerTask;
import org.weasis.dicom.explorer.imp.DicomDirImport;

class ExplorerLoaderHaveTest {

  @Test
  void loadDicomOpensPart10IntoDicomModel(@TempDir Path dir) throws Exception {
    File ct = dir.resolve("ct.dcm").toFile();
    LoadLocalDicomTest.writeCt(ct);
    DicomModel model = new DicomModel();
    LoadDicom loader = new LoadDicom(model, ct);
    assertInstanceOf(ExplorerTask.class, loader);
    var result = loader.load();
    assertEquals(1, result.imported().size());
    assertEquals(1, model.getInstances().size());
    assertEquals("SYN-CT-0001", model.getInstances().getFirst().patientId());
    assertSame(model, loader.getDicomModel());
  }

  @Test
  void loadDicomDirResolvesReferencedPart10(@TempDir Path dir) throws Exception {
    File dicomdir = writeDicomDir(dir);
    DicomModel model = new DicomModel();
    LoadDicomDir loader = new LoadDicomDir(model, dicomdir, new SkipUnsupportedSopNotifier());
    assertInstanceOf(ExplorerTask.class, loader);
    var result = loader.load();
    assertTrue(result.imported().size() >= 1, "DICOMDIR should resolve CT");
    assertEquals(result.imported().size(), model.getInstances().size());

    DicomModel viaType = new DicomModel();
    var again = DicomDirImport.read(dicomdir, viaType, new SkipUnsupportedSopNotifier());
    assertTrue(again.imported().size() >= 1);

    DicomModel viaPath = new DicomModel();
    var fromFolder =
        new LoadDicom(viaPath, List.of(dir.toFile()), null, new SkipUnsupportedSopNotifier())
            .load();
    assertTrue(fromFolder.imported().size() >= 1);
  }

  @Test
  void mimeSystemAppFactoryReadsPdfAndVideoNotImageDicom() {
    MimeSystemAppFactory factory = new MimeSystemAppFactory();
    assertTrue(factory.canReadMimeType("application/pdf"));
    assertTrue(factory.canReadMimeType("video/mp4"));
    assertTrue(factory.canReadMimeType("video/mpeg"));
    assertTrue(factory.canReadMimeType(DicomMime.VIDEO_DICOM));
    assertTrue(factory.canReadMimeType(DicomMime.ENCAP_DICOM));
    assertFalse(factory.canReadMimeType(DicomMime.IMAGE_DICOM));
    assertFalse(factory.canReadMimeType(DicomMime.SERIES_DICOM));
    assertFalse(factory.canReadMimeType(DicomMime.APPLICATION_DICOM));
    assertFalse(factory.canReadMimeType(null));
    assertEquals(100, factory.getLevel());
    assertInstanceOf(MimeSystemAppViewer.class, factory.createSeriesViewer(null));
  }

  @Test
  void seriesHandlerGroupsAndRoutesPdfToSystemAppNotImageDicom() {
    ImportedInstance ct =
        instance("2.25.s1", "2.25.i1", UID.CTImageStorage, "CT", DicomMime.IMAGE_DICOM);
    ImportedInstance same =
        instance("2.25.s1", "2.25.i2", UID.CTImageStorage, "CT", DicomMime.IMAGE_DICOM);
    ImportedInstance pdf =
        instance("2.25.s2", "2.25.i3", UID.EncapsulatedPDFStorage, "DOC", "application/pdf");
    DicomSeriesHandler handler = new DicomSeriesHandler();
    List<DicomSeriesHandler.SeriesBucket> buckets = handler.group(List.of(ct, same, pdf));
    assertEquals(2, buckets.size());
    DicomSeriesHandler.SeriesBucket image =
        buckets.stream().filter(b -> "2.25.s1".equals(b.seriesUid())).findFirst().orElseThrow();
    DicomSeriesHandler.SeriesBucket doc =
        buckets.stream().filter(b -> "2.25.s2".equals(b.seriesUid())).findFirst().orElseThrow();
    assertEquals(2, image.instances().size());
    assertEquals(DicomMime.IMAGE_DICOM, image.mime());
    assertEquals("application/pdf", doc.mime());

    MimeSystemAppFactory mime = new MimeSystemAppFactory();
    MediaSeries<?> imageSeries = handler.toMediaSeries(image);
    MediaSeries<?> pdfSeries = handler.toMediaSeries(doc);
    assertFalse(mime.canReadSeries(imageSeries));
    assertTrue(mime.canReadSeries(pdfSeries));

    UICore core = new UICore();
    core.registerSeriesViewerFactory(mime);
    ViewerPlugin<?> opened = handler.open(doc, core);
    assertNotNull(opened);
    assertTrue(mime.isViewerCreatedByThisFactory(opened));
    assertNull(handler.open(image, core));
    core.closeViewerPlugin(opened);
    core.unregisterSeriesViewerFactory(mime);
  }

  @Test
  void hangingProtocolsLayoutByModality() {
    HangingProtocols hp = new HangingProtocols();
    assertEquals(1, hp.layoutFor("CT").rows());
    assertEquals(1, hp.layoutFor("CT").columns());
    assertEquals(2, hp.layoutFor("MG").rows());
    assertEquals(2, hp.layoutFor("MG").columns());
    assertEquals(4, hp.layoutFor("MG").viewCount());
    assertEquals(2, hp.layoutFor("DX").columns());
    ImportedInstance a =
        new ImportedInstance(
            "SYNTHETIC^A",
            "SYN-1",
            "2.25.study",
            "2.25.s9",
            "2.25.i9",
            UID.CTImageStorage,
            "CT",
            "later",
            "20260101",
            9,
            1,
            null,
            DicomMime.IMAGE_DICOM);
    ImportedInstance b =
        new ImportedInstance(
            "SYNTHETIC^A",
            "SYN-1",
            "2.25.study",
            "2.25.s8",
            "2.25.i8",
            UID.CTImageStorage,
            "CT",
            "earlier",
            "20260101",
            1,
            1,
            null,
            DicomMime.IMAGE_DICOM);
    List<ImportedInstance> ordered = hp.orderSeries(List.of(a, b));
    assertEquals("2.25.s8", ordered.getFirst().seriesUid());
    assertEquals("CT", hp.dominantModality(List.of(a, b)));
    assertEquals("CT 1x1", hp.apply(List.of(a)).name());
  }

  @Test
  void seriesHandlerLoadsPart10Files(@TempDir Path dir) throws Exception {
    File ct = dir.resolve("ct.dcm").toFile();
    LoadLocalDicomTest.writeCt(ct);
    DicomModel model = new DicomModel();
    DicomSeriesHandler handler = new DicomSeriesHandler(model);
    var result = handler.handleFiles(List.of(ct));
    assertEquals(1, result.imported().size());
    assertEquals(1, handler.group(model).size());
  }

  static File writeDicomDir(Path dir) throws Exception {
    File ct = dir.resolve("CT0001.dcm").toFile();
    LoadLocalDicomTest.writeCt(ct);
    File dicomdir = dir.resolve("DICOMDIR").toFile();
    String fsUid = UIDUtils.createUID("2.25");
    DicomDirWriter.createEmptyDirectory(dicomdir, fsUid, "SYNTHETIC", null, "ISO_IR 100");
    RecordFactory factory = new RecordFactory();
    factory.loadDefaultConfiguration();
    try (DicomDirWriter writer = DicomDirWriter.open(dicomdir)) {
      Attributes ds = LoadLocalDicomTest.readDs(ct);
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
    return dicomdir;
  }

  static ImportedInstance instance(
      String seriesUid, String sopUid, String sopClass, String modality, String mime) {
    return new ImportedInstance(
        "SYNTHETIC^A",
        "SYN-1",
        "2.25.study",
        seriesUid,
        sopUid,
        sopClass,
        modality,
        "series",
        "20260101",
        seriesUid.hashCode() & 0xff,
        sopUid.hashCode() & 0xff,
        null,
        mime);
  }
}
