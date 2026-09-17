/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.graphic.imp.seg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Tree node grouping {@link SegRegion} entries in the segmentation tool. */
public class GroupTreeNode {

  private String name = "";
  private final List<SegRegion> regions = new ArrayList<>();
  private final List<GroupTreeNode> children = new ArrayList<>();

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name == null ? "" : name;
  }

  public List<SegRegion> getRegions() {
    return Collections.unmodifiableList(regions);
  }

  public void addRegion(SegRegion region) {
    if (region != null) {
      regions.add(region);
    }
  }

  public List<GroupTreeNode> getChildren() {
    return Collections.unmodifiableList(children);
  }

  public void addChild(GroupTreeNode child) {
    if (child != null) {
      children.add(child);
    }
  }
}
