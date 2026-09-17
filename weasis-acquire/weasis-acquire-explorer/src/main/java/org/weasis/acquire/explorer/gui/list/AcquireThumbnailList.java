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

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Import-side thumbnail selection: click replaces, Ctrl+click toggles, Shift+click ranges, Ctrl+A
 * selects all.
 */
public class AcquireThumbnailList {

  private final AcquireThumbnailModel model;
  private final LinkedHashSet<Integer> selected = new LinkedHashSet<>();
  private int anchor = -1;

  public AcquireThumbnailList() {
    this(new AcquireThumbnailModel());
  }

  public AcquireThumbnailList(AcquireThumbnailModel model) {
    this.model = model == null ? new AcquireThumbnailModel() : model;
  }

  public AcquireThumbnailModel model() {
    return model;
  }

  public void click(int index, boolean ctrl, boolean shift) {
    if (index < 0 || index >= model.size()) {
      return;
    }
    if (shift && anchor >= 0) {
      selected.clear();
      int from = Math.min(anchor, index);
      int to = Math.max(anchor, index);
      for (int i = from; i <= to; i++) {
        selected.add(i);
      }
    } else if (ctrl) {
      if (!selected.add(index)) {
        selected.remove(index);
      }
      anchor = index;
    } else {
      selected.clear();
      selected.add(index);
      anchor = index;
    }
  }

  public void selectAll() {
    selected.clear();
    for (int i = 0; i < model.size(); i++) {
      selected.add(i);
    }
  }

  public boolean keyPressed(KeyEvent event) {
    if (event == null) {
      return false;
    }
    boolean ctrl = (event.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0;
    if (ctrl && event.getKeyCode() == KeyEvent.VK_A) {
      selectAll();
      return true;
    }
    return false;
  }

  public Set<Integer> selectedIndices() {
    return Collections.unmodifiableSet(selected);
  }

  public List<Path> selectedItems() {
    List<Path> out = new ArrayList<>();
    for (Integer index : selected) {
      if (index >= 0 && index < model.size()) {
        out.add(model.get(index));
      }
    }
    return List.copyOf(out);
  }

  public int getAnchor() {
    return anchor;
  }

  public void clearSelection() {
    selected.clear();
    anchor = -1;
  }
}
