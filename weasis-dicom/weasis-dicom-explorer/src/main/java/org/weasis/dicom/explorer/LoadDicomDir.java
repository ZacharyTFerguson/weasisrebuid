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

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.weasis.dicom.explorer.exp.ExplorerTask;

/** Background DICOMDIR loader. Referenced IMAGE / ENCAP DOC files enter {@link DicomModel}. */
public class LoadDicomDir extends ExplorerTask<Boolean, String> {

  private final DicomModel model;
  private final File dicomdir;
  private final SkipUnsupportedSopNotifier skip;
  private LoadLocalDicom.ImportResult result =
      new LoadLocalDicom.ImportResult(List.of(), List.of());

  public LoadDicomDir(DicomModel model, File dicomdir) {
    this(model, dicomdir, new SkipUnsupportedSopNotifier());
  }

  public LoadDicomDir(DicomModel model, File dicomdir, SkipUnsupportedSopNotifier skip) {
    super("Loading DICOMDIR", true);
    this.model = model == null ? new DicomModel() : model;
    this.dicomdir = dicomdir;
    this.skip = skip;
  }

  public DicomModel getDicomModel() {
    return model;
  }

  public File getDicomdir() {
    return dicomdir;
  }

  public LoadLocalDicom.ImportResult result() {
    return result;
  }

  public LoadLocalDicom.ImportResult load() throws IOException {
    if (dicomdir == null || !dicomdir.isFile()) {
      result = new LoadLocalDicom.ImportResult(List.of(), List.of("missing DICOMDIR"));
      return result;
    }
    result = LoadLocalDicom.importDicomDir(dicomdir, model, skip);
    return result;
  }

  @Override
  protected Boolean doInBackground() throws Exception {
    LoadLocalDicom.ImportResult loaded = load();
    return !loaded.imported().isEmpty();
  }
}
