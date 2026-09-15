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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

public class Thumbnail extends JPanel implements Thumbnailable {
  public static final int MIN_SIZE = 48;
  public static final int DEFAULT_SIZE = 144;
  public static final int MAX_SIZE = 256;

  private BufferedImage image;
  private int thumbnailSize = DEFAULT_SIZE;

  public Thumbnail() {
    this(DEFAULT_SIZE);
  }

  public Thumbnail(int thumbnailSize) {
    this.thumbnailSize = Math.min(MAX_SIZE, Math.max(MIN_SIZE, thumbnailSize));
    setPreferredSize(new Dimension(this.thumbnailSize, this.thumbnailSize));
  }

  public void setThumbnail(BufferedImage image) {
    this.image = image;
    repaint();
  }

  @Override
  public BufferedImage getThumbnail() {
    return image;
  }

  @Override
  public void dispose() {
    image = null;
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (image != null) {
      g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
    }
  }
}
