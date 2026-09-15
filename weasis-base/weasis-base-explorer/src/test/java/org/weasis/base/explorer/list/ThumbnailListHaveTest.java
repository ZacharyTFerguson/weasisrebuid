/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.base.explorer.list;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.swing.JPanel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.base.explorer.DefaultExplorer;
import org.weasis.base.explorer.list.impl.JIThumbnailListPane;

class ThumbnailListHaveTest {

  @Test
  void ctrlClickShiftClickCtrlAEnterAndPriority(@TempDir Path dir) throws Exception {
    Path a = Files.createFile(dir.resolve("a.png"));
    Path b = Files.createFile(dir.resolve("b.png"));
    Path c = Files.createFile(dir.resolve("c.png"));
    Path d = Files.createFile(dir.resolve("d.png"));
    JIThumbnailListPane pane = new JIThumbnailListPane();
    pane.loadDirectory(dir);
    assertEquals(4, pane.thumbnailList().size());
    pane.click(1, false, false);
    assertEquals(1, pane.selected().size());
    assertEquals(b, pane.selected().getFirst());
    assertEquals(1, pane.getPriorityIndex());
    pane.click(3, true, false);
    assertEquals(2, pane.selected().size());
    pane.click(0, false, true);
    assertEquals(4, pane.selected().size());
    pane.click(2, false, false);
    JPanel host = new JPanel();
    pane.keyPressed(
        new KeyEvent(
            host, KeyEvent.KEY_PRESSED, 0L, InputEvent.CTRL_DOWN_MASK, KeyEvent.VK_A, 'A'));
    assertEquals(4, pane.selected().size());
    pane.click(2, false, false);
    pane.keyPressed(new KeyEvent(host, KeyEvent.KEY_PRESSED, 0L, 0, KeyEvent.VK_ENTER, '\n'));
    assertTrue(pane.isOpened());
    assertEquals(2, pane.getPriorityIndex());
    assertEquals(c, pane.thumbnailList().get(2));
  }

  @Test
  void defaultExplorerHostsThumbnailPane() {
    DefaultExplorer explorer = new DefaultExplorer();
    explorer.thumbnailPane().setItems(List.of());
    assertFalse(explorer.thumbnailPane().isOpened());
    assertEquals(-1, explorer.thumbnailPane().getPriorityIndex());
  }
}
