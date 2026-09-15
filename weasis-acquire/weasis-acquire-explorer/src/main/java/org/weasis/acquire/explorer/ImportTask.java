/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.explorer;

import java.nio.file.Path;
import java.util.List;
import org.weasis.acquire.explorer.gui.central.AcquireTabPanel;
import org.weasis.acquire.explorer.gui.dialog.AcquireImportDialog;

/**
 * Imports browse stills into the central album using {@link AcquireImportDialog} grouping already
 * Have.
 */
public class ImportTask implements Runnable {

  private final AcquireImportDialog dialog;
  private final AcquireManager manager;
  private final AcquireTabPanel album;
  private final List<Path> files;

  public ImportTask(
      AcquireImportDialog dialog, AcquireManager manager, AcquireTabPanel album, List<Path> files) {
    this.dialog = dialog == null ? new AcquireImportDialog() : dialog;
    this.manager = manager == null ? new AcquireManager() : manager;
    this.album = album;
    this.files = files == null ? List.of() : List.copyOf(files);
  }

  public AcquireImportDialog dialog() {
    return dialog;
  }

  public AcquireManager manager() {
    return manager;
  }

  public AcquireTabPanel album() {
    return album;
  }

  public List<Path> files() {
    return files;
  }

  @Override
  public void run() {
    if (album == null) {
      return;
    }
    dialog.importInto(manager, album.imagePanel().model(), files);
    album.refreshSeries();
  }
}
