/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.explorer.gui.control;

import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.weasis.acquire.explorer.AcquireManager;
import org.weasis.acquire.explorer.ImportTask;
import org.weasis.acquire.explorer.gui.central.AcquireTabPanel;
import org.weasis.acquire.explorer.gui.central.ImageGroupPane;
import org.weasis.acquire.explorer.gui.dialog.AcquireImportDialog;

/** Kicks {@link ImportTask} for the browse selection into the central album. */
public class ImportPanel extends JPanel {

  private BrowsePanel browse;
  private ImageGroupPane album;
  private AcquireManager manager = new AcquireManager();
  private final AcquireImportDialog dialog = new AcquireImportDialog();
  private final JButton importButton = new JButton("Import");

  public ImportPanel() {
    super(new FlowLayout(FlowLayout.LEFT));
    importButton.setName("import");
    importButton.addActionListener(e -> importSelected());
    add(importButton);
  }

  public ImportPanel(BrowsePanel browse, ImageGroupPane album, AcquireManager manager) {
    this();
    bind(browse, album, manager);
  }

  public void bind(BrowsePanel browse, ImageGroupPane album, AcquireManager manager) {
    this.browse = browse;
    this.album = album;
    this.manager = manager == null ? new AcquireManager() : manager;
  }

  public BrowsePanel browse() {
    return browse;
  }

  public ImageGroupPane album() {
    return album;
  }

  public AcquireManager manager() {
    return manager;
  }

  public AcquireImportDialog dialog() {
    return dialog;
  }

  public JButton importButton() {
    return importButton;
  }

  public AcquireTabPanel tabPanel() {
    return album == null ? null : album.tabPanel();
  }

  public ImportTask importSelected() {
    if (browse == null || album == null) {
      return null;
    }
    ImportTask task =
        new ImportTask(dialog, manager, album.tabPanel(), browse.thumbnails().selected());
    task.run();
    return task;
  }
}
