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
import java.util.ArrayList;
import java.util.List;
import javax.swing.tree.DefaultTreeModel;
import org.weasis.base.explorer.list.DiskFileList;

/** Directory tree for the non-DICOM explorer. */
public class FileTreeModel extends DefaultTreeModel {

  private final DiskFileList disk;

  public FileTreeModel(Path root) {
    this(root, new DiskFileList());
  }

  public FileTreeModel(Path root, DiskFileList disk) {
    super(new TreeNode(root));
    this.disk = disk == null ? new DiskFileList() : disk;
    load(rootNode());
  }

  public DiskFileList disk() {
    return disk;
  }

  public TreeNode rootNode() {
    return (TreeNode) getRoot();
  }

  public void load(TreeNode node) {
    if (node == null || !node.directory()) {
      return;
    }
    node.removeAllChildren();
    List<Path> children;
    try {
      children = disk.listDirectories(node.getNodePath());
    } catch (IOException e) {
      return;
    }
    for (Path child : children) {
      TreeNode childNode = new TreeNode(child);
      node.add(childNode);
      load(childNode);
    }
    nodeStructureChanged(node);
  }

  public List<Path> directories() {
    List<Path> out = new ArrayList<>();
    collect(rootNode(), out);
    return List.copyOf(out);
  }

  private static void collect(TreeNode node, List<Path> out) {
    if (node == null) {
      return;
    }
    Path path = node.getNodePath();
    if (path != null) {
      out.add(path);
    }
    for (int i = 0; i < node.getChildCount(); i++) {
      if (node.getChildAt(i) instanceof TreeNode child) {
        collect(child, out);
      }
    }
  }
}
