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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

/** Imported stills grouped by series in the dicomizer central pane. */
public class AcquireCentralThumbnailModel {

  public record Item(String series, Path file) {
    public Item {
      series = series == null || series.isBlank() ? "Series" : series;
      Objects.requireNonNull(file, "file");
    }
  }

  private final List<Item> items = new ArrayList<>();

  public void add(String series, Path file) {
    if (file != null) {
      items.add(new Item(series, file));
    }
  }

  public void add(Item item) {
    if (item != null) {
      items.add(item);
    }
  }

  public void clear() {
    items.clear();
  }

  public int size() {
    return items.size();
  }

  public List<Item> items() {
    return Collections.unmodifiableList(items);
  }

  public List<Item> itemsInSeries(String series) {
    if (series == null) {
      return items();
    }
    List<Item> out = new ArrayList<>();
    for (Item item : items) {
      if (series.equals(item.series())) {
        out.add(item);
      }
    }
    return List.copyOf(out);
  }

  public List<String> seriesNames() {
    LinkedHashSet<String> names = new LinkedHashSet<>();
    for (Item item : items) {
      names.add(item.series());
    }
    return List.copyOf(names);
  }
}
