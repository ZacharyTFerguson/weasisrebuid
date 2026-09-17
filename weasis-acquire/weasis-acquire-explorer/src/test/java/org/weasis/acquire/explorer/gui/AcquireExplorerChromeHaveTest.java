/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.explorer.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import javax.swing.JPanel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.acquire.explorer.AcquireDest;
import org.weasis.acquire.explorer.AcquireManager;
import org.weasis.acquire.explorer.ImportGrouping;
import org.weasis.acquire.explorer.gui.central.tumbnail.AcquireCentralThumbnailList;
import org.weasis.acquire.explorer.gui.central.tumbnail.AcquireCentralThumbnailModel;
import org.weasis.acquire.explorer.gui.central.tumbnail.AcquireCentralThumbnailModel.Item;
import org.weasis.acquire.explorer.gui.central.tumbnail.AcquireCentralThumbnailPane;
import org.weasis.acquire.explorer.gui.control.AcquirePublishPanel;
import org.weasis.acquire.explorer.gui.dialog.AcquireImportDialog;
import org.weasis.acquire.explorer.gui.dialog.AcquirePublishDialog;
import org.weasis.acquire.explorer.gui.list.AcquireThumbnailListPane;
import org.weasis.acquire.explorer.gui.model.publish.PublishTreeModel.Node;

class AcquireExplorerChromeHaveTest {

  @Test
  void browseThumbnailsCtrlShiftAndCtrlA(@TempDir Path dir) throws Exception {
    Path a = file(dir, "a.png");
    Path b = file(dir, "b.png");
    Path c = file(dir, "c.png");
    Path d = file(dir, "d.png");
    AcquireThumbnailListPane pane = new AcquireThumbnailListPane();
    pane.setItems(List.of(a, b, c, d));
    pane.click(1, false, false);
    assertEquals(1, pane.selected().size());
    assertEquals(b, pane.selected().get(0));
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
  }

  @Test
  void importDialogGroupsIntoCentralSeriesAndManager(@TempDir Path dir) throws Exception {
    Path a1 = file(dir, "seriesA1.png");
    Path a2 = file(dir, "seriesA2.png");
    Path other = file(dir, "other.png");
    AcquireThumbnailListPane browse = new AcquireThumbnailListPane();
    browse.setItems(List.of(a1, a2, other));
    browse.thumbnailList().selectAll();

    AcquireImportDialog dialog = new AcquireImportDialog();
    dialog.setGrouping(ImportGrouping.NAME);
    dialog.setPlacement(AcquireImportDialog.Placement.NEW_SERIES);
    AcquireCentralThumbnailPane central = new AcquireCentralThumbnailPane();
    AcquireManager manager = new AcquireManager();
    dialog.importInto(manager, central.model(), browse.selected());
    assertEquals(2, central.model().seriesNames().size());
    assertTrue(central.model().seriesNames().contains("seriesa"));
    assertEquals(2, central.model().itemsInSeries("seriesa").size());
    assertEquals(3, manager.getImages().size());

    AcquireCentralThumbnailModel current = new AcquireCentralThumbnailModel();
    dialog.setPlacement(AcquireImportDialog.Placement.CURRENT_SERIES);
    dialog.setCurrentSeries("Album-1");
    dialog.importInto(current, List.of(a1, other));
    assertEquals(List.of("Album-1"), current.seriesNames());
    assertEquals(2, current.itemsInSeries("Album-1").size());
  }

  @Test
  void importByDateSplitsOnMaxGap(@TempDir Path dir) throws Exception {
    Path early = file(dir, "early.png");
    Path late = file(dir, "late.png");
    Files.setLastModifiedTime(early, java.nio.file.attribute.FileTime.fromMillis(1_000L));
    Files.setLastModifiedTime(late, java.nio.file.attribute.FileTime.fromMillis(3_600_000L * 5));
    AcquireImportDialog dialog = new AcquireImportDialog();
    dialog.setGrouping(ImportGrouping.DATE);
    dialog.setMaxGap(Duration.ofHours(1));
    AcquireCentralThumbnailModel central = new AcquireCentralThumbnailModel();
    dialog.importInto(central, List.of(early, late));
    assertEquals(2, central.seriesNames().size());
  }

  @Test
  void centralThumbnailsFilterSeriesAndSelectForPublish(@TempDir Path dir) throws Exception {
    Path chest = file(dir, "chest.png");
    Path chest2 = file(dir, "chest2.png");
    Path hand = file(dir, "hand.png");
    AcquireCentralThumbnailList list = new AcquireCentralThumbnailList();
    list.model().add("chest", chest);
    list.model().add("chest", chest2);
    list.model().add("hand", hand);
    list.showSeries("chest");
    assertEquals(2, list.displayed().size());
    list.click(0, false, false);
    list.click(1, true, false);
    assertEquals(2, list.selectedItems().size());
    list.showSeries("hand");
    assertEquals(1, list.displayed().size());
    assertTrue(list.selectedItems().isEmpty());
    list.selectAll();
    assertEquals(hand, list.selectedItems().get(0).file());
  }

  @Test
  void publishTreeUncheckSeriesAndDialogSelectionPlansCstore(@TempDir Path dir) throws Exception {
    Path one = file(dir, "one.png");
    Path two = file(dir, "two.png");
    Path three = file(dir, "three.png");
    List<Item> all = List.of(new Item("s1", one), new Item("s1", two), new Item("s2", three));
    AcquirePublishPanel panel = new AcquirePublishPanel();
    panel.dialog().setScope(AcquirePublishDialog.Scope.ALL);
    panel.dialog().setResolutionDownscale(2);
    Properties prefs = new Properties();
    prefs.setProperty("weasis.acquire.dest.host", "localhost");
    prefs.setProperty("weasis.acquire.dest.aet", "DCM4CHEE");
    prefs.setProperty("weasis.acquire.dest.port", "11112");
    AcquireDest.Publication pub = panel.prepare(prefs, all, List.of(all.get(0)), "WEASIS");
    assertEquals(AcquireDest.PublishMode.CSTORE, pub.mode());
    assertTrue(pub.destination().contains("DCM4CHEE"));
    assertFalse(pub.selection());
    assertEquals(2, pub.resolutionDownscale());
    assertEquals(3, panel.checkedFiles().size());
    panel.tree().setSeriesChecked("s1", false);
    assertEquals(1, panel.checkedFiles().size());
    assertEquals(three, panel.checkedFiles().get(0));

    panel.dialog().setScope(AcquirePublishDialog.Scope.SELECTION);
    panel.dialog().setOriginalResolution();
    Properties unlocked = new Properties();
    AcquireDest.Publication local = panel.prepare(unlocked, all, List.of(all.get(2)), "WEASIS");
    assertEquals(AcquireDest.PublishMode.LOCAL_EXPORT, local.mode());
    assertTrue(local.selection());
    assertEquals(0, local.resolutionDownscale());
    assertEquals(1, panel.checkedFiles().size());
    Node only = panel.tree().checkedImages().get(0);
    panel.tree().toggle(only);
    assertTrue(panel.checkedFiles().isEmpty());
  }

  static Path file(Path dir, String name) throws Exception {
    Path path = dir.resolve(name);
    Files.writeString(path, name);
    return path;
  }
}
