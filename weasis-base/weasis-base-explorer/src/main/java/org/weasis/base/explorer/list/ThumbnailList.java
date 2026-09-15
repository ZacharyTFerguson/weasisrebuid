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
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

/** Non-DICOM explorer thumbnail list: selection plus the backing {@link AThumbnailModel}. */
public interface ThumbnailList {

  AThumbnailModel model();

  void setItems(List<Path> items);

  List<Path> getItems();

  int size();

  Path get(int index);

  void click(int index, boolean ctrl, boolean shift);

  void selectAll();

  void enter();

  boolean isOpened();

  boolean keyPressed(KeyEvent event);

  Set<Integer> selectedIndices();

  List<Path> selectedItems();

  int getAnchor();

  int getLead();

  int getPriorityIndex();

  void clearSelection();
}
