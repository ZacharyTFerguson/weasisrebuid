/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Explorer thumbnail selection (SHORTCUTS.md): Ctrl+click toggle, Shift+click range, Ctrl+A all.
 * The last clicked index is the raised download-priority series.
 */
public class SeriesSelectionModel {

  private final List<String> items = new ArrayList<>();
  private final LinkedHashSet<Integer> selected = new LinkedHashSet<>();
  private int anchor = -1;
  private int lead = -1;
  private int priorityIndex = -1;
  private boolean opened;

  public void setItems(List<String> items) {
    this.items.clear();
    if (items != null) {
      this.items.addAll(items);
    }
    selected.clear();
    anchor = -1;
    lead = -1;
    priorityIndex = -1;
    opened = false;
  }

  public List<String> getItems() {
    return Collections.unmodifiableList(items);
  }

  public void click(int index, boolean ctrl, boolean shift) {
    if (index < 0 || index >= items.size()) {
      return;
    }
    if (shift && anchor >= 0) {
      selected.clear();
      int from = Math.min(anchor, index);
      int to = Math.max(anchor, index);
      for (int i = from; i <= to; i++) {
        selected.add(i);
      }
      lead = index;
    } else if (ctrl) {
      if (!selected.add(index)) {
        selected.remove(index);
      }
      anchor = index;
      lead = index;
    } else {
      selected.clear();
      selected.add(index);
      anchor = index;
      lead = index;
    }
    priorityIndex = index;
  }

  public void selectAll() {
    selected.clear();
    for (int i = 0; i < items.size(); i++) {
      selected.add(i);
    }
  }

  public void enter() {
    opened = !selected.isEmpty();
  }

  public boolean isOpened() {
    return opened;
  }

  public Set<Integer> selectedIndices() {
    return Collections.unmodifiableSet(selected);
  }

  public int getAnchor() {
    return anchor;
  }

  public int getLead() {
    return lead;
  }

  /** Last clicked thumbnail — explorer raises this series' download priority. */
  public int getPriorityIndex() {
    return priorityIndex;
  }
}
