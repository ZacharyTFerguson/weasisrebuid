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
import java.util.ArrayList;
import java.util.List;
import org.weasis.dicom.explorer.exp.ExplorerTask;

/**
 * Loads an explicit list of Part-10 files into {@link DicomModel}. Folders, ZIP, and DICOMDIR are
 * not walked — use {@link LoadDicom} / {@link LoadDicomDir} for those sources.
 */
public class LoadDicomObjects extends ExplorerTask<Boolean, String> {

  private final DicomModel model;
  private final List<File> files;
  private final SkipUnsupportedSopNotifier skip;
  private LoadLocalDicom.ImportResult result =
      new LoadLocalDicom.ImportResult(List.of(), List.of());

  public LoadDicomObjects(DicomModel model, File... files) {
    this(model, copyFiles(files), new SkipUnsupportedSopNotifier());
  }

  public LoadDicomObjects(DicomModel model, List<File> files) {
    this(model, files, new SkipUnsupportedSopNotifier());
  }

  public LoadDicomObjects(DicomModel model, List<File> files, SkipUnsupportedSopNotifier skip) {
    super("Loading DICOM objects", true);
    this.model = model == null ? new DicomModel() : model;
    this.files = copyFiles(files == null ? new File[0] : files.toArray(File[]::new));
    this.skip = skip == null ? new SkipUnsupportedSopNotifier() : skip;
  }

  private static List<File> copyFiles(File[] files) {
    if (files == null || files.length == 0) {
      return List.of();
    }
    List<File> copy = new ArrayList<>();
    for (File file : files) {
      if (file != null) {
        copy.add(file);
      }
    }
    return List.copyOf(copy);
  }

  public DicomModel getDicomModel() {
    return model;
  }

  public List<File> getFiles() {
    return files;
  }

  public LoadLocalDicom.ImportResult result() {
    return result;
  }

  public LoadLocalDicom.ImportResult load() throws IOException {
    List<ImportedInstance> ok = new ArrayList<>();
    List<String> errors = new ArrayList<>();
    for (File file : files) {
      if (isCancelled()) {
        break;
      }
      if (file == null) {
        continue;
      }
      DragDropRules.Kind kind = DragDropRules.classify(file);
      if (kind != DragDropRules.Kind.DICOM_FILE) {
        errors.add("not Part-10: " + file.getName());
        continue;
      }
      LoadLocalDicom.ImportResult one = LoadLocalDicom.importFile(file, model, skip);
      ok.addAll(one.imported());
      errors.addAll(one.errors());
    }
    result = new LoadLocalDicom.ImportResult(List.copyOf(ok), List.copyOf(errors));
    return result;
  }

  @Override
  protected Boolean doInBackground() throws Exception {
    LoadLocalDicom.ImportResult loaded = load();
    return loaded.errors().isEmpty();
  }
}
