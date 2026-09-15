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

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Non-DICOM explorer thumbnail selection. SHORTCUTS.md: Ctrl+click toggle, Shift+click range,
 * Ctrl+A all, Enter open. Last click is the raised-priority item.
 */
public class AbstractThumbnailList implements ThumbnailList {

  private final AThumbnailModel model;
  private final LinkedHashSet<Integer> selected = new LinkedHashSet<>();
  private int anchor = -1;
  private int lead = -1;
  private int priorityIndex = -1;
  private boolean opened;

  public AbstractThumbnailList() {
    this(new AThumbnailModel());
  }

  public AbstractThumbnailList(AThumbnailModel model) {
    this.model = model == null ? new AThumbnailModel() : model;
  }

  @Override
  public AThumbnailModel model() {
    return model;
  }

  @Override
  public void setItems(List<Path> items) {
    model.setItems(items);
    clearSelection();
    opened = false;
  }

  @Override
  public List<Path> getItems() {
    return model.items();
  }

  @Override
  public int size() {
    return model.getSize();
  }

  @Override
  public Path get(int index) {
    return model.getElementAt(index);
  }

  @Override
  public void click(int index, boolean ctrl, boolean shift) {
    if (index < 0 || index >= model.getSize()) {
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

  @Override
  public void selectAll() {
    selected.clear();
    for (int i = 0; i < model.getSize(); i++) {
      selected.add(i);
    }
  }

  @Override
  public void enter() {
    opened = !selected.isEmpty();
  }

  @Override
  public boolean isOpened() {
    return opened;
  }

  @Override
  public boolean keyPressed(KeyEvent event) {
    if (event == null) {
      return false;
    }
    boolean ctrl = (event.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0;
    if (ctrl && event.getKeyCode() == KeyEvent.VK_A) {
      selectAll();
      return true;
    }
    if (event.getKeyCode() == KeyEvent.VK_ENTER) {
      enter();
      return true;
    }
    return false;
  }

  @Override
  public Set<Integer> selectedIndices() {
    return Collections.unmodifiableSet(selected);
  }

  @Override
  public List<Path> selectedItems() {
    List<Path> out = new ArrayList<>();
    for (Integer index : selected) {
      if (index != null && index >= 0 && index < model.getSize()) {
        out.add(model.getElementAt(index));
      }
    }
    return List.copyOf(out);
  }

  @Override
  public int getAnchor() {
    return anchor;
  }

  @Override
  public int getLead() {
    return lead;
  }

  /** Last clicked thumbnail — explorer raises this item's open / download priority. */
  @Override
  public int getPriorityIndex() {
    return priorityIndex;
  }

  @Override
  public void clearSelection() {
    selected.clear();
    anchor = -1;
    lead = -1;
    priorityIndex = -1;
  }
}
