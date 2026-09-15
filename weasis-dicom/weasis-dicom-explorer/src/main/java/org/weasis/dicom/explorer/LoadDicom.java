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
 * Background Part-10 / folder / ZIP loader into {@link DicomModel}. DICOMDIR sources go through
 * {@link LoadDicomDir}.
 */
public class LoadDicom extends ExplorerTask<Boolean, String> {

  private final DicomModel model;
  private final List<File> sources;
  private final String zipPassword;
  private final SkipUnsupportedSopNotifier skip;
  private final boolean recursive;
  private LoadLocalDicom.ImportResult result =
      new LoadLocalDicom.ImportResult(List.of(), List.of());

  public LoadDicom(DicomModel model, File file) {
    this(model, file == null ? List.of() : List.of(file), null, new SkipUnsupportedSopNotifier());
  }

  public LoadDicom(DicomModel model, File[] files, boolean recursive) {
    this(
        model,
        files == null ? List.of() : List.of(files),
        null,
        new SkipUnsupportedSopNotifier(),
        recursive);
  }

  public LoadDicom(
      DicomModel model, List<File> files, String zipPassword, SkipUnsupportedSopNotifier skip) {
    this(model, files, zipPassword, skip, true);
  }

  public LoadDicom(
      DicomModel model,
      List<File> files,
      String zipPassword,
      SkipUnsupportedSopNotifier skip,
      boolean recursive) {
    super("Loading DICOM", true);
    this.model = model == null ? LocalPersistence.getDicomModel() : model;
    this.sources = files == null ? List.of() : List.copyOf(files);
    this.zipPassword = zipPassword;
    this.skip = skip;
    this.recursive = recursive;
  }

  public DicomModel getDicomModel() {
    return model;
  }

  public boolean isRecursive() {
    return recursive;
  }

  public LoadLocalDicom.ImportResult result() {
    return result;
  }

  public LoadLocalDicom.ImportResult load() throws IOException {
    List<ImportedInstance> ok = new ArrayList<>();
    List<String> errors = new ArrayList<>();
    for (File file : sources) {
      if (isCancelled()) {
        break;
      }
      if (file == null) {
        continue;
      }
      LoadLocalDicom.ImportResult one = loadOne(file);
      ok.addAll(one.imported());
      errors.addAll(one.errors());
    }
    result = new LoadLocalDicom.ImportResult(List.copyOf(ok), List.copyOf(errors));
    return result;
  }

  LoadLocalDicom.ImportResult loadOne(File file) throws IOException {
    DragDropRules.Kind kind = DragDropRules.classify(file);
    if (kind == DragDropRules.Kind.DICOMDIR) {
      return new LoadDicomDir(model, file, skip).load();
    }
    if (file.isDirectory()) {
      File dicomdir = new File(file, "DICOMDIR");
      if (dicomdir.isFile()) {
        return new LoadDicomDir(model, dicomdir, skip).load();
      }
      if (!recursive) {
        return importImmediateChildren(file);
      }
    }
    return LoadLocalDicom.importPath(file, zipPassword, model, skip);
  }

  LoadLocalDicom.ImportResult importImmediateChildren(File folder) throws IOException {
    List<ImportedInstance> ok = new ArrayList<>();
    List<String> errors = new ArrayList<>();
    File[] children = folder.listFiles();
    if (children == null) {
      return new LoadLocalDicom.ImportResult(ok, errors);
    }
    for (File child : children) {
      if (child.isDirectory()) {
        continue;
      }
      LoadLocalDicom.ImportResult one = LoadLocalDicom.importPath(child, zipPassword, model, skip);
      ok.addAll(one.imported());
      errors.addAll(one.errors());
    }
    return new LoadLocalDicom.ImportResult(ok, errors);
  }

  @Override
  protected Boolean doInBackground() throws Exception {
    LoadLocalDicom.ImportResult loaded = load();
    return loaded.errors().isEmpty();
  }
}
