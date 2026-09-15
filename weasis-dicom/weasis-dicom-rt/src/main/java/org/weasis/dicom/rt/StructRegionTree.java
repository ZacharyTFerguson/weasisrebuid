/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.rt;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/** Patient RT ROI list (name + number). */
public class StructRegionTree extends JTree {

  public StructRegionTree() {
    super(new DefaultTreeModel(new DefaultMutableTreeNode("RT")));
  }

  public void setStructureSet(StructureSet structureSet) {
    DefaultMutableTreeNode root =
        new DefaultMutableTreeNode(
            structureSet == null || structureSet.label().isBlank() ? "RT" : structureSet.label());
    if (structureSet != null) {
      for (StructRegion region : structureSet.regions()) {
        root.add(new DefaultMutableTreeNode(region.name() + " [" + region.number() + "]"));
      }
    }
    setModel(new DefaultTreeModel(root));
  }

  public int regionCount() {
    return ((DefaultMutableTreeNode) getModel().getRoot()).getChildCount();
  }
}
