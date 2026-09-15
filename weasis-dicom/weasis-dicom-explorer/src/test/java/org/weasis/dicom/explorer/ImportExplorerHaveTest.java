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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.core.api.media.data.SeriesThumbnail;
import org.weasis.core.api.service.UICore;
import org.weasis.core.ui.editor.image.ViewerPlugin;
import org.weasis.dicom.explorer.exp.ExplorerTask;
import org.weasis.dicom.explorer.main.DicomTaskManager;
import org.weasis.dicom.explorer.main.LoadingTaskPanel;

class ImportExplorerHaveTest {

  @AfterEach
  void resetSharedState() {
    LocalPersistence.reset();
    DicomTaskManager.getInstance().reset();
  }

  @Test
  void importPageLoadsSharedModelIntoExplorerThumbnailsAndLoadingTask(@TempDir Path dir)
      throws Exception {
    File ct = dir.resolve("hand.dcm").toFile();
    LoadLocalDicomTest.writeCt(ct);
    DicomModel model = LocalPersistence.getDicomModel();
    DicomExplorer explorer = new DicomExplorer(model);
    ImportDicomPage page = new ImportDicomPage("DICOM", 0, model, new SkipUnsupportedSopNotifier());
    page.importFiles(List.of(ct), null);
    assertEquals(1, model.getInstances().size());
    assertEquals(1, explorer.patientPane().getSelectionManager().patientKeys().size());
    assertFalse(explorer.studyPane().getSeriesPane().thumbnails().isEmpty());
    assertInstanceOf(
        SeriesThumbnail.class, explorer.studyPane().getSeriesPane().thumbnails().getFirst());
    assertFalse(DicomTaskManager.getInstance().getTasks().isEmpty());
    assertTrue(DicomTaskManager.getInstance().messages().getFirst().contains("Loading DICOM"));
    assertInstanceOf(ExplorerTask.class, DicomTaskManager.getInstance().getTasks().getFirst());
    LoadingTaskPanel row = DicomTaskManager.getInstance().getLoadingPanel().getRows().getFirst();
    assertEquals("Loading DICOM", row.getMessage());
  }

  @Test
  void factoryExplorerAndImportShareLocalPersistence(@TempDir Path dir) throws Exception {
    File ct = dir.resolve("ct.dcm").toFile();
    LoadLocalDicomTest.writeCt(ct);
    DicomExplorerFactory factory = new DicomExplorerFactory();
    DicomExplorer explorer = (DicomExplorer) factory.createInstance(null);
    assertSame(LocalPersistence.getDicomModel(), explorer.getDicomModel());
    ImportDicomPage page =
        new ImportDicomPage(
            "DICOM", 0, LocalPersistence.getDicomModel(), new SkipUnsupportedSopNotifier());
    page.importFiles(List.of(ct), null);
    assertEquals(1, explorer.getDicomModel().getInstances().size());
    assertEquals(1, explorer.seriesSelection().getItems().size());
  }

  @Test
  void enterOpensSelectedSeriesInViewerPlugin(@TempDir Path dir) throws Exception {
    File ct = dir.resolve("ct.dcm").toFile();
    LoadLocalDicomTest.writeCt(ct);
    DicomViewerPlugin.Factory images = new DicomViewerPlugin.Factory();
    UICore.getInstance().registerSeriesViewerFactory(images);
    try {
      DicomModel model = new DicomModel();
      DicomExplorer explorer = new DicomExplorer(model);
      new ImportDicomPage("DICOM", 0, model, new SkipUnsupportedSopNotifier())
          .importFiles(List.of(ct), null);
      explorer.thumbnailAdapter().pressed(0, false, false);
      List<ViewerPlugin<?>> opened = explorer.openSelected();
      assertEquals(1, opened.size());
      assertInstanceOf(DicomViewerPlugin.class, opened.getFirst());
    } finally {
      UICore.getInstance().unregisterSeriesViewerFactory(images);
    }
  }

  @Test
  void seriesFilterHidesNonMatchingModality() {
    DicomModel model = new DicomModel();
    model.addInstance(
        new ImportedInstance(
            "SYNTHETIC^A",
            "SYN-1",
            "2.25.1",
            "2.25.s1",
            "2.25.i1",
            "1.2.840.10008.10.0.2.2.1.2",
            "CT",
            "chest",
            "20260101",
            1,
            1,
            null,
            "image/dicom"));
    DicomExplorer explorer = new DicomExplorer(model);
    assertEquals(1, explorer.seriesSelection().getItems().size());
    explorer.seriesFilter().setQuery("mr");
    explorer.refresh();
    assertEquals(0, explorer.seriesSelection().getItems().size());
    explorer.seriesFilter().setQuery("ct");
    explorer.refresh();
    assertEquals(1, explorer.seriesSelection().getItems().size());
  }
}
