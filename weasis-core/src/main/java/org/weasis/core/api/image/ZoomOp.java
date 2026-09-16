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
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * Raster zoom. Command range 0.0–12.0; {@code 0} default; {@code -200} best fit; {@code -100} real
 * size. Factor {@code 1.0} is a passthrough.
 */
public class ZoomOp extends AbstractOp {

  public static final String P_ZOOM = AffineTransformOp.P_ZOOM;
  public static final String P_VIEW_WIDTH = "view.width";
  public static final String P_VIEW_HEIGHT = "view.height";

  public ZoomOp() {
    super("op.zoom");
    setParam(P_ZOOM, 0.0);
  }

  @Override
  protected void processEnabled() {
    setParam(OUTPUT_IMG, zoomIfNeeded(getParam(INPUT_IMG)));
  }

  Object zoomIfNeeded(Object in) {
    if (!(in instanceof BufferedImage src)) {
      return in;
    }
    double scale = factor(src.getWidth(), src.getHeight());
    if (scale == 1.0) {
      return in;
    }
    return scale(src, scale);
  }

  double factor(int imgW, int imgH) {
    double zoom = WindowOp.number(getParam(P_ZOOM), 0.0);
    if (zoom == AffineTransformOp.ZOOM_BEST_FIT) {
      return bestFit(imgW, imgH);
    }
    if (zoom == AffineTransformOp.ZOOM_REAL_SIZE || zoom <= 0) {
      return 1.0;
    }
    return zoom;
  }

  double bestFit(int imgW, int imgH) {
    int viewW = (int) WindowOp.number(getParam(P_VIEW_WIDTH), 0.0);
    int viewH = (int) WindowOp.number(getParam(P_VIEW_HEIGHT), 0.0);
    if (!positive(viewW, viewH) || !positive(imgW, imgH)) {
      return 1.0;
    }
    return Math.min(viewW / (double) imgW, viewH / (double) imgH);
  }

  static boolean positive(int a, int b) {
    return a > 0 && b > 0;
  }

  static BufferedImage scale(BufferedImage src, double factor) {
    int w = Math.max(1, (int) Math.round(src.getWidth() * factor));
    int h = Math.max(1, (int) Math.round(src.getHeight() * factor));
    int type =
        src.getType() == BufferedImage.TYPE_CUSTOM ? BufferedImage.TYPE_INT_ARGB : src.getType();
    BufferedImage dst = new BufferedImage(w, h, type);
    Graphics2D g = dst.createGraphics();
    try {
      g.setRenderingHint(
          RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      g.drawImage(src, 0, 0, w, h, null);
    } finally {
      g.dispose();
    }
    return dst;
  }
}
