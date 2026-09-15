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

import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import org.weasis.core.api.media.data.Thumbnail;

/** Icon wrapping a cache thumbnail image. */
public class ThumbnailIcon implements Icon {

  private final BufferedImage image;
  private final int size;

  public ThumbnailIcon(BufferedImage image) {
    this.image = image;
    this.size =
        image == null
            ? Thumbnail.DEFAULT_SIZE
            : Math.max(1, Math.max(image.getWidth(), image.getHeight()));
  }

  public BufferedImage getImage() {
    return image;
  }

  @Override
  public void paintIcon(Component c, Graphics g, int x, int y) {
    if (g == null) {
      return;
    }
    if (image != null) {
      g.drawImage(image, x, y, getIconWidth(), getIconHeight(), c);
      return;
    }
    g.setColor(java.awt.Color.DARK_GRAY);
    g.fillRect(x, y, getIconWidth(), getIconHeight());
  }

  @Override
  public int getIconWidth() {
    return image == null ? size : Math.max(1, image.getWidth());
  }

  @Override
  public int getIconHeight() {
    return image == null ? size : Math.max(1, image.getHeight());
  }
}
