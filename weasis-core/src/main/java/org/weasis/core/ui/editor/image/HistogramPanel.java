/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.editor.image;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import org.weasis.core.ui.editor.image.HistogramData.BinInfo;
import org.weasis.core.ui.editor.image.HistogramData.Channel;

/** Paints histogram bins; click a bar for occurrence count and modality-value range. */
public class HistogramPanel extends JPanel {

  private HistogramData data = new HistogramData();
  private BinInfo lastHit = new BinInfo(-1, 0, 0, 0, 0, 0);

  public HistogramPanel() {
    setOpaque(true);
    setBackground(Color.WHITE);
    setPreferredSize(new Dimension(256, 80));
    addMouseListener(
        new MouseAdapter() {
          @Override
          public void mousePressed(MouseEvent e) {
            lastHit = data.hit(e.getX(), Math.max(1, getWidth()));
          }
        });
  }

  public HistogramData getData() {
    return data;
  }

  public void setData(HistogramData data) {
    this.data = data == null ? new HistogramData() : data;
    repaint();
  }

  public void setChannel(Channel channel) {
    data.setChannel(channel);
  }

  public void update(BufferedImage image) {
    update(image, null);
  }

  public void update(BufferedImage image, Shape roi) {
    data.accumulate(image, roi);
    repaint();
  }

  public BinInfo getLastHit() {
    return lastHit;
  }

  public BinInfo hit(int x) {
    lastHit = data.hit(x, Math.max(1, getWidth()));
    return lastHit;
  }

  public void resetDisplay() {
    data.resetDisplay();
    repaint();
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    int[] display = data.displayBins();
    if (display.length == 0) {
      return;
    }
    int peak = Math.max(1, data.displayPeak());
    int w = Math.max(1, getWidth());
    int h = Math.max(1, getHeight());
    double barW = w / (double) display.length;
    int last = Math.max(1, display.length - 1);
    for (int i = 0; i < display.length; i++) {
      int bh = (int) Math.round(display[i] * (h - 2.0) / peak);
      if (bh <= 0) {
        continue;
      }
      if (data.isShowIntensityColor()) {
        int gray = i * 255 / last;
        g.setColor(new Color(gray, gray, gray));
      } else {
        g.setColor(Color.BLACK);
      }
      int x = (int) Math.floor(i * barW);
      int bw = Math.max(1, (int) Math.ceil(barW));
      g.fillRect(x, h - bh, bw, bh);
    }
  }
}
