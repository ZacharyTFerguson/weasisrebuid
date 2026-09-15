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

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.weasis.acquire.explorer.gui.central.tumbnail.AcquireCentralThumbnailModel.Item;

/** Central-pane thumbnail selection over imported series stills (click / Ctrl / Shift / Ctrl+A). */
public class AcquireCentralThumbnailList {

  private final AcquireCentralThumbnailModel model;
  private final LinkedHashSet<Integer> selected = new LinkedHashSet<>();
  private String seriesFilter;
  private int anchor = -1;

  public AcquireCentralThumbnailList() {
    this(new AcquireCentralThumbnailModel());
  }

  public AcquireCentralThumbnailList(AcquireCentralThumbnailModel model) {
    this.model = model == null ? new AcquireCentralThumbnailModel() : model;
  }

  public AcquireCentralThumbnailModel model() {
    return model;
  }

  public void showSeries(String series) {
    this.seriesFilter = series;
    selected.clear();
    anchor = -1;
  }

  public String seriesFilter() {
    return seriesFilter;
  }

  public List<Item> displayed() {
    return seriesFilter == null ? model.items() : model.itemsInSeries(seriesFilter);
  }

  public void click(int index, boolean ctrl, boolean shift) {
    List<Item> shown = displayed();
    if (index < 0 || index >= shown.size()) {
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
    int n = displayed().size();
    for (int i = 0; i < n; i++) {
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

  public List<Item> selectedItems() {
    List<Item> shown = displayed();
    List<Item> out = new ArrayList<>();
    for (Integer index : selected) {
      if (index >= 0 && index < shown.size()) {
        out.add(shown.get(index));
      }
    }
    return List.copyOf(out);
  }

  public void clearSelection() {
    selected.clear();
    anchor = -1;
  }
}
