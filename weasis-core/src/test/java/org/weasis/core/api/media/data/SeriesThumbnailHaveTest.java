/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.media.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.junit.jupiter.api.Test;

class SeriesThumbnailHaveTest {

  @Test
  void overlayIconPaintsYellowBadgeInCorner() {
    Series<MediaElement> series = new Series<>("2.25.ko");
    SeriesThumbnail thumb = new SeriesThumbnail(series, Thumbnail.MIN_SIZE);
    thumb.setSize(48, 48);
    thumb.setOverlayIcon("KO");
    assertEquals("KO", thumb.getOverlayIcon());
    BufferedImage page = new BufferedImage(48, 48, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = page.createGraphics();
    try {
      thumb.paint(g);
    } finally {
      g.dispose();
    }
    assertTrue(yellowNear(page, 40, 40));
  }

  static boolean yellowNear(BufferedImage page, int x, int y) {
    for (int dy = -4; dy <= 4; dy++) {
      for (int dx = -4; dx <= 4; dx++) {
        if (isYellow(page, x + dx, y + dy)) {
          return true;
        }
      }
    }
    return false;
  }

  static boolean isYellow(BufferedImage page, int x, int y) {
    if (x < 0 || y < 0 || x >= page.getWidth() || y >= page.getHeight()) {
      return false;
    }
    int rgb = page.getRGB(x, y);
    int r = (rgb >> 16) & 255;
    int green = (rgb >> 8) & 255;
    int b = rgb & 255;
    return r > 200 && green > 200 && b < 80;
  }
}
