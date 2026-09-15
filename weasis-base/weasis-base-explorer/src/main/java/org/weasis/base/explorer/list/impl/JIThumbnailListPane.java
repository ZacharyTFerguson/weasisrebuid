/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.base.explorer.list.impl;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.weasis.base.explorer.list.AbstractThumbnailList;

/** Scroll host for the non-DICOM explorer thumbnail list. */
public class JIThumbnailListPane extends JPanel {

  private final AbstractThumbnailList thumbnailList;

  public JIThumbnailListPane() {
    this(new AbstractThumbnailList());
  }

  public JIThumbnailListPane(AbstractThumbnailList thumbnailList) {
    super(new BorderLayout());
    this.thumbnailList = thumbnailList == null ? new AbstractThumbnailList() : thumbnailList;
    add(new JLabel("Thumbnails"), BorderLayout.NORTH);
  }

  public AbstractThumbnailList thumbnailList() {
    return thumbnailList;
  }

  public void setItems(List<Path> items) {
    thumbnailList.setItems(items);
  }

  public void loadDirectory(File directory) throws IOException {
    loadDirectory(directory == null ? null : directory.toPath());
  }

  public void loadDirectory(Path directory) throws IOException {
    if (directory == null || !Files.isDirectory(directory)) {
      thumbnailList.setItems(List.of());
      return;
    }
    List<Path> files = new ArrayList<>();
    try (Stream<Path> stream = Files.list(directory)) {
      stream
          .filter(Files::isRegularFile)
          .filter(path -> !path.getFileName().toString().startsWith("."))
          .sorted(Comparator.comparing(path -> path.getFileName().toString()))
          .forEach(files::add);
    }
    thumbnailList.setItems(files);
  }

  public void click(int index, boolean ctrl, boolean shift) {
    thumbnailList.click(index, ctrl, shift);
  }

  public boolean keyPressed(KeyEvent event) {
    return thumbnailList.keyPressed(event);
  }

  public List<Path> selected() {
    return thumbnailList.selectedItems();
  }

  public int getPriorityIndex() {
    return thumbnailList.getPriorityIndex();
  }

  public boolean isOpened() {
    return thumbnailList.isOpened();
  }
}
