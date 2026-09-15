/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.explorer.gui.list;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.nio.file.Path;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** Browse pane hosting the import thumbnail list. */
public class AcquireThumbnailListPane extends JPanel {

  private final AcquireThumbnailList list;

  public AcquireThumbnailListPane() {
    this(new AcquireThumbnailList());
  }

  public AcquireThumbnailListPane(AcquireThumbnailList list) {
    super(new BorderLayout());
    this.list = list == null ? new AcquireThumbnailList() : list;
    add(new JLabel("Import"), BorderLayout.NORTH);
  }

  public AcquireThumbnailList thumbnailList() {
    return list;
  }

  public AcquireThumbnailModel model() {
    return list.model();
  }

  public void setItems(List<Path> items) {
    list.model().setItems(items);
    list.clearSelection();
  }

  public void click(int index, boolean ctrl, boolean shift) {
    list.click(index, ctrl, shift);
  }

  public boolean keyPressed(KeyEvent event) {
    return list.keyPressed(event);
  }

  public List<Path> selected() {
    return list.selectedItems();
  }
}
