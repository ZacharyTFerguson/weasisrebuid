/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.base.viewer2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.weasis.core.ui.model.layer.AbstractInfoLayer;

/** Non-DICOM overlay. Space/I cycles full / minimal / hidden. */
public class InfoLayer extends AbstractInfoLayer {

  public String overlayText(int width, int height, double zoom) {
    if (!isVisible()) {
      return "";
    }
    if (isMinimal()) {
      return width + "x" + height;
    }
    return width + "x" + height + " zoom=" + zoom;
  }

  public String overlayText(View2d view) {
    if (view == null) {
      return "";
    }
    BufferedImage src = view.getSourceImage();
    int w = src == null ? 0 : src.getWidth();
    int h = src == null ? 0 : src.getHeight();
    return overlayText(w, h, view.getZoom());
  }

  public void paint(Graphics2D g, View2d view) {
    if (!isVisible() || g == null || view == null) {
      return;
    }
    String line = overlayText(view);
    if (line.isBlank()) {
      return;
    }
    g.setColor(Color.YELLOW);
    g.drawString(line, 8, isMinimal() ? 16 : Math.max(16, view.getHeight() - 12));
  }
}
