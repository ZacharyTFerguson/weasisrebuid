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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import org.weasis.core.api.media.data.Thumbnail;

/** Paints a {@link ThumbnailIcon} loaded from {@link JIThumbnailCache}. */
public class ThumbnailRenderer extends DefaultListCellRenderer {

  private final JIThumbnailCache cache;

  public ThumbnailRenderer() {
    this(new JIThumbnailCache());
  }

  public ThumbnailRenderer(JIThumbnailCache cache) {
    this.cache = cache == null ? new JIThumbnailCache() : cache;
  }

  public JIThumbnailCache cache() {
    return cache;
  }

  public ThumbnailIcon iconFor(Path path) {
    if (path == null) {
      return new ThumbnailIcon(null);
    }
    return new ThumbnailIcon(cache.getOrLoad(path));
  }

  public BufferedImage paintToImage(Path path) {
    ThumbnailIcon icon = iconFor(path);
    int width = Math.max(Thumbnail.MIN_SIZE, icon.getIconWidth());
    int height = Math.max(Thumbnail.MIN_SIZE, icon.getIconHeight());
    BufferedImage out = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = out.createGraphics();
    g.setColor(Color.BLACK);
    g.fillRect(0, 0, width, height);
    icon.paintIcon(this, g, 0, 0);
    g.dispose();
    return out;
  }

  @Override
  public Component getListCellRendererComponent(
      JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    Path path = value instanceof Path p ? p : null;
    ThumbnailIcon icon = iconFor(path);
    setIcon(icon);
    setText(path == null ? "" : path.getFileName().toString());
    return this;
  }
}
