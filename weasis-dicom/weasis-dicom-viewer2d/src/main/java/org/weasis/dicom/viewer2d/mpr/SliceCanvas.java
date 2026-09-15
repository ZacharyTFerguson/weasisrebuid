/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.mpr;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import org.weasis.core.ui.editor.image.Canvas;

/** Paints one reconstructed MPR plane from {@link VolImageIO}. */
public class SliceCanvas extends JPanel implements Canvas {

  private BuildContext context = new BuildContext();
  private BufferedImage source;
  private double zoom = 1.0;
  private double panX;
  private double panY;
  private double rotation;

  public SliceCanvas() {
    setBackground(Color.BLACK);
    setOpaque(true);
  }

  public SliceCanvas(BuildContext context) {
    this();
    setBuildContext(context);
  }

  public BuildContext getBuildContext() {
    return context;
  }

  public void setBuildContext(BuildContext context) {
    this.context = context == null ? new BuildContext() : context;
    rebuild();
  }

  public VolImageIO getVolImageIO() {
    return context.toVolImageIO();
  }

  public BufferedImage rebuild() {
    BufferedImage image = getVolImageIO().getImage();
    setSourceImage(image);
    return image;
  }

  @Override
  public BufferedImage getSourceImage() {
    return source;
  }

  @Override
  public void setSourceImage(BufferedImage source) {
    this.source = source;
    repaint();
  }

  @Override
  public double getZoom() {
    return zoom;
  }

  @Override
  public void setZoom(double zoom) {
    this.zoom = zoom;
    repaint();
  }

  @Override
  public double getPanX() {
    return panX;
  }

  @Override
  public double getPanY() {
    return panY;
  }

  @Override
  public void setPan(double x, double y) {
    this.panX = x;
    this.panY = y;
    repaint();
  }

  @Override
  public double getRotation() {
    return rotation;
  }

  @Override
  public void setRotation(double rotation) {
    double wrapped = rotation % 360.0;
    if (wrapped < 0) {
      wrapped += 360.0;
    }
    this.rotation = wrapped;
    repaint();
  }

  @Override
  public Point2D.Double viewToImage(double viewX, double viewY) {
    if (source == null) {
      return new Point2D.Double(viewX, viewY);
    }
    try {
      Point2D.Double out = new Point2D.Double();
      getAffineTransform().inverseTransform(new Point2D.Double(viewX, viewY), out);
      return out;
    } catch (Exception e) {
      return new Point2D.Double(viewX, viewY);
    }
  }

  @Override
  public Point2D.Double imageToView(double imageX, double imageY) {
    if (source == null) {
      return new Point2D.Double(imageX, imageY);
    }
    Point2D.Double out = new Point2D.Double();
    getAffineTransform().transform(new Point2D.Double(imageX, imageY), out);
    return out;
  }

  @Override
  public AffineTransform getAffineTransform() {
    int w = Math.max(1, getWidth());
    int h = Math.max(1, getHeight());
    double scale = zoom <= 0 ? 1.0 : zoom;
    AffineTransform tx = new AffineTransform();
    tx.translate(w / 2.0 + panX, h / 2.0 + panY);
    tx.rotate(Math.toRadians(rotation));
    tx.scale(scale, scale);
    if (source != null) {
      tx.translate(-source.getWidth() / 2.0, -source.getHeight() / 2.0);
    }
    return tx;
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g.create();
    try {
      g2.setColor(getBackground());
      g2.fillRect(0, 0, Math.max(1, getWidth()), Math.max(1, getHeight()));
      if (source == null) {
        return;
      }
      g2.setRenderingHint(
          RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
      g2.drawImage(source, getAffineTransform(), this);
    } finally {
      g2.dispose();
    }
  }
}
