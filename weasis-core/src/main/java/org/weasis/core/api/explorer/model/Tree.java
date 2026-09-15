/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package org.weasis.core.api.explorer.model;

import java.util.ArrayList;
import java.util.List;
import org.weasis.core.api.media.data.MediaSeriesGroup;

public class Tree {
  private final MediaSeriesGroup node;
  private final List<Tree> children = new ArrayList<>();

  public Tree(MediaSeriesGroup node) {
    this.node = node;
  }

  public MediaSeriesGroup getNode() {
    return node;
  }

  public List<Tree> getChildren() {
    return children;
  }

  public void add(Tree child) {
    if (child != null) {
      children.add(child);
    }
  }
}
