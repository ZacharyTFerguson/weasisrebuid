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

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.weasis.base.explorer.JIThumbnailCache;

/** Scroll host for a {@link ThumbnailList} plus a {@link JIThumbnailCache} keyed by URI. */
public interface IThumbnailListPane {

  ThumbnailList thumbnailList();

  JIThumbnailCache cache();

  void setItems(List<Path> items);

  void loadDirectory(File directory) throws IOException;

  void loadDirectory(Path directory) throws IOException;

  void click(int index, boolean ctrl, boolean shift);

  boolean keyPressed(KeyEvent event);

  List<Path> selected();

  int getPriorityIndex();

  boolean isOpened();

  BufferedImage thumbnailAt(int index);
}
