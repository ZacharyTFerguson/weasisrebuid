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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.swing.JList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.acquire.explorer.AcquireManager;
import org.weasis.acquire.explorer.ImportGrouping;
import org.weasis.acquire.explorer.gui.central.ImageGroupPane;
import org.weasis.acquire.explorer.gui.control.BrowsePanel;
import org.weasis.acquire.explorer.gui.control.ImportPanel;
import org.weasis.acquire.explorer.gui.model.renderer.MediaSourceListCellRenderer;
import org.weasis.acquire.explorer.media.FileSystemDrive;

class AcquireBrowseSeriesHaveTest {

  @Test
  void fileSystemDriveListsStillsAndBrowseSelectsSource(@TempDir Path dir) throws Exception {
    Path png = file(dir, "chest.png");
    file(dir, "notes.txt");
    Path jpg = file(dir, "hand.jpg");
    FileSystemDrive drive = new FileSystemDrive(dir);
    List<Path> stills = drive.listStills();
    assertEquals(2, stills.size());
    assertTrue(stills.contains(png));
    assertTrue(stills.contains(jpg));

    BrowsePanel browse = new BrowsePanel();
    browse.addSource(drive);
    assertSame(drive, browse.selected());
    assertInstanceOf(FileSystemDrive.class, browse.selected());
    assertEquals(2, browse.thumbnails().model().size());
    assertTrue(browse.thumbnails().model().items().contains(png));
    assertTrue(browse.thumbnails().model().items().contains(jpg));

    MediaSourceListCellRenderer renderer = new MediaSourceListCellRenderer();
    renderer.getListCellRendererComponent(new JList<>(), drive, 0, false, false);
    assertEquals(drive.getDisplayName(), renderer.getText());

    Path nested = dir.resolve("camera");
    Files.createDirectory(nested);
    Path extra = file(nested, "extra.png");
    browse.changePath().applyPath(nested);
    assertEquals(nested, drive.getPath());
    assertEquals(List.of(extra), browse.thumbnails().model().items());
  }

  @Test
  void importPanelKicksGroupingIntoAlbumAndSeriesButtonsSwitch(@TempDir Path dir)
      throws Exception {
    Path a1 = file(dir, "seriesA1.png");
    Path a2 = file(dir, "seriesA2.png");
    Path other = file(dir, "other.png");
    FileSystemDrive drive = new FileSystemDrive(dir);
    BrowsePanel browse = new BrowsePanel();
    browse.addSource(drive);
    browse.thumbnails().thumbnailList().selectAll();
    assertEquals(3, browse.thumbnails().selected().size());

    ImageGroupPane album = new ImageGroupPane();
    AcquireManager manager = new AcquireManager();
    ImportPanel importPanel = new ImportPanel(browse, album, manager);
    importPanel.dialog().setGrouping(ImportGrouping.NAME);
    importPanel.importSelected();

    assertEquals(3, manager.getImages().size());
    assertEquals(2, album.tabPanel().seriesButtons().seriesNames().size());
    assertTrue(album.tabPanel().seriesButtons().seriesNames().contains("seriesa"));
    assertTrue(album.tabPanel().seriesButtons().seriesNames().contains("other"));

    album.tabPanel().seriesButtons().button("seriesa").doClick();
    assertEquals("seriesa", album.tabPanel().selectedSeries());
    assertEquals(2, album.tabPanel().imagePanel().displayed().size());
    assertTrue(
        album.tabPanel().imagePanel().displayed().stream()
            .allMatch(item -> "seriesa".equals(item.series())));
    assertEquals("seriesa (2)", album.tabPanel().infoPanel().text());

    album.tabPanel().seriesButtons().button("other").doClick();
    assertEquals("other", album.tabPanel().selectedSeries());
    assertEquals(1, album.tabPanel().imagePanel().displayed().size());
    assertEquals(other, album.tabPanel().imagePanel().displayed().get(0).file());
    assertEquals("other (1)", album.tabPanel().infoPanel().text());
    assertFalse(album.tabPanel().seriesButtons().button("seriesa").isSelected());
    assertTrue(album.tabPanel().seriesButtons().button("other").isSelected());
    album.tabPanel().selectSeries("seriesa");
    assertEquals(2, album.tabPanel().imagePanel().displayed().size());
    assertTrue(
        album.tabPanel().imagePanel().displayed().stream()
            .map(item -> item.file())
            .toList()
            .containsAll(List.of(a1, a2)));
  }

  static Path file(Path dir, String name) throws Exception {
    Path path = dir.resolve(name);
    Files.writeString(path, name);
    return path;
  }
}
