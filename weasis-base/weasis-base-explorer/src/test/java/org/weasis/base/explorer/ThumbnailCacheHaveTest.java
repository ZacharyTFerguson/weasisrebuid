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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.base.explorer.list.AThumbnailListPane;
import org.weasis.base.explorer.list.impl.DefaultThumbnailList;
import org.weasis.base.explorer.list.impl.JIThumbnailListPane;
import org.weasis.core.api.media.data.Thumbnail;

class ThumbnailCacheHaveTest {

  @Test
  void cacheStoresImagesByUriAndEvictsLru(@TempDir Path dir) throws Exception {
    Path a = dir.resolve("a.png");
    Path b = dir.resolve("b.png");
    writePng(a, 0xFF0000);
    writePng(b, 0x00FF00);
    JIThumbnailCache cache = new JIThumbnailCache(1);
    URI ua = a.toUri();
    URI ub = b.toUri();
    BufferedImage first = cache.getOrLoad(a);
    assertNotNull(first);
    assertTrue(cache.contains(ua));
    assertSame(first, cache.get(ua));
    assertSame(first, cache.getOrLoad(ua));
    cache.getOrLoad(b);
    assertTrue(cache.contains(ub));
    assertEquals(1, cache.size());
    assertFalse(cache.contains(ua));
    cache.put(ua, first);
    assertEquals(Thumbnail.DEFAULT_SIZE, cache.get(ua).getWidth());
  }

  @Test
  void defaultListAndPaneShareCacheByUri(@TempDir Path dir) throws Exception {
    Path png = dir.resolve("thumb.png");
    writePng(png, 0x0000FF);
    DefaultThumbnailList list = new DefaultThumbnailList();
    list.setItems(java.util.List.of(png));
    BufferedImage image = list.thumbnailAt(0);
    assertNotNull(image);
    assertTrue(list.getCache().contains(png.toUri()));
    assertSame(image, list.getCache().get(png.toUri()));

    AThumbnailListPane pane = new AThumbnailListPane(list);
    assertSame(list.getCache(), pane.cache());
    assertSame(image, pane.thumbnailAt(0));
    pane.click(0, false, false);
    assertEquals(png, pane.selected().getFirst());
  }

  @Test
  void defaultExplorerThumbnailPaneUsesCache(@TempDir Path dir) throws Exception {
    Path png = dir.resolve("c.png");
    writePng(png, 0xFFFFFF);
    DefaultExplorer explorer = new DefaultExplorer();
    assertInstanceOf(JIThumbnailListPane.class, explorer.thumbnailPane());
    assertInstanceOf(AThumbnailListPane.class, explorer.thumbnailPane());
    assertInstanceOf(DefaultThumbnailList.class, explorer.thumbnailPane().thumbnailList());
    explorer.thumbnailPane().setItems(java.util.List.of(png));
    BufferedImage image = explorer.thumbnailPane().thumbnailAt(0);
    assertNotNull(image);
    assertTrue(explorer.thumbnailPane().cache().contains(png.toUri()));
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
