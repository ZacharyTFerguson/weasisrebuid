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
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.util.Hashtable;
import java.util.List;
import org.dcm4che3.data.UID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.api.service.UICore;
import org.weasis.core.ui.editor.MimeSystemAppViewer;
import org.weasis.core.ui.editor.SeriesViewer;
import org.weasis.core.ui.editor.SeriesViewerFactory;
import org.weasis.core.ui.editor.image.ViewerPlugin;
import org.weasis.dicom.codec.DicomMime;
import org.weasis.dicom.explorer.exp.ExplorerTask;

class ExplorerOpenHaveTest {

  @Test
  void loadDicomObjectsReadsPart10ListNotFolder(@TempDir Path dir) throws Exception {
    File a = dir.resolve("a.dcm").toFile();
    File b = dir.resolve("b.dcm").toFile();
    LoadLocalDicomTest.writeCt(a);
    LoadLocalDicomTest.writeCt(b);
    DicomModel model = new DicomModel();
    LoadDicomObjects loader = new LoadDicomObjects(model, List.of(a, b));
    assertInstanceOf(ExplorerTask.class, loader);
    var result = loader.load();
    assertEquals(2, result.imported().size());
    assertEquals(2, model.getInstances().size());
    assertSame(model, loader.getDicomModel());

    DicomModel skipped = new DicomModel();
    var folder = new LoadDicomObjects(skipped, dir.toFile()).load();
    assertTrue(folder.imported().isEmpty());
    assertFalse(folder.errors().isEmpty());
    assertEquals(0, skipped.getInstances().size());
  }

  @Test
  void pluginOpeningStrategyRoutesSopToFactoriesAndReusesPatientTab() {
    UICore core = new UICore();
    DicomViewerPlugin.Factory images = new DicomViewerPlugin.Factory();
    MimeFactory sr = new MimeFactory(DicomMime.SR_DICOM, "SR", 70);
    MimeSystemAppFactory system = new MimeSystemAppFactory();
    core.registerSeriesViewerFactory(images);
    core.registerSeriesViewerFactory(sr);
    core.registerSeriesViewerFactory(system);
    PluginOpeningStrategy opening = new PluginOpeningStrategy(core);

    assertEquals(DicomMime.IMAGE_DICOM, opening.mimeForSop(UID.CTImageStorage));
    assertEquals(DicomMime.SR_DICOM, opening.mimeForSop(UID.BasicTextSRStorage));
    assertEquals(DicomMime.ENCAP_DICOM, opening.mimeForSop(UID.EncapsulatedPDFStorage));
    assertEquals(DicomMime.SEG_DICOM, opening.mimeForSop(UID.SegmentationStorage));
    assertEquals(DicomMime.WAVE_DICOM, opening.mimeForSop(UID.TwelveLeadECGWaveformStorage));
    assertEquals(DicomMime.AU_DICOM, opening.mimeForSop(UID.BasicVoiceAudioWaveformStorage));
    assertEquals(PluginOpeningStrategy.Kind.OVERLAY, opening.kindForSop(UID.SegmentationStorage));
    assertEquals(PluginOpeningStrategy.Kind.OVERLAY, opening.kindForSop(UID.RTStructureSetStorage));
    assertEquals(
        PluginOpeningStrategy.Kind.OVERLAY,
        opening.kindForSop(UID.GrayscaleSoftcopyPresentationStateStorage));
    assertEquals(PluginOpeningStrategy.Kind.SPECIAL, opening.kindForSop(UID.BasicTextSRStorage));
    assertEquals(PluginOpeningStrategy.Kind.SYSTEM, opening.kindForSop(UID.EncapsulatedPDFStorage));
    assertSame(images, opening.factoryForSop(UID.CTImageStorage));
    assertSame(sr, opening.factoryForSop(UID.BasicTextSRStorage));
    assertSame(system, opening.factoryForSop(UID.EncapsulatedPDFStorage));

    ImportedInstance ct1 = instance("A", "1", "2.25.s1", UID.CTImageStorage, DicomMime.IMAGE_DICOM);
    ImportedInstance ct2 = instance("A", "1", "2.25.s2", UID.CTImageStorage, DicomMime.IMAGE_DICOM);
    ImportedInstance other =
        instance("B", "2", "2.25.s9", UID.CTImageStorage, DicomMime.IMAGE_DICOM);
    ViewerPlugin<?> first = opening.open(ct1);
    assertInstanceOf(DicomViewerPlugin.class, first);
    ViewerPlugin<?> overlay =
        opening.open(instance("A", "1", "2.25.seg", UID.SegmentationStorage, DicomMime.SEG_DICOM));
    assertEquals(1, opening.overlaysRouted());
    assertSame(first, overlay);
    assertEquals(1, core.getOpenViewerPlugins().size());

    ViewerPlugin<?> same = opening.open(ct2);
    assertSame(first, same);
    assertEquals(2, first.getOpenSeries().size());
    ViewerPlugin<?> second = opening.open(other);
    assertNotNull(second);
    assertNotSame(first, second);
    assertEquals(2, core.getOpenViewerPlugins().size());

    ViewerPlugin<?> report =
        opening.open(instance("A", "1", "2.25.sr", UID.BasicTextSRStorage, DicomMime.SR_DICOM));
    assertTrue(sr.isViewerCreatedByThisFactory(report));
    ViewerPlugin<?> pdf =
        opening.open(
            instance("A", "1", "2.25.pdf", UID.EncapsulatedPDFStorage, DicomMime.ENCAP_DICOM));
    assertInstanceOf(MimeSystemAppViewer.class, pdf);

    core.unregisterSeriesViewerFactory(images);
    core.unregisterSeriesViewerFactory(sr);
    core.unregisterSeriesViewerFactory(system);
  }

  @Test
  void mainExplorerLoadsPart10AndOpensDicomTab(@TempDir Path dir) throws Exception {
    File ct = dir.resolve("ct.dcm").toFile();
    LoadLocalDicomTest.writeCt(ct);
    UICore core = new UICore();
    DicomViewerPlugin.Factory factory = new DicomViewerPlugin.Factory();
    core.registerSeriesViewerFactory(factory);
    org.weasis.dicom.explorer.main.DicomExplorer explorer =
        new org.weasis.dicom.explorer.main.DicomExplorer(new DicomModel(), core);
    List<ViewerPlugin<?>> opened = explorer.loadAndOpen(List.of(ct));
    assertEquals(1, explorer.getDicomModel().getInstances().size());
    assertEquals(1, opened.size());
    assertInstanceOf(DicomViewerPlugin.class, opened.getFirst());
    assertTrue(factory.isViewerCreatedByThisFactory(opened.getFirst()));
    assertEquals(1, explorer.view().seriesSelection().getItems().size());
    core.unregisterSeriesViewerFactory(factory);
  }

  @Test
  void mainExplorerDefaultsToSharedUiCore() {
    org.weasis.dicom.explorer.main.DicomExplorer explorer =
        new org.weasis.dicom.explorer.main.DicomExplorer(new DicomModel());
    assertSame(UICore.getInstance(), explorer.openingStrategy().getUICore());
  }

  @Test
  void dicomViewerPluginIsAViewerTab() {
    DicomViewerPlugin plugin = new DicomViewerPlugin("p\tid");
    assertInstanceOf(ViewerPlugin.class, plugin);
    assertEquals("p\tid", plugin.getPatientKey());
    assertEquals(DicomViewerPlugin.NAME, plugin.getPluginName());
    DicomViewerPlugin.Factory factory = new DicomViewerPlugin.Factory();
    assertTrue(factory.canReadMimeType(DicomMime.IMAGE_DICOM));
    assertFalse(factory.canReadMimeType(DicomMime.SR_DICOM));
    assertTrue(factory.isViewerCreatedByThisFactory(plugin));
  }

  static ImportedInstance instance(
      String name, String id, String series, String sopClass, String mime) {
    return new ImportedInstance(
        "SYNTHETIC^" + name,
        "SYN-" + id,
        "2.25.study." + id,
        series,
        series + ".1",
        sopClass,
        "CT",
        series,
        "20260101",
        1,
        1,
        null,
        mime);
  }

  static final class MimeFactory implements SeriesViewerFactory {
    private final String mime;
    private final String name;
    private final int level;

    MimeFactory(String mime, String name, int level) {
      this.mime = mime;
      this.name = name;
      this.level = level;
    }

    @Override
    public SeriesViewer<?> createSeriesViewer(Hashtable<String, Object> properties) {
      return new ViewerPlugin<MediaElement>(name) {};
    }

    @Override
    public boolean canReadMimeType(String mimeType) {
      return mime.equals(mimeType);
    }

    @Override
    public boolean isViewerCreatedByThisFactory(SeriesViewer<?> viewer) {
      return viewer instanceof ViewerPlugin<?> plugin && name.equals(plugin.getPluginName());
    }

    @Override
    public int getLevel() {
      return level;
    }

    @Override
    public boolean canAddSeries() {
      return true;
    }

    @Override
    public boolean canExternalizeSeries() {
      return true;
    }

    @Override
    public String getUIName() {
      return name;
    }

    @Override
    public String getDescription() {
      return name;
    }

    @Override
    public String getIconPath() {
      return null;
    }

    @Override
    public String getSeriesViewerName() {
      return name;
    }

    @Override
    public String getClassName() {
      return name;
    }
  }
}
