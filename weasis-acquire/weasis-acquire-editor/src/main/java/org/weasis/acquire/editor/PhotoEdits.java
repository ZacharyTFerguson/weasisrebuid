/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.editor;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

/** Photo Editor ops: crop, rotate, contrast. Drawings/measurements reuse WP-5 graphics. */
public final class PhotoEdits {

  private PhotoEdits() {}

  public static BufferedImage rotate90(BufferedImage src) {
    int type = src.getType() == 0 ? BufferedImage.TYPE_INT_RGB : src.getType();
    BufferedImage out = new BufferedImage(src.getHeight(), src.getWidth(), type);
    Graphics2D g = out.createGraphics();
    AffineTransform at = new AffineTransform();
    at.translate(src.getHeight(), 0);
    at.rotate(Math.PI / 2);
    g.drawImage(src, at, null);
    g.dispose();
    return out;
  }

  public static BufferedImage crop(BufferedImage src, Rectangle r) {
    Rectangle bounds = r.intersection(new Rectangle(0, 0, src.getWidth(), src.getHeight()));
    return src.getSubimage(bounds.x, bounds.y, bounds.width, bounds.height);
  }

  public static BufferedImage contrast(BufferedImage src, float factor) {
    BufferedImage work = src;
    if (src.getType() != BufferedImage.TYPE_3BYTE_BGR
        && src.getType() != BufferedImage.TYPE_BYTE_GRAY) {
      work = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
      Graphics2D g = work.createGraphics();
      g.drawImage(src, 0, 0, null);
      g.dispose();
    }
    RescaleOp op = new RescaleOp(factor, 0, null);
    return op.filter(work, null);
  }
}
