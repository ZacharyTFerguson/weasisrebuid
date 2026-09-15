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

import java.util.Hashtable;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.ui.editor.SeriesViewer;
import org.weasis.core.ui.editor.SeriesViewerFactory;
import org.weasis.core.ui.editor.image.ImageViewerPlugin;
import org.weasis.dicom.codec.DicomMime;

/**
 * DICOM viewer tab. One patient occupies one multi-view tab; {@link PluginOpeningStrategy} reuses
 * this plugin when the next series belongs to the same patient.
 */
public class DicomViewerPlugin extends ImageViewerPlugin<MediaElement> {

  public static final String NAME = "DICOM";

  private String patientKey;

  public DicomViewerPlugin() {
    this(NAME, "");
  }

  public DicomViewerPlugin(String patientKey) {
    this(NAME, patientKey);
  }

  public DicomViewerPlugin(String pluginName, String patientKey) {
    super(pluginName == null || pluginName.isBlank() ? NAME : pluginName);
    this.patientKey = patientKey == null ? "" : patientKey;
  }

  public String getPatientKey() {
    return patientKey;
  }

  public void setPatientKey(String patientKey) {
    this.patientKey = patientKey == null ? "" : patientKey;
  }

  public boolean acceptsPatient(String key) {
    return patientKey != null && !patientKey.isBlank() && patientKey.equals(key);
  }

  public boolean accepts(ImportedInstance inst) {
    return inst != null && acceptsPatient(inst.patientKey());
  }

  /** Fallback {@code image/dicom} factory when View2d is not registered. */
  public static final class Factory implements SeriesViewerFactory {

    public static final String NAME = DicomViewerPlugin.NAME;

    @Override
    public SeriesViewer<?> createSeriesViewer(Hashtable<String, Object> properties) {
      Object key = properties == null ? null : properties.get("patientKey");
      return new DicomViewerPlugin(NAME, key == null ? "" : key.toString());
    }

    @Override
    public boolean canReadMimeType(String mimeType) {
      return DicomMime.IMAGE_DICOM.equals(mimeType)
          || DicomMime.SERIES_DICOM.equals(mimeType)
          || DicomMime.APPLICATION_DICOM.equals(mimeType);
    }

    @Override
    public boolean isViewerCreatedByThisFactory(SeriesViewer<?> viewer) {
      return viewer instanceof DicomViewerPlugin;
    }

    @Override
    public int getLevel() {
      return 20;
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
      return NAME;
    }

    @Override
    public String getDescription() {
      return "DICOM series tab";
    }

    @Override
    public String getIconPath() {
      return null;
    }

    @Override
    public String getSeriesViewerName() {
      return NAME;
    }

    @Override
    public String getClassName() {
      return DicomViewerPlugin.class.getName();
    }
  }
}
