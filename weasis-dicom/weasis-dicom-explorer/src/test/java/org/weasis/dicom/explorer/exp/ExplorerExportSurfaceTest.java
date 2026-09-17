/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.exp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
import org.weasis.dicom.codec.DicomMime;
import org.weasis.dicom.explorer.DicomModel;
import org.weasis.dicom.explorer.ImportedInstance;
import org.weasis.dicom.explorer.LoadLocalDicom;
import org.weasis.dicom.explorer.SkipUnsupportedSopNotifier;
import org.weasis.dicom.explorer.pr.DicomExportPR;
import org.weasis.dicom.explorer.pr.DicomPrSerializer;

class ExplorerExportSurfaceTest {

  @Test
  void factoryPagesAreLocalExport() {
    DicomExportFactory factory = new DicomExportFactory();
    Hashtable<String, Object> local = new Hashtable<>();
    local.put("title", DicomExportFactory.PAGE_LOCAL);
    assertTrue(factory.createDicomExportPage(local) instanceof LocalExport);
    Hashtable<String, Object> zip = new Hashtable<>();
    zip.put("title", DicomExportFactory.PAGE_ZIP);
    assertEquals(DicomExportFactory.PAGE_ZIP, factory.createDicomExportPage(zip).getTitle());
    Hashtable<String, Object> dir = new Hashtable<>();
    dir.put("title", DicomExportFactory.PAGE_DIR);
    assertTrue(factory.createDicomExportPage(dir) instanceof ExportDicom);
  }

  @Test
  void exportLocalZipAndDicomDirRoundTrip(@TempDir Path dir) throws Exception {
    File ct = dir.resolve("src.dcm").toFile();
    writeCt(ct);
    DicomModel model = new DicomModel();
    LoadLocalDicom.importFile(ct, model, new SkipUnsupportedSopNotifier());
    CheckTreeModel tree = new CheckTreeModel(model);
    assertEquals(1, tree.selectedInstances().size());

    File localDest = dir.resolve("out-files").toFile();
    File copied =
        new LocalExport(DicomExportFactory.PAGE_LOCAL, model).exportDICOM(tree, localDest);
    assertTrue(copied.isDirectory());
    assertTrue(Files.list(copied.toPath()).findAny().isPresent());

    File zip = dir.resolve("out.zip").toFile();
    File zipped = new LocalExport(DicomExportFactory.PAGE_ZIP, model).exportDICOM(tree, zip);
    assertTrue(zipped.isFile());
    try (ZipFile zf = new ZipFile(zipped)) {
      assertFalse(zf.getFileHeaders().isEmpty());
    }

    File dirDest = dir.resolve("out-dir").toFile();
    File dicomdir = new LocalExport(DicomExportFactory.PAGE_DIR, model).exportDICOM(tree, dirDest);
    assertEquals("DICOMDIR", dicomdir.getName());
    DicomModel reimport = new DicomModel();
    var result =
        LoadLocalDicom.importDicomDir(dicomdir, reimport, new SkipUnsupportedSopNotifier());
    assertEquals(1, result.imported().size());
  }

  @Test
  void uncheckingSeriesOmitsInstancesFromZip(@TempDir Path dir) throws Exception {
    File a = dir.resolve("a.dcm").toFile();
    File b = dir.resolve("b.dcm").toFile();
    writeCt(a);
    writeCt(b);
    DicomModel model = new DicomModel();
    LoadLocalDicom.importFile(a, model, new SkipUnsupportedSopNotifier());
    LoadLocalDicom.importFile(b, model, new SkipUnsupportedSopNotifier());
    assertEquals(2, model.getInstances().size());
    String dropSeries = model.getInstances().get(1).seriesUid();
    CheckTreeModel tree = new CheckTreeModel(model);
    tree.setSeriesChecked(dropSeries, false);
    assertEquals(1, tree.selectedInstances().size());
    File zip = dir.resolve("one.zip").toFile();
    new LocalExport(DicomExportFactory.PAGE_ZIP, model).exportDICOM(tree, zip);
    try (ZipFile zf = new ZipFile(zip)) {
      assertEquals(1, zf.getFileHeaders().size());
    }
  }

  @Test
  void dialogToolbarAndPrefGate() {
    DicomModel model = new DicomModel();
    ExportDicomView view = DicomExport.open(null, model);
    assertNotNull(view);
    assertEquals("Export DICOM", view.getTitle());
    assertEquals("export-dicom-dialog", view.getName());
    assertEquals("export-tabs", view.tabs().getName());
    assertEquals("export-tree", view.getExportTree().getName());
    assertEquals(3, view.tabs().getTabCount());
    assertEquals(DicomExportFactory.PAGE_LOCAL, view.tabs().getTitleAt(0));
    assertEquals(DicomExportFactory.PAGE_ZIP, view.tabs().getTitleAt(1));
    assertEquals(DicomExportFactory.PAGE_DIR, view.tabs().getTitleAt(2));
    assertNotNull(view.page(DicomExportFactory.PAGE_LOCAL));
    ExportToolBar bar = new ExportToolBar();
    assertEquals(Insertable.Type.TOOLBAR, bar.getType());
    assertEquals("Export DICOM", bar.getComponentName());
    assertEquals("export-dicom", ((javax.swing.AbstractButton) bar.getComponent(0)).getName());
    DicomExportAction action = new DicomExportAction();
    assertEquals("Export DICOM", action.getValue(javax.swing.Action.NAME));
    String prev = System.getProperty(DicomExportFactory.PREF_EXPORT);
    System.setProperty(DicomExportFactory.PREF_EXPORT, "false");
    try {
      assertFalse(DicomExport.isExportEnabled());
      assertNull(DicomExport.open(null, model));
    } finally {
      if (prev == null) {
        System.clearProperty(DicomExportFactory.PREF_EXPORT);
      } else {
        System.setProperty(DicomExportFactory.PREF_EXPORT, prev);
      }
    }
  }

  @Test
  void explorerTaskCompletes() throws Exception {
    ExplorerTask<Integer, Void> task =
        new ExplorerTask<>("export", true) {
          @Override
          protected Integer doInBackground() {
            return 7;
          }
        };
    assertEquals("export", task.getMessage());
    assertTrue(task.isInterruptible());
    task.execute();
    assertEquals(7, task.get(5, TimeUnit.SECONDS));
  }

  @Test
  void exportPrCopiesPresentationState(@TempDir Path dir) throws Exception {
    Attributes pr = new Attributes();
    pr.setString(Tag.SOPClassUID, VR.UI, UID.GrayscaleSoftcopyPresentationStateStorage);
    pr.setString(Tag.SOPInstanceUID, VR.UI, UIDUtils.createUID("2.25"));
    pr.setString(Tag.Modality, VR.CS, "PR");
    File src = dir.resolve("src-pr.dcm").toFile();
    DicomPrSerializer.write(pr, src);
    DicomModel model = new DicomModel();
    model.addInstance(
        new ImportedInstance(
            "SYNTHETIC^A",
            "SYN-1",
            "2.25.1",
            "2.25.2",
            pr.getString(Tag.SOPInstanceUID),
            UID.GrayscaleSoftcopyPresentationStateStorage,
            "PR",
            "pr",
            "20260101",
            1,
            1,
            src,
            DicomMime.PR_DICOM));
    List<File> written = DicomExportPR.export(model, dir.resolve("pr-out").toFile());
    assertEquals(1, written.size());
    assertTrue(written.get(0).isFile());
  }

  @Test
  void namedExportRunWritesSelectedInstances(@TempDir Path dir) throws Exception {
    File ct = dir.resolve("src.dcm").toFile();
    writeCt(ct);
    DicomModel model = new DicomModel();
    LoadLocalDicom.importFile(ct, model, new SkipUnsupportedSopNotifier());
    ExportDicomView view = DicomExport.open(null, model);
    javax.swing.tree.DefaultMutableTreeNode root =
        (javax.swing.tree.DefaultMutableTreeNode) view.getExportTree().getModel().getRoot();
    assertTrue(root.getChildAt(0).toString().contains("SYNTHETIC^A"));
    assertTrue(root.getChildAt(0).toString().contains("SYN-1"));
    LocalExport local = view.page(DicomExportFactory.PAGE_LOCAL);
    assertEquals("export-path", local.pathField().getName());
    assertEquals("export-run", local.exportButton().getName());
    assertEquals("export-status", local.statusLabel().getName());
    File dest = dir.resolve("out-named").toFile();
    local.pathField().setText(dest.getAbsolutePath());
    local.exportButton().doClick();
    assertTrue(local.statusLabel().getText().startsWith("Exported"));
    assertTrue(Files.list(dest.toPath()).findAny().isPresent());
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
