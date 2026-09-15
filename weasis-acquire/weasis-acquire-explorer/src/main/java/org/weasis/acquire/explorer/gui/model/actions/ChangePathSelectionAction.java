/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.explorer.gui.model.actions;

import java.awt.event.ActionEvent;
import java.nio.file.Path;
import javax.swing.AbstractAction;
import org.weasis.acquire.explorer.gui.control.BrowsePanel;
import org.weasis.acquire.explorer.media.FileSystemDrive;

/** Replaces the selected filesystem source path and reloads browse stills. */
public class ChangePathSelectionAction extends AbstractAction {

  private final BrowsePanel browsePanel;

  public ChangePathSelectionAction() {
    this(null);
  }

  public ChangePathSelectionAction(BrowsePanel browsePanel) {
    super("Change path");
    this.browsePanel = browsePanel;
  }

  public BrowsePanel browsePanel() {
    return browsePanel;
  }

  public void applyPath(Path path) {
    if (browsePanel == null || path == null) {
      return;
    }
    if (browsePanel.selected() instanceof FileSystemDrive drive) {
      drive.setPath(path);
      browsePanel.reloadStills();
      return;
    }
    browsePanel.addSource(new FileSystemDrive(path));
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    // Headless Have uses {@link #applyPath(Path)}; a file chooser is headed-only.
  }
}
