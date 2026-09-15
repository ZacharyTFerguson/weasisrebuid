/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.imp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.util.Hashtable;
import net.lingala.zip4j.ZipFile;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;
import org.dcm4che3.io.DicomOutputStream;
import org.dcm4che3.util.UIDUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.core.api.gui.Insertable;
import org.weasis.core.api.media.data.MediaReader;
import org.weasis.dicom.explorer.DicomModel;
import org.weasis.dicom.explorer.ImportDicomDialog;
import org.weasis.dicom.explorer.LocalImportFactory;

class ExplorerImportSurfaceTest {

  @Test
  void factoryPagesAreWeasisImportTypes() {
    DicomImportFactory factory = new DicomImportFactory();
    Hashtable<String, Object> local = new Hashtable<>();
    local.put("title", DicomImportFactory.PAGE_LOCAL);
    assertTrue(factory.createDicomImportPage(local) instanceof LocalImport);
    Hashtable<String, Object> dir = new Hashtable<>();
    dir.put("title", DicomImportFactory.PAGE_DIR);
    assertTrue(factory.createDicomImportPage(dir) instanceof DicomDirImport);
    Hashtable<String, Object> zip = new Hashtable<>();
    zip.put("title", DicomImportFactory.PAGE_ZIP);
    assertTrue(factory.createDicomImportPage(zip) instanceof ImportDicom);
    assertEquals(DicomImportFactory.PAGE_LOCAL, LocalImportFactory.PAGE_LOCAL);
  }

  @Test
  void dicomZipMediaIoReadsPasswordlessZip(@TempDir Path dir) throws Exception {
    File ct = dir.resolve("a.dcm").toFile();
    writeCt(ct);
    File zip = dir.resolve("s.zip").toFile();
    try (ZipFile zf = new ZipFile(zip)) {
      zf.addFile(ct);
    }
    DicomZipCodec codec = new DicomZipCodec();
    assertTrue(codec.isMimeTypeSupported(DicomZipCodec.MIME));
    MediaReader io = codec.getMediaIO(zip.toURI(), DicomZipCodec.MIME, null);
    assertTrue(io instanceof DicomZipMediaIO);
    assertTrue(io.getMediaSeries().size() >= 1);
    DicomModel model = new DicomModel();
    var result = ((DicomZipMediaIO) io).importZip(model, null);
    assertEquals(1, result.imported().size());
  }

  @Test
  void importToolbarAndDialogEntry() {
    ImportToolBar bar = new ImportToolBar();
    assertEquals(Insertable.Type.TOOLBAR, bar.getType());
    assertEquals("Import DICOM", bar.getComponentName());
    ImportDicomDialog dialog = DicomImport.open(null, false);
    assertEquals("Import DICOM", dialog.getTitle());
  }

  static void writeCt(File file) throws Exception {
    Attributes ds = new Attributes();
    ds.setString(Tag.SOPClassUID, VR.UI, UID.CTImageStorage);
    ds.setString(Tag.SOPInstanceUID, VR.UI, UIDUtils.createUID("2.25"));
    ds.setString(Tag.StudyInstanceUID, VR.UI, UIDUtils.createUID("2.25"));
    ds.setString(Tag.SeriesInstanceUID, VR.UI, UIDUtils.createUID("2.25"));
    ds.setString(Tag.PatientName, VR.PN, "SYNTHETIC^A");
    ds.setString(Tag.PatientID, VR.LO, "SYN-1");
    ds.setString(Tag.Modality, VR.CS, "CT");
    Attributes fmi = new Attributes();
    fmi.setString(Tag.MediaStorageSOPClassUID, VR.UI, UID.CTImageStorage);
    fmi.setString(Tag.MediaStorageSOPInstanceUID, VR.UI, ds.getString(Tag.SOPInstanceUID));
    fmi.setString(Tag.TransferSyntaxUID, VR.UI, UID.ExplicitVRLittleEndian);
    try (DicomOutputStream out = new DicomOutputStream(file)) {
      out.writeDataset(fmi, ds);
    }
  }
}
