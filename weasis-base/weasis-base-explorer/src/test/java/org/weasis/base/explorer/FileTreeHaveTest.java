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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JTree;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.base.explorer.list.DiskFileList;
import org.weasis.base.explorer.list.IThumbnailModel;
import org.weasis.base.explorer.list.impl.JIListModel;

class FileTreeHaveTest {

  @Test
  void diskListAndTreeModelListADirectory(@TempDir Path dir) throws Exception {
    Path photos = Files.createDirectory(dir.resolve("photos"));
    Path a = Files.createFile(dir.resolve("a.png"));
    Path b = Files.createFile(dir.resolve("b.jpg"));
    Files.createFile(dir.resolve(".hidden"));
    Files.createFile(photos.resolve("c.png"));

    DiskFileList disk = new DiskFileList();
    assertEquals(2, disk.listFiles(dir).size());
    assertEquals(a, disk.listFiles(dir).get(0));
    assertEquals(b, disk.listFiles(dir).get(1));
    assertEquals(1, disk.listDirectories(dir).size());
    assertEquals(photos, disk.listDirectories(dir).getFirst());

    FileTreeModel tree = new FileTreeModel(dir, disk);
    assertEquals(2, tree.directories().size());
    assertTrue(tree.directories().contains(dir));
    assertTrue(tree.directories().contains(photos));
    assertEquals(dir.getFileName().toString(), tree.rootNode().name());
    assertTrue(tree.rootNode().directory());

    TreeRenderer renderer = new TreeRenderer();
    assertEquals("photos", renderer.labelFor(new TreeNode(photos)));
    JTree jtree = new JTree(tree);
    jtree.setCellRenderer(renderer);
    assertEquals("photos", renderer.labelFor(tree.rootNode().getChildAt(0)));

    AtomicInteger fires = new AtomicInteger();
    JIListModel model = new JIListModel(disk);
    assertInstanceOf(IThumbnailModel.class, model);
    model.observable().addPropertyChangeListener(e -> fires.incrementAndGet());
    model.loadDirectory(dir);
    assertEquals(2, model.getSize());
    assertEquals(a, model.getElementAt(0));
    assertTrue(fires.get() >= 1);

    JIExplorerContext context = new JIExplorerContext();
    context.openRoot(dir);
    assertEquals(2, context.files().getSize());
    context.select((TreeNode) context.treeModel().rootNode().getChildAt(0));
    assertEquals(1, context.files().getSize());
    assertEquals("c.png", context.files().getElementAt(0).getFileName().toString());

    DefaultExplorer explorer = new DefaultExplorer();
    explorer.explorerContext().openRoot(dir);
    assertFalse(explorer.explorerContext().files().items().isEmpty());
  }
}
