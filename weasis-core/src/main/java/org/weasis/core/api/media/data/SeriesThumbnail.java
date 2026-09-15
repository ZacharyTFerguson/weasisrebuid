/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.eclipse.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.media.data;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

/** Series thumbnail. Overlay MIME families (KO/PR/SEG/RT) paint a corner badge. */
public class SeriesThumbnail extends Thumbnail {
  private final MediaSeries<?> series;
  private String overlayIcon = "";

  public SeriesThumbnail(MediaSeries<?> series, int thumbnailSize) {
    super(thumbnailSize);
    this.series = series;
  }

  public MediaSeries<?> getSeries() {
    return series;
  }

  public void setOverlayIcon(String overlayIcon) {
    this.overlayIcon = overlayIcon == null ? "" : overlayIcon.trim();
    repaint();
  }

  public String getOverlayIcon() {
    return overlayIcon;
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    paintOverlayIcon(g);
  }

  void paintOverlayIcon(Graphics g) {
    if (overlayIcon.isBlank() || g == null) {
      return;
    }
    Graphics2D g2 = (Graphics2D) g.create();
    try {
      drawBadge(g2);
    } finally {
      g2.dispose();
    }
  }

  void drawBadge(Graphics2D g) {
    int w = Math.max(1, getWidth());
    int h = Math.max(1, getHeight());
    int bw = Math.min(22, w);
    int bh = Math.min(14, h);
    int x = Math.max(0, w - bw - 1);
    int y = Math.max(0, h - bh - 1);
    g.setColor(new Color(0, 0, 0, 180));
    g.fillRect(x, y, bw, bh);
    g.setColor(Color.YELLOW);
    g.setFont(g.getFont().deriveFont(Font.BOLD, 9f));
    g.drawString(overlayIcon, x + 2, y + bh - 3);
  }
}
