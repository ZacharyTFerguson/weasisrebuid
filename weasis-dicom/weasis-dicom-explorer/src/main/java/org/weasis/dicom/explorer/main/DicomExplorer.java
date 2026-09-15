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

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.weasis.core.api.service.UICore;
import org.weasis.core.ui.editor.image.ViewerPlugin;
import org.weasis.dicom.explorer.DicomModel;
import org.weasis.dicom.explorer.DicomSeriesHandler;
import org.weasis.dicom.explorer.LoadDicomObjects;
import org.weasis.dicom.explorer.LoadLocalDicom;
import org.weasis.dicom.explorer.PluginOpeningStrategy;

/**
 * Explorer open session: load Part-10 files into {@link DicomModel}, refresh the explorer tree, and
 * route series to viewer tabs.
 */
public class DicomExplorer {

  private final org.weasis.dicom.explorer.DicomExplorer view;
  private final PluginOpeningStrategy opening;

  public DicomExplorer() {
    this(new DicomModel(), new UICore());
  }

  public DicomExplorer(DicomModel model) {
    this(model, new UICore());
  }

  public DicomExplorer(DicomModel model, UICore core) {
    DicomModel dicom = model == null ? new DicomModel() : model;
    this.view = new org.weasis.dicom.explorer.DicomExplorer(dicom);
    this.opening = new PluginOpeningStrategy(core, new DicomSeriesHandler(dicom));
  }

  public org.weasis.dicom.explorer.DicomExplorer view() {
    return view;
  }

  public DicomModel getDicomModel() {
    return view.getDicomModel();
  }

  public PluginOpeningStrategy openingStrategy() {
    return opening;
  }

  public LoadLocalDicom.ImportResult loadFiles(List<File> files) throws IOException {
    LoadDicomObjects loader = new LoadDicomObjects(view.getDicomModel(), files);
    LoadLocalDicom.ImportResult result = loader.load();
    view.refresh();
    return result;
  }

  public List<ViewerPlugin<?>> openLoaded() {
    return opening.openModel(view.getDicomModel());
  }

  public List<ViewerPlugin<?>> loadAndOpen(List<File> files) throws IOException {
    loadFiles(files);
    return openLoaded();
  }
}
