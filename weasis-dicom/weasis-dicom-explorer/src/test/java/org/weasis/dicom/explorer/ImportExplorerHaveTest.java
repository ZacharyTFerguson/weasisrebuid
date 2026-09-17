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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.core.api.media.data.SeriesThumbnail;
import org.weasis.core.api.service.UICore;
import org.weasis.core.ui.editor.image.ViewerPlugin;
import org.weasis.dicom.explorer.exp.ExplorerTask;
import org.weasis.dicom.explorer.main.DicomTaskManager;
import org.weasis.dicom.explorer.main.LoadingTaskPanel;
import org.weasis.dicom.explorer.main.SeriesFilter;
import org.weasis.dicom.explorer.tag.AbstractTagSearchPanel.TagRow;
import org.weasis.dicom.explorer.tag.DicomFieldsView;
import org.weasis.dicom.viewer2d.View2dContainer;
import org.weasis.dicom.viewer2d.View2dFactory;

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
  void importPutsView2dContainerInWindowCenter(@TempDir Path dir) throws Exception {
    File ct = dir.resolve("series.dcm").toFile();
    LoadLocalDicomTest.writeCt(ct);
    JFrame win = new JFrame();
    UICore core = UICore.getInstance();
    core.setApplicationWindow(win);
    View2dFactory factory = new View2dFactory();
    core.registerSeriesViewerFactory(factory);
    try {
      DicomModel model = LocalPersistence.getDicomModel();
      ImportDicomPage page =
          new ImportDicomPage("DICOM", 0, model, new SkipUnsupportedSopNotifier());
      page.importFiles(List.of(ct), null);
      page.openViewerIfPresent();
      Component center =
          ((BorderLayout) win.getContentPane().getLayout()).getLayoutComponent(BorderLayout.CENTER);
      assertInstanceOf(View2dContainer.class, center);
      View2dContainer tab = (View2dContainer) center;
      assertNotNull(tab.getView2d().getDataset());
      assertSame(tab, core.getSelectedViewerPlugin());
    } finally {
      for (ViewerPlugin<?> plugin : List.copyOf(core.getOpenViewerPlugins())) {
        core.closeViewerPlugin(plugin);
      }
      core.unregisterSeriesViewerFactory(factory);
      core.setApplicationWindow(null);
      win.dispose();
    }
  }

  @Test
  void gogoGetLocalPutsView2dContainerInWindowCenter(@TempDir Path dir) throws Exception {
    File ct = dir.resolve("series.dcm").toFile();
    LoadLocalDicomTest.writeCt(ct);
    JFrame win = new JFrame();
    UICore core = UICore.getInstance();
    core.setApplicationWindow(win);
    View2dFactory factory = new View2dFactory();
    core.registerSeriesViewerFactory(factory);
    try {
      DicomExplorer explorer = new DicomExplorer(LocalPersistence.getDicomModel());
      String out = new DicomCommands().get("-l", ct.getAbsolutePath());
      assertTrue(out.contains("imported=1"));
      assertEquals(1, explorer.patientPane().getSelectionManager().patientKeys().size());
      Component center =
          ((BorderLayout) win.getContentPane().getLayout()).getLayoutComponent(BorderLayout.CENTER);
      assertInstanceOf(View2dContainer.class, center);
      assertNotNull(((View2dContainer) center).getView2d().getDataset());
    } finally {
      for (ViewerPlugin<?> plugin : List.copyOf(core.getOpenViewerPlugins())) {
        core.closeViewerPlugin(plugin);
      }
      core.unregisterSeriesViewerFactory(factory);
      core.setApplicationWindow(null);
      win.dispose();
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
    explorer.seriesFilter().setMode(SeriesFilter.MODALITY);
    explorer.seriesFilter().setQuery("mr");
    explorer.refresh();
    assertEquals(0, explorer.seriesSelection().getItems().size());
    explorer.seriesFilter().setQuery("ct");
    explorer.refresh();
    assertEquals(1, explorer.seriesSelection().getItems().size());
  }

  @Test
  void explorerFilterChromeAppliesPrefModesWithoutRenamingSeriesList() {
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
    assertEquals("explorer-series", explorer.seriesList().getName());
    assertEquals("explorer-filter-mode", explorer.filterModeCombo().getName());
    assertEquals("explorer-filter-query", explorer.filterQueryField().getName());
    explorer.filterModeCombo().setSelectedItem(SeriesFilter.TEXT);
    assertEquals(SeriesFilter.TEXT, explorer.seriesFilter().getMode());
    explorer.filterModeCombo().setSelectedItem(SeriesFilter.DATE);
    explorer.filterQueryField().setText("20251231");
    assertEquals(0, explorer.seriesSelection().getItems().size());
    explorer.filterQueryField().setText("20260101");
    assertEquals(1, explorer.seriesSelection().getItems().size());
  }

  @Test
  void explorerDocksFieldsLimitedSearchOnImportedSeries(@TempDir Path dir) throws Exception {
    File ct = dir.resolve("ct.dcm").toFile();
    LoadLocalDicomTest.writeCt(ct);
    DicomModel model = new DicomModel();
    DicomExplorer explorer = new DicomExplorer(model);
    new ImportDicomPage("DICOM", 0, model, new SkipUnsupportedSopNotifier())
        .importFiles(List.of(ct), null);
    assertTrue(explorer.seriesSelection().selectedIndices().isEmpty());
    JSplitPane split =
        (JSplitPane) ((BorderLayout) explorer.getLayout()).getLayoutComponent(BorderLayout.CENTER);
    assertEquals("explorer-fields-split", split.getName());
    assertSame(
        DicomTaskManager.getInstance().getLoadingPanel(),
        ((BorderLayout) explorer.getLayout()).getLayoutComponent(BorderLayout.SOUTH));
    DicomFieldsView view = explorer.fieldsView();
    assertEquals("DICOM Fields", view.getName());
    assertEquals("limited", view.limitedBox().getName());
    assertEquals("tagSearch", view.searchField().getName());
    assertEquals("tagTable", view.tablePanel().table().getName());
    assertEquals("tagDocument", view.documentPanel().document().getName());
    assertEquals("tagViews", ((JTabbedPane) view.getComponent(1)).getName());
    assertTrue(view.isLimited());
    List<TagRow> limited = view.allItems();
    assertTrue(keywordValue(limited, "PatientID").contains("SYN-CT-0001"));
    assertTrue(keywordValue(limited, "PatientName").contains("SYNTHETIC^CT"));
    assertTrue(keywordValue(limited, "Modality").contains("CT"));
    assertFalse(containsKeyword(limited, "PixelData"));
    assertTrue(view.documentPanel().plainText().contains("SYN-CT-0001"));
    assertFalse(view.documentPanel().plainText().contains("PixelData"));
    view.setLimited(false);
    List<TagRow> all = view.allItems();
    assertTrue(containsKeyword(all, "PixelData"));
    assertEquals("[OW]", keywordValue(all, "PixelData"));
    view.setQuery("Patient");
    List<TagRow> filtered = view.visibleItems();
    assertTrue(containsKeyword(filtered, "PatientID"));
    assertTrue(containsKeyword(filtered, "PatientName"));
    assertFalse(containsKeyword(filtered, "Modality"));
    assertTrue(view.documentPanel().plainText().contains("PatientID"));
    assertFalse(view.documentPanel().plainText().contains("Modality"));
  }

  @Test
  void explorerImportDialogNamesAndRunLoadsPart10(@TempDir Path dir) throws Exception {
    File ct = dir.resolve("ct.dcm").toFile();
    LoadLocalDicomTest.writeCt(ct);
    DicomModel model = new DicomModel();
    DicomExplorer explorer = new DicomExplorer(model);
    ImportDicomDialog dialog = explorer.createImportView(null, false);
    assertEquals("import-dicom-dialog", dialog.getName());
    assertEquals("import-tabs", dialog.tabs().getName());
    assertEquals(LocalImportFactory.PAGE_LOCAL, dialog.tabs().getTitleAt(0));
    assertEquals(LocalImportFactory.PAGE_ZIP, dialog.tabs().getTitleAt(1));
    assertEquals(LocalImportFactory.PAGE_DIR, dialog.tabs().getTitleAt(2));
    ImportDicomPage page = dialog.page(LocalImportFactory.PAGE_LOCAL);
    assertEquals("import-path", page.pathField().getName());
    page.setPath(ct.getAbsolutePath());
    page.runImport();
    assertTrue(page.statusText().startsWith("Imported "));
    assertEquals(1, model.getInstances().size());
  }

  @Test
  void explorerExportDialogBindsImportedSeries(@TempDir Path dir) throws Exception {
    File ct = dir.resolve("ct.dcm").toFile();
    LoadLocalDicomTest.writeCt(ct);
    DicomModel model = new DicomModel();
    DicomExplorer explorer = new DicomExplorer(model);
    new ImportDicomPage("DICOM", 0, model, new SkipUnsupportedSopNotifier())
        .importFiles(List.of(ct), null);
    org.weasis.dicom.explorer.exp.ExportDicomView view = explorer.createExportView(null);
    assertEquals("export-dicom-dialog", view.getName());
    javax.swing.tree.DefaultMutableTreeNode root =
        (javax.swing.tree.DefaultMutableTreeNode) view.getExportTree().getModel().getRoot();
    assertTrue(root.getChildAt(0).toString().contains("SYNTHETIC^CT"));
    assertTrue(root.getChildAt(0).toString().contains("SYN-CT-0001"));
    assertNotNull(view.page(org.weasis.dicom.explorer.exp.DicomExportFactory.PAGE_LOCAL));
    assertNotNull(view.page(org.weasis.dicom.explorer.exp.DicomExportFactory.PAGE_ZIP));
    assertNotNull(view.page(org.weasis.dicom.explorer.exp.DicomExportFactory.PAGE_DIR));
  }

  static boolean containsKeyword(List<TagRow> rows, String keyword) {
    return rows.stream().anyMatch(r -> keyword.equals(r.keyword()));
  }

  static String keywordValue(List<TagRow> rows, String keyword) {
    return rows.stream()
        .filter(r -> keyword.equals(r.keyword()))
        .map(TagRow::value)
        .findFirst()
        .orElse("");
  }
}
