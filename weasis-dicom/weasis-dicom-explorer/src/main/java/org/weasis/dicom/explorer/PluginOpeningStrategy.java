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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.weasis.core.api.gui.util.GuiExecutor;
import org.weasis.core.api.media.data.MediaSeries;
import org.weasis.core.api.service.UICore;
import org.weasis.core.ui.editor.SeriesViewerFactory;
import org.weasis.core.ui.editor.ViewerPluginBuilder;
import org.weasis.core.ui.editor.image.ViewerPlugin;
import org.weasis.dicom.codec.DicomMime;

/**
 * Routes SOP classes to MIME viewer factories. Image series share one tab per patient; SR / AU /
 * ECG / PDF / video open their own factory; SEG / RT / PR / KO attach as overlays and do not create
 * a tab.
 */
public class PluginOpeningStrategy {

  public enum Kind {
    IMAGE,
    SPECIAL,
    SYSTEM,
    OVERLAY,
    SKIP
  }

  private final UICore core;
  private final DicomSeriesHandler handler;
  private final Map<String, ViewerPlugin<?>> patientTabs = new LinkedHashMap<>();
  private int overlaysRouted;

  public PluginOpeningStrategy() {
    this(UICore.getInstance(), new DicomSeriesHandler());
  }

  public PluginOpeningStrategy(UICore core) {
    this(core, new DicomSeriesHandler());
  }

  public PluginOpeningStrategy(UICore core, DicomSeriesHandler handler) {
    this.core = core == null ? UICore.getInstance() : core;
    this.handler = handler == null ? new DicomSeriesHandler() : handler;
  }

  public UICore getUICore() {
    return core;
  }

  public DicomSeriesHandler getSeriesHandler() {
    return handler;
  }

  public int overlaysRouted() {
    return overlaysRouted;
  }

  public String mimeForSop(String sopClassUid) {
    return DicomMime.fromSopClass(sopClassUid);
  }

  public String mimeFor(ImportedInstance inst) {
    if (inst == null) {
      return DicomMime.UNREADABLE_DICOM;
    }
    if (inst.mime() != null && !inst.mime().isBlank()) {
      return inst.mime();
    }
    return mimeForSop(inst.sopClassUid());
  }

  public Kind kindForSop(String sopClassUid) {
    return kindFor(mimeForSop(sopClassUid));
  }

  public Kind kindFor(String mime) {
    if (mime == null || mime.isBlank()) {
      return Kind.SKIP;
    }
    String m = mime.toLowerCase(Locale.ROOT).trim();
    if (DicomMime.UNREADABLE_DICOM.equals(m)) {
      return Kind.SKIP;
    }
    if (DicomMime.SEG_DICOM.equals(m)
        || DicomMime.RT_DICOM.equals(m)
        || DicomMime.PR_DICOM.equals(m)
        || DicomMime.KO_DICOM.equals(m)) {
      return Kind.OVERLAY;
    }
    if (DicomMime.ENCAP_DICOM.equals(m)
        || DicomMime.VIDEO_DICOM.equals(m)
        || m.startsWith("video/")
        || m.startsWith("application/pdf")) {
      return Kind.SYSTEM;
    }
    if (DicomMime.SR_DICOM.equals(m)
        || DicomMime.AU_DICOM.equals(m)
        || DicomMime.WAVE_DICOM.equals(m)) {
      return Kind.SPECIAL;
    }
    return Kind.IMAGE;
  }

  public SeriesViewerFactory factoryFor(String mime) {
    if (mime == null) {
      return null;
    }
    return core.getViewerFactory(mime).orElse(null);
  }

  public SeriesViewerFactory factoryForSop(String sopClassUid) {
    return factoryFor(mimeForSop(sopClassUid));
  }

  public SeriesViewerFactory factoryFor(ImportedInstance inst) {
    return factoryFor(mimeFor(inst));
  }

  public ViewerPlugin<?> open(ImportedInstance inst) {
    if (inst == null) {
      return null;
    }
    DicomSeriesHandler.SeriesBucket bucket =
        new DicomSeriesHandler.SeriesBucket(inst.seriesUid(), mimeFor(inst), List.of(inst));
    return open(bucket);
  }

  public List<ViewerPlugin<?>> open(List<ImportedInstance> instances) {
    List<ViewerPlugin<?>> opened = new ArrayList<>();
    for (DicomSeriesHandler.SeriesBucket bucket : handler.group(instances)) {
      ViewerPlugin<?> plugin = open(bucket);
      if (plugin != null && !opened.contains(plugin)) {
        opened.add(plugin);
      }
    }
    return opened;
  }

  public List<ViewerPlugin<?>> openModel(DicomModel model) {
    List<DicomSeriesHandler.SeriesBucket> buckets = handler.group(model);
    buckets.sort(Comparator.comparingInt(b -> kindFor(b.mime()) == Kind.OVERLAY ? 1 : 0));
    List<ViewerPlugin<?>> opened = new ArrayList<>();
    for (DicomSeriesHandler.SeriesBucket bucket : buckets) {
      ViewerPlugin<?> plugin = open(bucket);
      if (plugin != null && !opened.contains(plugin)) {
        opened.add(plugin);
      }
    }
    return opened;
  }

  /** Opens series on the EDT when the live window exists (Gogo and File &gt; Import). */
  public List<ViewerPlugin<?>> openIfWindow(DicomModel model) {
    if (!canOpen(model)) {
      return List.of();
    }
    List<ViewerPlugin<?>> opened = new ArrayList<>();
    GuiExecutor.invokeAndWait(() -> opened.addAll(openModel(model)));
    return opened;
  }

  boolean canOpen(DicomModel model) {
    return model != null
        && !model.getInstances().isEmpty()
        && core.getApplicationWindow() != null;
  }

  public ViewerPlugin<?> open(DicomSeriesHandler.SeriesBucket bucket) {
    if (bucket == null || bucket.instances().isEmpty()) {
      return null;
    }
    String mime = bucket.mime();
    Kind kind = kindFor(mime);
    if (kind == Kind.SKIP) {
      return null;
    }
    if (kind == Kind.OVERLAY) {
      overlaysRouted++;
      return core.getSelectedViewerPlugin();
    }
    String patient = patientKey(bucket);
    if (kind == Kind.IMAGE) {
      ViewerPlugin<?> existing = patientTabs.get(patient);
      if (stillOpen(existing) && canAddSeries(existing, mime)) {
        addSeries(existing, handler.toMediaSeries(bucket));
        core.setSelectedViewerPlugin(existing);
        return existing;
      }
    }
    ViewerPlugin<?> plugin = createPlugin(bucket, mime, patient);
    if (plugin != null && kind == Kind.IMAGE && !patient.isBlank()) {
      patientTabs.put(patient, plugin);
    }
    return plugin;
  }

  private ViewerPlugin<?> createPlugin(
      DicomSeriesHandler.SeriesBucket bucket, String mime, String patient) {
    SeriesViewerFactory factory = factoryFor(mime);
    var series = handler.toMediaSeries(bucket);
    if (factory != null) {
      Hashtable<String, Object> props = new Hashtable<>();
      if (!patient.isBlank()) {
        props.put("patientKey", patient);
      }
      return ViewerPluginBuilder.openSequenceInPlugin(core, factory, series, props, true, true);
    }
    DicomViewerPlugin plugin = new DicomViewerPlugin(DicomViewerPlugin.NAME, patient);
    plugin.addSeries(series);
    core.openViewerPlugin(plugin);
    return plugin;
  }

  @SuppressWarnings("unchecked")
  private static void addSeries(ViewerPlugin<?> plugin, MediaSeries<?> series) {
    ((ViewerPlugin<org.weasis.core.api.media.data.MediaElement>) plugin)
        .addSeries((MediaSeries<org.weasis.core.api.media.data.MediaElement>) series);
  }

  private boolean stillOpen(ViewerPlugin<?> plugin) {
    return plugin != null && core.getOpenViewerPlugins().contains(plugin);
  }

  private boolean canAddSeries(ViewerPlugin<?> plugin, String mime) {
    if (plugin instanceof DicomViewerPlugin) {
      return true;
    }
    SeriesViewerFactory factory = factoryFor(mime);
    return factory != null
        && factory.canAddSeries()
        && factory.isViewerCreatedByThisFactory(plugin);
  }

  static String patientKey(DicomSeriesHandler.SeriesBucket bucket) {
    if (bucket == null || bucket.instances().isEmpty()) {
      return "";
    }
    return bucket.instances().getFirst().patientKey();
  }
}
