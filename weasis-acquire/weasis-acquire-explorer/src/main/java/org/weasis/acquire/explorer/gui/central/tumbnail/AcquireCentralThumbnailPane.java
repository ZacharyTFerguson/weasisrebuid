/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.explorer.gui.central.tumbnail;

import java.awt.BorderLayout;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.weasis.acquire.explorer.gui.central.tumbnail.AcquireCentralThumbnailModel.Item;

/** Central album pane: series-filtered imported thumbnails. */
public class AcquireCentralThumbnailPane extends JPanel {

  private final AcquireCentralThumbnailList list;

  public AcquireCentralThumbnailPane() {
    this(new AcquireCentralThumbnailList());
  }

  public AcquireCentralThumbnailPane(AcquireCentralThumbnailList list) {
    super(new BorderLayout());
    this.list = list == null ? new AcquireCentralThumbnailList() : list;
    add(new JLabel("Album"), BorderLayout.NORTH);
  }

  public AcquireCentralThumbnailList thumbnailList() {
    return list;
  }

  public AcquireCentralThumbnailModel model() {
    return list.model();
  }

  public void showSeries(String series) {
    list.showSeries(series);
  }

  public void click(int index, boolean ctrl, boolean shift) {
    list.click(index, ctrl, shift);
  }

  public List<Item> displayed() {
    return list.displayed();
  }

  public List<Item> selected() {
    return list.selectedItems();
  }
}
