/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.explorer.gui.model.publish;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import org.weasis.acquire.explorer.gui.central.tumbnail.AcquireCentralThumbnailModel.Item;

/** Checkbox tree of series → stills to publish. Checking a series checks every child. */
public class PublishTreeModel {

  public record Node(String series, Path file) {
    public Node {
      series = series == null || series.isBlank() ? "Series" : series;
      Objects.requireNonNull(file, "file");
    }

    public static Node from(Item item) {
      return item == null ? null : new Node(item.series(), item.file());
    }
  }

  private final List<Node> images = new ArrayList<>();
  private final LinkedHashSet<Node> checked = new LinkedHashSet<>();

  public void add(String series, Path file) {
    if (file == null) {
      return;
    }
    Node node = new Node(series, file);
    images.add(node);
    checked.add(node);
  }

  public void add(Item item) {
    if (item != null) {
      add(item.series(), item.file());
    }
  }

  public void clear() {
    images.clear();
    checked.clear();
  }

  public void replace(List<Item> items) {
    clear();
    if (items == null) {
      return;
    }
    for (Item item : items) {
      add(item);
    }
  }

  public List<Node> images() {
    return Collections.unmodifiableList(images);
  }

  public List<String> seriesNames() {
    LinkedHashSet<String> names = new LinkedHashSet<>();
    for (Node node : images) {
      names.add(node.series());
    }
    return List.copyOf(names);
  }

  public List<Node> imagesInSeries(String series) {
    List<Node> out = new ArrayList<>();
    for (Node node : images) {
      if (node.series().equals(series)) {
        out.add(node);
      }
    }
    return List.copyOf(out);
  }

  public boolean isChecked(Node node) {
    return checked.contains(node);
  }

  public void setChecked(Node node, boolean value) {
    if (node == null || !images.contains(node)) {
      return;
    }
    if (value) {
      checked.add(node);
    } else {
      checked.remove(node);
    }
  }

  public void setSeriesChecked(String series, boolean value) {
    for (Node node : imagesInSeries(series)) {
      setChecked(node, value);
    }
  }

  public List<Node> checkedImages() {
    List<Node> out = new ArrayList<>();
    for (Node node : images) {
      if (checked.contains(node)) {
        out.add(node);
      }
    }
    return List.copyOf(out);
  }

  public List<Path> checkedFiles() {
    List<Path> out = new ArrayList<>();
    for (Node node : checkedImages()) {
      out.add(node.file());
    }
    return List.copyOf(out);
  }
}
