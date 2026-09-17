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
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import org.weasis.core.ui.model.utils.bean.PanPoint;

/**
 * Overview navigator for the selected 2D view. Click/drag places that image point at the view
 * center (shortcut T uses the same pan action on the canvas).
 */
public class Panner extends JPanel {

  private DefaultView2d<?> view;
  private PannerListener listener;
  private PanPoint lastPan;

  public Panner() {
    setOpaque(true);
    setBackground(Color.DARK_GRAY);
    setPreferredSize(new Dimension(128, 128));
    MouseAdapter mouse =
        new MouseAdapter() {
          @Override
          public void mousePressed(MouseEvent e) {
            panAt(e.getX(), e.getY());
          }

          @Override
          public void mouseDragged(MouseEvent e) {
            panAt(e.getX(), e.getY());
          }
        };
    addMouseListener(mouse);
    addMouseMotionListener(mouse);
  }

  public void bind(DefaultView2d<?> view) {
    this.view = view;
    if (view != null) {
      view.setPannerListener(
          (v, x, y) -> {
            if (listener != null) {
              listener.panChanged(v, x, y);
            }
            repaint();
          });
    }
    repaint();
  }

  public DefaultView2d<?> boundView() {
    return view;
  }

  public void setPannerListener(PannerListener listener) {
    this.listener = listener;
  }

  public PannerListener getPannerListener() {
    return listener;
  }

  public PanPoint lastPan() {
    return lastPan;
  }

  public void panAt(int x, int y) {
    if (view == null || view.getSourceImage() == null) {
      return;
    }
    BufferedImage src = view.getSourceImage();
    int w = Math.max(1, getWidth());
    int h = Math.max(1, getHeight());
    double imgX = x * src.getWidth() / (double) w;
    double imgY = y * src.getHeight() / (double) h;
    lastPan = new PanPoint(PanPoint.State.CENTER, imgX, imgY);
    view.centerOnImage(imgX, imgY);
    if (listener != null) {
      listener.panChanged(view, view.getPanX(), view.getPanY());
    }
    repaint();
  }

  public Rectangle2D viewportOnPanner() {
    if (view == null || view.getSourceImage() == null) {
      return new Rectangle2D.Double();
    }
    BufferedImage src = view.getSourceImage();
    int pw = Math.max(1, getWidth());
    int ph = Math.max(1, getHeight());
    Point2D.Double a = view.viewToImage(0, 0);
    Point2D.Double b =
        view.viewToImage(Math.max(1, view.getWidth()), Math.max(1, view.getHeight()));
    double x1 = a.x * pw / src.getWidth();
    double y1 = a.y * ph / src.getHeight();
    double x2 = b.x * pw / src.getWidth();
    double y2 = b.y * ph / src.getHeight();
    return new Rectangle2D.Double(
        Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1));
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (view == null || view.getSourceImage() == null) {
      return;
    }
    Graphics2D g2 = (Graphics2D) g.create();
    try {
      g2.drawImage(view.getSourceImage(), 0, 0, getWidth(), getHeight(), this);
      g2.setColor(Color.YELLOW);
      g2.draw(viewportOnPanner());
    } finally {
      g2.dispose();
    }
  }
}
