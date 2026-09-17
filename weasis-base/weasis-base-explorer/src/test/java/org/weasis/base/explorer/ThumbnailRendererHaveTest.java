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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.base.explorer.list.AThumbnailModel;
import org.weasis.base.explorer.list.IThumbnailListPane;
import org.weasis.base.explorer.list.ThumbnailList;
import org.weasis.base.explorer.list.impl.DefaultThumbnailList;
import org.weasis.base.explorer.list.impl.JIThumbnailListPane;

class ThumbnailRendererHaveTest {

  @Test
  void rendererPaintsThumbnailIconFromCache(@TempDir Path dir) throws Exception {
    Path png = dir.resolve("cell.png");
    writePng(png, 0xFF3366);
    JIThumbnailCache cache = new JIThumbnailCache();
    ThumbnailRenderer renderer = new ThumbnailRenderer(cache);
    ThumbnailIcon icon = renderer.iconFor(png);
    assertInstanceOf(Icon.class, icon);
    assertNotNull(icon.getImage());
    assertSame(cache.get(png.toUri()), icon.getImage());
    BufferedImage painted = renderer.paintToImage(png);
    assertEquals(icon.getIconWidth(), painted.getWidth());
    assertNotEquals(0, painted.getRGB(0, 0));
    assertTrue(cache.contains(png.toUri()));
  }

  @Test
  void paneExposesThumbnailListModelAndRenderer(@TempDir Path dir) throws Exception {
    Path png = dir.resolve("a.png");
    writePng(png, 0x00AAFF);
    DefaultThumbnailList list = new DefaultThumbnailList();
    list.setItems(java.util.List.of(png));
    JIThumbnailListPane pane = new JIThumbnailListPane(list);
    assertInstanceOf(IThumbnailListPane.class, pane);
    assertInstanceOf(ThumbnailList.class, pane.thumbnailList());
    assertInstanceOf(AThumbnailModel.class, pane.thumbnailList().model());
    assertEquals(1, pane.thumbnailList().model().getSize());
    assertEquals(png, pane.thumbnailList().model().getElementAt(0));
    ThumbnailIcon icon = pane.renderer().iconFor(png);
    assertSame(pane.cache().get(png.toUri()), icon.getImage());
    BufferedImage painted = pane.renderer().paintToImage(png);
    assertEquals(icon.getImage().getWidth(), painted.getWidth());
  }

  static void writePng(Path path, int rgb) throws Exception {
    BufferedImage image = new BufferedImage(8, 8, BufferedImage.TYPE_INT_RGB);
    for (int y = 0; y < 8; y++) {
      for (int x = 0; x < 8; x++) {
        image.setRGB(x, y, rgb);
      }
    }
    ImageIO.write(image, "png", path.toFile());
  }
}
