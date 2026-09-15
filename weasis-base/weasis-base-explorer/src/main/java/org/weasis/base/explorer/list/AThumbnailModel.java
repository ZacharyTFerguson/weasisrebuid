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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.AbstractListModel;

/** Swing list model of thumbnail paths for {@link ThumbnailList}. */
public class AThumbnailModel extends AbstractListModel<Path> {

  private final List<Path> items = new ArrayList<>();

  public void setItems(List<Path> items) {
    int previous = this.items.size();
    this.items.clear();
    if (previous > 0) {
      fireIntervalRemoved(this, 0, previous - 1);
    }
    if (items != null) {
      this.items.addAll(items);
    }
    if (!this.items.isEmpty()) {
      fireIntervalAdded(this, 0, this.items.size() - 1);
    }
  }

  public void add(Path path) {
    if (path == null) {
      return;
    }
    int index = items.size();
    items.add(path);
    fireIntervalAdded(this, index, index);
  }

  public void clear() {
    int previous = items.size();
    items.clear();
    if (previous > 0) {
      fireIntervalRemoved(this, 0, previous - 1);
    }
  }

  @Override
  public int getSize() {
    return items.size();
  }

  @Override
  public Path getElementAt(int index) {
    if (index < 0 || index >= items.size()) {
      return null;
    }
    return items.get(index);
  }

  public List<Path> items() {
    return Collections.unmodifiableList(items);
  }
}
