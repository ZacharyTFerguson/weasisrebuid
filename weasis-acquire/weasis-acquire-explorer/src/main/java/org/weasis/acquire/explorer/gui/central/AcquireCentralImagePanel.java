/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.explorer.gui.central;

import java.awt.BorderLayout;
import java.util.List;
import javax.swing.JPanel;
import org.weasis.acquire.explorer.gui.central.tumbnail.AcquireCentralThumbnailModel;
import org.weasis.acquire.explorer.gui.central.tumbnail.AcquireCentralThumbnailModel.Item;
import org.weasis.acquire.explorer.gui.central.tumbnail.AcquireCentralThumbnailPane;

/** Central album thumbnails for the selected series group. */
public class AcquireCentralImagePanel extends JPanel {

  private final AcquireCentralThumbnailPane pane;

  public AcquireCentralImagePanel() {
    this(new AcquireCentralThumbnailPane());
  }

  public AcquireCentralImagePanel(AcquireCentralThumbnailPane pane) {
    super(new BorderLayout());
    this.pane = pane == null ? new AcquireCentralThumbnailPane() : pane;
    add(this.pane, BorderLayout.CENTER);
  }

  public AcquireCentralThumbnailPane pane() {
    return pane;
  }

  public AcquireCentralThumbnailModel model() {
    return pane.model();
  }

  public void showSeries(String series) {
    pane.showSeries(series);
  }

  public List<Item> displayed() {
    return pane.displayed();
  }

  public List<Item> selected() {
    return pane.selected();
  }
}
