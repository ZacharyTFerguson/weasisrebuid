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
import java.util.List;
import org.weasis.acquire.explorer.gui.central.tumbnail.AcquireCentralThumbnailModel.Item;
import org.weasis.acquire.explorer.gui.model.publish.PublishTreeModel.Node;

/** Publish checkbox tree chrome over {@link PublishTreeModel}. */
public class PublishTree {

  private final PublishTreeModel model;

  public PublishTree() {
    this(new PublishTreeModel());
  }

  public PublishTree(PublishTreeModel model) {
    this.model = model == null ? new PublishTreeModel() : model;
  }

  public PublishTreeModel model() {
    return model;
  }

  public void load(List<Item> items) {
    model.replace(items);
  }

  public void toggle(Node node) {
    model.setChecked(node, !model.isChecked(node));
  }

  public void setSeriesChecked(String series, boolean value) {
    model.setSeriesChecked(series, value);
  }

  public List<Node> checkedImages() {
    return model.checkedImages();
  }

  public List<Path> checkedFiles() {
    return model.checkedFiles();
  }
}
