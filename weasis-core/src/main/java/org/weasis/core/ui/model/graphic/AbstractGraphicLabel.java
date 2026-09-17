/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.graphic;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public abstract class AbstractGraphicLabel implements GraphicLabel {

  private String[] labels = new String[0];
  private double offsetX;
  private double offsetY;

  @Override
  public String[] getLabels() {
    return labels.clone();
  }

  @Override
  public void setLabels(String[] labels) {
    this.labels = labels == null ? new String[0] : labels.clone();
  }

  @Override
  public double getOffsetX() {
    return offsetX;
  }

  @Override
  public double getOffsetY() {
    return offsetY;
  }

  public void setOffset(double x, double y) {
    this.offsetX = x;
    this.offsetY = y;
  }

  public void paint(Graphics2D g, double anchorX, double anchorY) {
    if (g == null) {
      return;
    }
    paintLines(g, g.getFontMetrics(), anchorX, anchorY);
  }

  void paintLines(Graphics2D g, FontMetrics fm, double anchorX, double anchorY) {
    float x = (float) (anchorX + offsetX);
    float y = (float) (anchorY + offsetY + fm.getAscent());
    for (String line : getLabels()) {
      if (line != null) {
        g.drawString(line, x, y);
      }
      y += fm.getHeight();
    }
  }

  public Rectangle2D bounds(Graphics2D g, double anchorX, double anchorY) {
    if (g == null) {
      return new Rectangle2D.Double();
    }
    return labelBox(g.getFontMetrics(), anchorX, anchorY);
  }

  Rectangle2D labelBox(FontMetrics fm, double anchorX, double anchorY) {
    String[] lines = getLabels();
    if (lines.length == 0) {
      return new Rectangle2D.Double(anchorX + offsetX, anchorY + offsetY, 0, 0);
    }
    return new Rectangle2D.Double(
        anchorX + offsetX,
        anchorY + offsetY,
        maxLineWidth(fm, lines),
        fm.getHeight() * lines.length);
  }

  static int maxLineWidth(FontMetrics fm, String[] lines) {
    int width = 0;
    for (String line : lines) {
      width = Math.max(width, fm.stringWidth(line == null ? "" : line));
    }
    return width;
  }

  public boolean contains(double px, double py, Graphics2D g, double anchorX, double anchorY) {
    return bounds(g, anchorX, anchorY).contains(px, py);
  }
}
