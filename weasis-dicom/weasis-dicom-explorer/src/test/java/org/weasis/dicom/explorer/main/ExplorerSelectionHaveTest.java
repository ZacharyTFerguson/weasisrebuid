/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.JPanel;
import org.junit.jupiter.api.Test;
import org.weasis.dicom.explorer.DicomExplorer;
import org.weasis.dicom.explorer.DicomModel;
import org.weasis.dicom.explorer.ImportedInstance;

class ExplorerSelectionHaveTest {

  @Test
  void ctrlClickShiftClickCtrlAAndEnter() {
    SeriesSelectionModel model = new SeriesSelectionModel();
    model.setItems(List.of("a", "b", "c", "d"));
    ThumbnailMouseAndKeyAdapter adapter = new ThumbnailMouseAndKeyAdapter(model);
    adapter.pressed(1, false, false);
    assertEquals(1, model.selectedIndices().size());
    assertTrue(model.selectedIndices().contains(1));
    assertEquals(1, model.getPriorityIndex());
    adapter.pressed(3, true, false);
    assertEquals(2, model.selectedIndices().size());
    adapter.pressed(0, false, true);
    assertEquals(4, model.selectedIndices().size());
    JPanel host = new JPanel();
    adapter.keyPressed(
        new KeyEvent(
            host, KeyEvent.KEY_PRESSED, 0L, InputEvent.CTRL_DOWN_MASK, KeyEvent.VK_A, 'A'));
    assertEquals(4, model.selectedIndices().size());
    adapter.keyPressed(new KeyEvent(host, KeyEvent.KEY_PRESSED, 0L, 0, KeyEvent.VK_ENTER, '\n'));
    assertTrue(model.isOpened());
  }

  @Test
  void explorerRefreshFillsSelectionAndClickRaisesPriority() {
    DicomModel model = new DicomModel();
    model.addInstance(inst(1, "one"));
    model.addInstance(inst(2, "two"));
    DicomExplorer explorer = new DicomExplorer(model);
    assertEquals(2, explorer.seriesSelection().getItems().size());
    explorer.thumbnailAdapter().pressed(1, false, false);
    assertEquals(1, explorer.seriesSelection().getPriorityIndex());
    SeriesPane pane = new SeriesPane();
    pane.getSelectionModel().setItems(List.of("x", "y"));
    pane.getAdapter().pressed(0, true, false);
    assertTrue(pane.getSelectionModel().selectedIndices().contains(0));
  }

  static ImportedInstance inst(int series, String desc) {
    return new ImportedInstance(
        "SYNTHETIC^A",
        "SYN-1",
        "2.25.1",
        "2.25.2." + series,
        "2.25.3." + series,
        "1.2.840.10008.10.0.2.2.1.2",
        "CT",
        desc,
        "20260101",
        series,
        1,
        null,
        "image/dicom");
  }
}
