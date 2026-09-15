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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import org.weasis.core.api.image.AffineTransformOp;
import org.weasis.core.api.image.OpManager;
import org.weasis.core.api.image.SimpleOpManager;
import org.weasis.core.api.media.data.ImageElement;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.api.media.data.MediaSeries;
import org.weasis.core.ui.editor.image.dockable.MeasureTool;
import org.weasis.core.ui.model.graphic.Graphic;
import org.weasis.core.ui.model.layer.AbstractInfoLayer;

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
  private volatile int frameCount;
  private MediaSeries<? extends MediaElement> series;
  private volatile SynchCineEvent lastCineEvent;
  private volatile String measureTool = MeasureTool.DISTANCE;
  private Graphic drawing;
  protected AbstractInfoLayer infoLayer = new AbstractInfoLayer();
  private volatile SynchView synch = SynchView.STACK;
  private volatile SynchData synchData = new SynchData();
  private volatile SynchManager synchManager;
  private volatile String frameOfReferenceUID = "";
  private volatile boolean freezeParameters;
  private volatile boolean freezeImage;
  private volatile String lossyLabel = "";
  private volatile String geometryWarning = "";

  /** Prefs &gt; Monitors spatial calibration (MX-07). */
  private volatile double monitorCalibrationMmPerPixel;

  /** Manual Calibration, session, image or series (MX-07). */
  private volatile double sessionManualCalibrationMmPerPixel;

  /** Modality LUT (Rescale Slope/Intercept) for histogram X axis. */
  private volatile double modalityLutSlope = 1.0;

  private volatile double modalityLutIntercept;

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
    setFocusable(true);
    addKeyListener(
        new KeyAdapter() {
          @Override
          public void keyPressed(KeyEvent e) {
            eventManager.keyPressed(e);
          }
        });
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

  public int getFrameCount() {
    if (series != null && series.size() > 0) {
      return series.size();
    }
    return frameCount > 0 ? frameCount : 1;
  }

  public void setFrameCount(int frameCount) {
    this.frameCount = Math.max(0, frameCount);
  }

  public MediaSeries<? extends MediaElement> getSeries() {
    return series;
  }

  public void setSeries(MediaSeries<? extends MediaElement> series) {
    this.series = series;
  }

  public SynchCineEvent lastCineEvent() {
    return lastCineEvent;
  }

  public String getMeasureTool() {
    return measureTool;
  }

  public void setMeasureTool(String measureTool) {
    this.measureTool =
        measureTool == null || measureTool.isBlank() ? MeasureTool.DISTANCE : measureTool;
  }

  public Graphic getDrawing() {
    return drawing;
  }

  public void setDrawing(Graphic drawing) {
    this.drawing = drawing;
  }

  public AbstractInfoLayer getInfoLayer() {
    return infoLayer;
  }

  public void setInfoLayer(AbstractInfoLayer infoLayer) {
    this.infoLayer = infoLayer == null ? new AbstractInfoLayer() : infoLayer;
  }

  public void cycleAnnotations() {
    infoLayer.cycle();
    repaint();
  }

  public void setFrameIndex(int frameIndex) {
    setFrameIndex(frameIndex, true);
  }

  public void setFrameIndex(int frameIndex, boolean propagate) {
    this.frameIndex = Math.max(0, frameIndex);
    applyFramePixels();
    lastCineEvent = new SynchCineEvent(this, this.frameIndex);
    if (propagate && synchManager != null && synch != SynchView.NONE) {
      synchManager.onFrame(this);
    }
  }

  void applyFramePixels() {
    if (series == null) {
      return;
    }
    List<? extends MediaElement> medias = series.getMedias();
    if (frameIndex < 0 || frameIndex >= medias.size()) {
      return;
    }
    MediaElement media = medias.get(frameIndex);
    if (media instanceof ImageElement image && image.getImage() != null) {
      setSourceImage(image.getImage());
    }
  }

  public SynchView getSynch() {
    return synch;
  }

  public void setSynch(SynchView synch) {
    this.synch = synch == null ? SynchView.NONE : synch;
    synchData.setMode(SynchData.Mode.fromView(this.synch));
  }

  public SynchData getSynchData() {
    return synchData;
  }

  public void setSynchData(SynchData synchData) {
    this.synchData = synchData == null ? new SynchData() : synchData;
    this.synch = this.synchData.getMode().toView();
  }

  public SynchManager getSynchManager() {
    return synchManager;
  }

  public void setSynchManager(SynchManager synchManager) {
    this.synchManager = synchManager;
  }

  public String getFrameOfReferenceUID() {
    return frameOfReferenceUID;
  }

  public void setFrameOfReferenceUID(String frameOfReferenceUID) {
    this.frameOfReferenceUID = frameOfReferenceUID == null ? "" : frameOfReferenceUID;
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

  public double getModalityLutSlope() {
    return modalityLutSlope;
  }

  public double getModalityLutIntercept() {
    return modalityLutIntercept;
  }

  public void setModalityLut(double slope, double intercept) {
    this.modalityLutSlope = slope == 0 ? 1.0 : slope;
    this.modalityLutIntercept = intercept;
  }

  /** Key image (K). DICOM {@code View2d} toggles the SOP in {@code KOManager}. */
  public boolean toggleKeyImage() {
    return false;
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

  public void paintView(Graphics2D g, boolean overlays) {
    if (g == null) {
      return;
    }
    g.setColor(Color.BLACK);
    g.fillRect(0, 0, Math.max(1, getWidth()), Math.max(1, getHeight()));
    if (source != null) {
      Graphics2D g2 = (Graphics2D) g.create();
      try {
        g2.setRenderingHint(
            RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        int w = Math.max(1, getWidth());
        int h = Math.max(1, getHeight());
        double scale = resolvedScale(w, h);
        AffineTransform tx = new AffineTransform();
        tx.translate(w / 2.0 + panX, h / 2.0 + panY);
        tx.rotate(Math.toRadians(rotation));
        tx.scale(scale, scale);
        tx.translate(-source.getWidth() / 2.0, -source.getHeight() / 2.0);
        g2.drawImage(source, tx, this);
      } finally {
        g2.dispose();
      }
    }
    if (overlays) {
      paintDecorations(g);
    }
  }

  protected void paintDecorations(Graphics2D g) {
    g.setColor(Color.YELLOW);
    int y = 16;
    if (!lossyLabel.isBlank()) {
      g.drawString(lossyLabel, 8, y);
      y += 14;
    }
    if (!geometryWarning.isBlank()) {
      g.drawString(geometryWarning, 8, y);
    }
    for (Graphic graphic : graphics) {
      if (graphic.getShape() == null) {
        continue;
      }
      g.setPaint(graphic.getColorPaint() == null ? Color.YELLOW : graphic.getColorPaint());
      g.draw(graphic.getShape());
    }
  }
}
