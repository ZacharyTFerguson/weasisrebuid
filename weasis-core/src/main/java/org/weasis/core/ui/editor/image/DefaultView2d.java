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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import org.weasis.core.api.image.AffineTransformOp;
import org.weasis.core.api.image.OpManager;
import org.weasis.core.api.image.SimpleOpManager;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.ui.model.graphic.Graphic;

/**
 * Shared 2D canvas. Downstream DICOM {@code View2d} binds pixels; affine (zoom/rotation) is last.
 */
public class DefaultView2d<E extends MediaElement> extends JPanel {

  public static final double ZOOM_BEST_FIT = AffineTransformOp.ZOOM_BEST_FIT;
  public static final double ZOOM_REAL_SIZE = AffineTransformOp.ZOOM_REAL_SIZE;

  private final SimpleOpManager displayOp = SimpleOpManager.view2dChain();
  private final MouseActions mouseActions = new MouseActions();
  private final List<Graphic> graphics = new ArrayList<>();
  private final ImageViewerEventManager eventManager;

  private BufferedImage source;
  private volatile double zoom = ZOOM_BEST_FIT;
  private volatile double panX;
  private volatile double panY;
  private volatile double rotation;
  private volatile int frameIndex;
  private volatile SynchView synch = SynchView.STACK;
  private volatile boolean freezeParameters;
  private volatile boolean freezeImage;
  private volatile String lossyLabel = "";
  private volatile String geometryWarning = "";

  /** Prefs &gt; Monitors spatial calibration (MX-07). */
  private volatile double monitorCalibrationMmPerPixel;

  /** Manual Calibration, session, image or series (MX-07). */
  private volatile double sessionManualCalibrationMmPerPixel;

  public DefaultView2d() {
    setBackground(Color.BLACK);
    setOpaque(true);
    this.eventManager = createEventManager();
    MouseAdapter adapter =
        new MouseAdapter() {
          @Override
          public void mousePressed(MouseEvent e) {
            eventManager.mousePressed(e);
          }

          @Override
          public void mouseDragged(MouseEvent e) {
            eventManager.mouseDragged(e);
          }

          @Override
          public void mouseReleased(MouseEvent e) {
            eventManager.mouseReleased(e);
          }

          @Override
          public void mouseWheelMoved(MouseWheelEvent e) {
            eventManager.mouseWheelMoved(e);
          }
        };
    addMouseListener(adapter);
    addMouseMotionListener(adapter);
    addMouseWheelListener(adapter);
  }

  public OpManager getDisplayOpManager() {
    return displayOp;
  }

  public MouseActions getMouseActions() {
    return mouseActions;
  }

  public ImageViewerEventManager getEventManager() {
    return eventManager;
  }

  protected ImageViewerEventManager createEventManager() {
    return new ImageViewerEventManager(this);
  }

  public void setSourceImage(BufferedImage source) {
    this.source = source;
    displayOp.setFirstNode(source);
    displayOp.setParamValue("op.affine", AffineTransformOp.P_ZOOM, zoom);
    repaint();
  }

  public BufferedImage getSourceImage() {
    return source;
  }

  public double getZoom() {
    return zoom;
  }

  public void setZoom(double zoom) {
    this.zoom = zoom;
    displayOp.setParamValue("op.affine", AffineTransformOp.P_ZOOM, zoom);
    if (!freezeParameters) {
      repaint();
    }
  }

  /** Image pixel coordinates from a view (component) point using the current affine. */
  public Point2D.Double viewToImage(double viewX, double viewY) {
    int viewW = Math.max(1, getWidth());
    int viewH = Math.max(1, getHeight());
    AffineTransform toView = imageToViewTransform(viewW, viewH);
    try {
      AffineTransform toImage = toView.createInverse();
      Point2D.Double p = new Point2D.Double(viewX, viewY);
      toImage.transform(p, p);
      return p;
    } catch (NoninvertibleTransformException e) {
      return new Point2D.Double(viewX, viewY);
    }
  }

  AffineTransform imageToViewTransform(int viewW, int viewH) {
    AffineTransform tx = new AffineTransform();
    if (source == null) {
      return tx;
    }
    double scale = resolvedScale(viewW, viewH);
    tx.translate(viewW / 2.0 + panX, viewH / 2.0 + panY);
    tx.rotate(Math.toRadians(rotation));
    tx.scale(scale, scale);
    tx.translate(-source.getWidth() / 2.0, -source.getHeight() / 2.0);
    return tx;
  }

  public double resolvedScale(int viewW, int viewH) {
    if (source == null) {
      return 1.0;
    }
    if (zoom == ZOOM_BEST_FIT) {
      if (viewW <= 0 || viewH <= 0) {
        return 1.0;
      }
      double sx = viewW / (double) source.getWidth();
      double sy = viewH / (double) source.getHeight();
      return Math.min(sx, sy);
    }
    if (zoom == ZOOM_REAL_SIZE) {
      return 1.0;
    }
    return zoom <= 0 ? 1.0 : zoom;
  }

  public void increaseZoom(double steps) {
    double current = resolvedScale(Math.max(1, getWidth()), Math.max(1, getHeight()));
    setZoom(Math.max(0.01, current * Math.pow(1.1, steps)));
  }

  public double getPanX() {
    return panX;
  }

  public double getPanY() {
    return panY;
  }

  public void setPan(double x, double y) {
    this.panX = x;
    this.panY = y;
    displayOp.setParamValue("op.affine", AffineTransformOp.P_PAN_X, x);
    displayOp.setParamValue("op.affine", AffineTransformOp.P_PAN_Y, y);
    repaint();
  }

  public double getRotation() {
    return rotation;
  }

  public void setRotation(double rotation) {
    this.rotation = rotation;
    displayOp.setParamValue("op.affine", AffineTransformOp.P_ROTATION, rotation);
    repaint();
  }

  public int getFrameIndex() {
    return frameIndex;
  }

  public void setFrameIndex(int frameIndex) {
    this.frameIndex = Math.max(0, frameIndex);
  }

  public SynchView getSynch() {
    return synch;
  }

  public void setSynch(SynchView synch) {
    this.synch = synch == null ? SynchView.NONE : synch;
  }

  public boolean isFreezeParameters() {
    return freezeParameters;
  }

  public void setFreezeParameters(boolean freezeParameters) {
    this.freezeParameters = freezeParameters;
  }

  public boolean isFreezeImage() {
    return freezeImage;
  }

  public void setFreezeImage(boolean freezeImage) {
    this.freezeImage = freezeImage;
  }

  public String getLossyLabel() {
    return lossyLabel;
  }

  public void setLossyLabel(String lossyLabel) {
    this.lossyLabel = lossyLabel == null ? "" : lossyLabel;
    repaint();
  }

  public String getGeometryWarning() {
    return geometryWarning;
  }

  public void setGeometryWarning(String geometryWarning) {
    this.geometryWarning = geometryWarning == null ? "" : geometryWarning;
  }

  public double getMonitorCalibrationMmPerPixel() {
    return monitorCalibrationMmPerPixel;
  }

  public void setMonitorCalibrationMmPerPixel(double monitorCalibrationMmPerPixel) {
    this.monitorCalibrationMmPerPixel = monitorCalibrationMmPerPixel;
  }

  public double getSessionManualCalibrationMmPerPixel() {
    return sessionManualCalibrationMmPerPixel;
  }

  public void setSessionManualCalibrationMmPerPixel(double sessionManualCalibrationMmPerPixel) {
    this.sessionManualCalibrationMmPerPixel = sessionManualCalibrationMmPerPixel;
  }

  public List<Graphic> getGraphicList() {
    return graphics;
  }

  public void addGraphic(Graphic graphic) {
    if (graphic != null) {
      graphics.add(graphic);
      repaint();
    }
  }

  public void resetWinLevelDefaults() {
    // subclass applies DICOM window/level
  }

  public void resetView(String what) {
    if (what == null || "-a".equals(what) || "all".equalsIgnoreCase(what)) {
      setZoom(ZOOM_BEST_FIT);
      setPan(0, 0);
      setRotation(0);
      resetWinLevelDefaults();
      return;
    }
    switch (what) {
      case "winLevel" -> resetWinLevelDefaults();
      case "zoom" -> setZoom(ZOOM_BEST_FIT);
      case "pan" -> setPan(0, 0);
      case "rotation" -> setRotation(0);
      default -> {
        // ignore unknown
      }
    }
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (source == null || freezeImage && source != null) {
      paintDecorations((Graphics2D) g);
      if (source == null) {
        return;
      }
    }
    Graphics2D g2 = (Graphics2D) g.create();
    try {
      g2.setRenderingHint(
          RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      double scale = resolvedScale(getWidth(), getHeight());
      AffineTransform tx = new AffineTransform();
      tx.translate(getWidth() / 2.0 + panX, getHeight() / 2.0 + panY);
      tx.rotate(Math.toRadians(rotation));
      tx.scale(scale, scale);
      tx.translate(-source.getWidth() / 2.0, -source.getHeight() / 2.0);
      g2.drawImage(source, tx, this);
    } finally {
      g2.dispose();
    }
    paintDecorations((Graphics2D) g);
  }

  void paintDecorations(Graphics2D g) {
    paintGraphicOverlays(g);
    g.setColor(Color.YELLOW);
    int y = 16;
    if (!lossyLabel.isBlank()) {
      g.drawString(lossyLabel, 8, y);
      y += 14;
    }
    if (!geometryWarning.isBlank()) {
      g.drawString(geometryWarning, 8, y);
    }
  }

  void paintGraphicOverlays(Graphics2D g) {
    if (graphics.isEmpty() || source == null) {
      return;
    }
    Graphics2D g2 = (Graphics2D) g.create();
    try {
      g2.transform(imageToViewTransform(Math.max(1, getWidth()), Math.max(1, getHeight())));
      for (Graphic graphic : graphics) {
        paintGraphic(g2, graphic);
      }
    } finally {
      g2.dispose();
    }
  }

  private void paintGraphic(Graphics2D g, Graphic graphic) {
    Shape shape = graphic.getShape();
    if (shape != null) {
      Paint paint = graphic.getColorPaint();
      g.setPaint(paint == null ? Color.YELLOW : paint);
      float thickness = graphic.getLineThickness() == null ? 1.0f : graphic.getLineThickness();
      Stroke stroke = new BasicStroke(thickness);
      g.setStroke(stroke);
      g.draw(shape);
    }
    if (Boolean.TRUE.equals(graphic.getLabelVisible())) {
      String[] labels = graphic.getLabel();
      if (labels != null && labels.length > 0 && labels[0] != null && !labels[0].isBlank()) {
        g.setPaint(Color.YELLOW);
        Point2D.Double anchor = graphicLabelAnchor(graphic);
        if (anchor != null) {
          g.drawString(labels[0], (float) anchor.getX() + 4f, (float) anchor.getY() - 4f);
        }
      }
    }
  }

  private static Point2D.Double graphicLabelAnchor(Graphic graphic) {
    if (graphic.getPtsNumber() == null || graphic.getPtsNumber() <= 0) {
      return null;
    }
    return graphic.getPts().getFirst();
  }
}
