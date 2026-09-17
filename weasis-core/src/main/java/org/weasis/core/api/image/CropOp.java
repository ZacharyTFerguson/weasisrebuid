/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.image;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * Crop to {@code P_REGION} or x/y/width/height, intersected with the source. Output is a copy (does
 * not share the source raster). Missing region is a passthrough.
 */
public class CropOp extends AbstractOp {

  public static final String P_REGION = "region";
  public static final String P_X = "x";
  public static final String P_Y = "y";
  public static final String P_WIDTH = "width";
  public static final String P_HEIGHT = "height";

  public CropOp() {
    super("op.crop");
  }

  @Override
  protected void processEnabled() {
    setParam(OUTPUT_IMG, cropIfNeeded(getParam(INPUT_IMG)));
  }

  Object cropIfNeeded(Object in) {
    if (!(in instanceof BufferedImage src)) {
      return in;
    }
    Rectangle region = region();
    if (region == null) {
      return in;
    }
    return copyIntersect(src, region);
  }

  Rectangle region() {
    Object value = getParam(P_REGION);
    if (value instanceof Rectangle r) {
      return r;
    }
    if (value instanceof Rectangle2D r) {
      return r.getBounds();
    }
    if (!hasBox()) {
      return null;
    }
    return box();
  }

  boolean hasBox() {
    return getParam(P_WIDTH) != null && getParam(P_HEIGHT) != null;
  }

  Rectangle box() {
    return new Rectangle(
        (int) WindowOp.number(getParam(P_X), 0.0),
        (int) WindowOp.number(getParam(P_Y), 0.0),
        (int) WindowOp.number(getParam(P_WIDTH), 0.0),
        (int) WindowOp.number(getParam(P_HEIGHT), 0.0));
  }

  static BufferedImage copyIntersect(BufferedImage src, Rectangle region) {
    Rectangle box = region.intersection(new Rectangle(0, 0, src.getWidth(), src.getHeight()));
    if (box.isEmpty()) {
      return src;
    }
    return copy(src, box);
  }

  static BufferedImage copy(BufferedImage src, Rectangle box) {
    int type =
        src.getType() == BufferedImage.TYPE_CUSTOM ? BufferedImage.TYPE_INT_ARGB : src.getType();
    BufferedImage dst = new BufferedImage(box.width, box.height, type);
    Graphics2D g = dst.createGraphics();
    try {
      g.drawImage(
          src,
          0,
          0,
          box.width,
          box.height,
          box.x,
          box.y,
          box.x + box.width,
          box.y + box.height,
          null);
    } finally {
      g.dispose();
    }
    return dst;
  }
}
