/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.base.explorer;

import java.io.IOException;
import java.nio.file.Path;
import org.weasis.base.explorer.list.impl.JIListModel;

/** Selected directory plus the file list shown as thumbnails. */
public class JIExplorerContext {

  private FileTreeModel treeModel;
  private TreeNode selected;
  private final JIListModel listModel = new JIListModel();

  public void openRoot(Path root) throws IOException {
    this.treeModel = new FileTreeModel(root);
    select(treeModel.rootNode());
  }

  public void select(TreeNode node) throws IOException {
    this.selected = node;
    Path path = node == null ? null : node.getNodePath();
    listModel.loadDirectory(path);
  }

  public FileTreeModel treeModel() {
    return treeModel;
  }

  public TreeNode selected() {
    return selected;
  }

  public JIListModel files() {
    return listModel;
  }
}
