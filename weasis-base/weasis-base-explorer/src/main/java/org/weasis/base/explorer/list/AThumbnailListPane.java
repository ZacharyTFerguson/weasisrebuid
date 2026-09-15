/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.base.explorer.list;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
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
import org.weasis.base.explorer.JIThumbnailCache;
import org.weasis.base.explorer.list.impl.DefaultThumbnailList;

/** Scroll host for a thumbnail list plus a {@link JIThumbnailCache} keyed by URI. */
public class AThumbnailListPane extends JPanel {

  private final AbstractThumbnailList thumbnailList;
  private final JIThumbnailCache cache;

  public AThumbnailListPane() {
    this(new DefaultThumbnailList());
  }

  public AThumbnailListPane(AbstractThumbnailList thumbnailList) {
    this(thumbnailList, cacheOf(thumbnailList));
  }

  public AThumbnailListPane(AbstractThumbnailList thumbnailList, JIThumbnailCache cache) {
    super(new BorderLayout());
    this.thumbnailList = thumbnailList == null ? new DefaultThumbnailList(cache) : thumbnailList;
    this.cache = cache == null ? cacheOf(this.thumbnailList) : cache;
    add(new JLabel("Thumbnails"), BorderLayout.NORTH);
  }

  public AbstractThumbnailList thumbnailList() {
    return thumbnailList;
  }

  public JIThumbnailCache cache() {
    return cache;
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

  public BufferedImage thumbnailAt(int index) {
    if (thumbnailList instanceof DefaultThumbnailList list) {
      return list.thumbnailAt(index);
    }
    var path = thumbnailList.get(index);
    if (path == null) {
      return null;
    }
    return cache.getOrLoad(path);
  }

  private static JIThumbnailCache cacheOf(AbstractThumbnailList list) {
    if (list instanceof DefaultThumbnailList defaultList) {
      return defaultList.getCache();
    }
    return new JIThumbnailCache();
  }
}
