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
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyBoundsAdapter;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.weasis.core.api.gui.util.ActionW;
import org.weasis.core.api.gui.util.SliderCineListener;
import org.weasis.core.api.image.AffineTransformOp;
import org.weasis.core.api.image.OpManager;
import org.weasis.core.api.image.PseudoColorOp;
import org.weasis.core.api.image.SimpleOpManager;
import org.weasis.core.api.image.op.ByteLutCollection;
import org.weasis.core.api.media.data.ImageElement;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.api.media.data.MediaSeries;
import org.weasis.core.ui.editor.image.dockable.MeasureTool;
import org.weasis.core.ui.model.graphic.Graphic;
import org.weasis.core.ui.model.graphic.GraphicArea;
import org.weasis.core.ui.model.graphic.GraphicSelectionListener;
import org.weasis.core.ui.model.layer.AbstractInfoLayer;
import org.weasis.core.ui.model.layer.GraphicLayer;
import org.weasis.core.ui.model.layer.GraphicModelChangeListener;
import org.weasis.core.ui.model.layer.Layer;
import org.weasis.core.ui.model.layer.LayerItem;
import org.weasis.core.ui.model.layer.LayerType;
import org.weasis.core.ui.model.layer.imp.DefaultLayer;
import org.weasis.core.ui.model.layer.imp.RenderedImageLayer;
import org.weasis.core.ui.model.utils.ImageStatistics;
import org.weasis.core.ui.util.ImagePrint;
import org.weasis.core.ui.util.PrintOptions;

/**
 * Shared 2D canvas. Downstream DICOM {@code View2d} binds pixels; affine (zoom/rotation) is last.
 */
public class DefaultView2d<E extends MediaElement> extends JPanel implements ViewCanvas {

  public static final double ZOOM_BEST_FIT = AffineTransformOp.ZOOM_BEST_FIT;
  public static final double ZOOM_REAL_SIZE = AffineTransformOp.ZOOM_REAL_SIZE;
  static final List<DefaultView2d<?>> LIVE = new CopyOnWriteArrayList<>();
  static volatile int boundsGen;
  static final ComponentAdapter BOUNDS =
      new ComponentAdapter() {
        @Override
        public void componentResized(ComponentEvent e) {
          dropBoundsCache();
        }

        @Override
        public void componentMoved(ComponentEvent e) {
          dropBoundsCache();
        }
      };
  static final HierarchyBoundsAdapter ANCESTOR =
      new HierarchyBoundsAdapter() {
        @Override
        public void ancestorResized(HierarchyEvent e) {
          dropBoundsCache();
        }

        @Override
        public void ancestorMoved(HierarchyEvent e) {
          dropBoundsCache();
        }
      };
  static final HierarchyListener TREE = e -> dropBoundsCache();

  private final SimpleOpManager displayOp = SimpleOpManager.view2dChain();
  private final MouseActions mouseActions = new MouseActions();
  private final List<Graphic> graphics = new ArrayList<>();
  private final List<GraphicSelectionListener> selectionListeners = new ArrayList<>();
  private final List<GraphicModelChangeListener> modelListeners = new ArrayList<>();
  private final ImageViewerEventManager eventManager;
  private final List<ViewButton> viewButtons = new ArrayList<>();
  private final PlayViewButton playButton = new PlayViewButton();
  private final SequenceHandler sequenceHandler = new SequenceHandler();
  private final FocusHandler focusHandler = new FocusHandler();
  private final ShowPopup showPopup = new ShowPopup();
  private final PropertyChangeHandler propertyChangeHandler = new PropertyChangeHandler();
  private final ViewProgress viewProgress = new ViewProgress();
  private final FrameOfReferenceColor frameOfReferenceColor = new FrameOfReferenceColor();
  private DisplayByteLut displayByteLut = new DisplayByteLut(ByteLutCollection.GRAY);

  private BufferedImage source;
  private BufferedImage flipBlit;
  private BufferedImage flipBlitSrc;
  private volatile double zoom = ZOOM_BEST_FIT;
  private volatile double panX;
  private volatile double panY;
  private volatile double rotation;
  private volatile boolean flip;
  private volatile int frameIndex;
  private volatile int frameCount;
  private MediaSeries<? extends MediaElement> series;
  private volatile SynchCineEvent lastCineEvent;
  private volatile String measureTool = MeasureTool.DISTANCE;
  private MeasureToolBar measureToolBar;
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
  private final EnumMap<LayerType, Layer> layers = new EnumMap<>(LayerType.class);
  private volatile int crosshairX;
  private volatile int crosshairY;
  private volatile boolean crosshairSet;
  private PixelInfo pixelInfo = new PixelInfo();
  private CrosshairListener crosshairListener;
  private PannerListener pannerListener;
  private final ContextMenuHandler contextMenuHandler = new ContextMenuHandler();
  private SliderCineListener cine;
  private ImagePrint lastPrint;
  private final List<MediaSeries<? extends MediaElement>> seriesStack = new ArrayList<>();
  private final List<Integer> studyOfSeries = new ArrayList<>();
  private final List<Integer> patientOfSeries = new ArrayList<>();
  private int seriesIndex;
  private boolean fullScreen;
  private boolean segmentationsVisible = true;

  public DefaultView2d() {
    setBackground(Color.BLACK);
    setOpaque(true);
    this.eventManager = createEventManager();
    addPropertyChangeListener(propertyChangeHandler);
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
    initLayers();
    viewButtons.add(playButton);
    LIVE.add(this);
  }

  @Override
  public void addNotify() {
    super.addNotify();
    if (!LIVE.contains(this)) {
      LIVE.add(this);
    }
    armBounds();
    dropBoundsCache();
  }

  @Override
  public void removeNotify() {
    LIVE.remove(this);
    dropBoundsCache();
    super.removeNotify();
  }

  void armBounds() {
    if (Boolean.TRUE.equals(getClientProperty("measure.bounds"))) {
      return;
    }
    putClientProperty("measure.bounds", Boolean.TRUE);
    addComponentListener(BOUNDS);
    addHierarchyBoundsListener(ANCESTOR);
    addHierarchyListener(TREE);
  }

  /** Dock / resize invalidates any prior screen boxes. Hit-tests always reread live bounds. */
  public static void dropBoundsCache() {
    boundsGen++;
  }

  /** Smallest showing canvas under {@code screen}. Image raster wins over an empty sibling. */
  public static DefaultView2d<?> atScreen(Point screen) {
    return atScreen(screen, LIVE);
  }

  public static DefaultView2d<?> atScreen(
      Point screen, Iterable<? extends DefaultView2d<?>> views) {
    DefaultView2d<?> raster = best(views, screen, true);
    return raster != null ? raster : best(views, screen, false);
  }

  static DefaultView2d<?> best(
      Iterable<? extends DefaultView2d<?>> views, Point screen, boolean raster) {
    DefaultView2d<?> hit = null;
    long area = Long.MAX_VALUE;
    for (DefaultView2d<?> v : views) {
      long a = score(v, screen, raster);
      if (a > 0 && a < area) {
        area = a;
        hit = v;
      }
    }
    return hit;
  }

  static long score(DefaultView2d<?> v, Point screen, boolean raster) {
    Rectangle box = liveScreenBox(v);
    if (!boxContains(box, screen)) {
      return 0;
    }
    if (raster != rasterHit(v, box, screen)) {
      return 0;
    }
    return (long) box.width * box.height;
  }

  static boolean rasterHit(DefaultView2d<?> v, Rectangle box, Point screen) {
    BufferedImage src = v.getSourceImage();
    if (src == null || box == null) {
      return false;
    }
    Point2D.Double img = v.viewToImage(screen.x - box.x, screen.y - box.y);
    return inRaster(img, src);
  }

  static boolean inRaster(Point2D.Double img, BufferedImage src) {
    return img.x >= 0 && img.y >= 0 && img.x < src.getWidth() && img.y < src.getHeight();
  }

  static boolean boxContains(Rectangle box, Point screen) {
    return box != null && screen != null && box.contains(screen);
  }

  static Rectangle screenBox(DefaultView2d<?> c) {
    return liveScreenBox(c);
  }

  public static Rectangle liveScreenBox(Component c) {
    if (c == null || !c.isShowing()) {
      return null;
    }
    syncLayout(c);
    try {
      return new Rectangle(c.getLocationOnScreen(), c.getSize());
    } catch (IllegalComponentStateException e) {
      return null;
    }
  }

  static void syncLayout(Component c) {
    Component root = SwingUtilities.getRoot(c);
    if (root instanceof Container box && !box.isValid()) {
      box.validate();
    }
  }

  private void initLayers() {
    for (LayerType type : LayerType.values()) {
      layers.put(type, layerFor(type));
    }
  }

  static Layer layerFor(LayerType type) {
    if (type == LayerType.MEASURE || type == LayerType.DRAW) {
      return new GraphicLayer(type);
    }
    if (type == LayerType.IMAGE) {
      return new RenderedImageLayer();
    }
    return new DefaultLayer(type);
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
    this.flipBlit = null;
    this.flipBlitSrc = null;
    displayOp.setFirstNode(source);
    displayOp.setParamValue("op.affine", AffineTransformOp.P_ZOOM, zoom);
    bindImageLayer(source);
    repaint();
  }

  public RenderedImageLayer getImageLayer() {
    Layer layer = getLayer(LayerType.IMAGE);
    return layer instanceof RenderedImageLayer rendered ? rendered : null;
  }

  void bindImageLayer(BufferedImage source) {
    RenderedImageLayer layer = getImageLayer();
    if (layer != null) {
      layer.setImage(source);
    }
  }

  public BufferedImage getSourceImage() {
    return source;
  }

  public ImageStatistics imageStatistics() {
    ImageRegionStatistics.Stats stats = ImageRegionStatistics.compute(this);
    return new ImageStatistics(
        stats.getSamples(), stats.getMin(), stats.getMax(), stats.getMean(), stats.getStdev());
  }

  public double getZoom() {
    return zoom;
  }

  public void setZoom(double zoom) {
    double old = this.zoom;
    this.zoom = zoom;
    displayOp.setParamValue("op.affine", AffineTransformOp.P_ZOOM, zoom);
    firePropertyChange("zoom", old, this.zoom);
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
    if (pannerListener != null) {
      pannerListener.panChanged(this, panX, panY);
    }
    repaint();
  }

  /**
   * Place an image point at the view center (panner click). Rotation is applied in the same order
   * as {@link #imageTransform(int, int)}.
   */
  public void centerOnImage(double imgX, double imgY) {
    if (source == null) {
      return;
    }
    int w = Math.max(1, getWidth());
    int h = Math.max(1, getHeight());
    double scale = resolvedScale(w, h);
    double dx = (imgX - source.getWidth() / 2.0) * scale;
    double dy = (imgY - source.getHeight() / 2.0) * scale;
    double rad = Math.toRadians(rotation);
    double cos = Math.cos(rad);
    double sin = Math.sin(rad);
    setPan(-(dx * cos - dy * sin), -(dx * sin + dy * cos));
  }

  public double getRotation() {
    return rotation;
  }

  public void setRotation(double rotation) {
    double wrapped = rotation % 360.0;
    if (wrapped < 0) {
      wrapped += 360.0;
    }
    this.rotation = wrapped;
    displayOp.setParamValue("op.affine", AffineTransformOp.P_ROTATION, this.rotation);
    repaint();
  }

  public boolean isFlip() {
    return flip;
  }

  public void setFlip(boolean flip) {
    this.flip = flip;
    repaint();
  }

  public void toggleFlip() {
    setFlip(!flip);
  }

  public void cycleLeftMouseAction() {
    String current = MouseActions.normalize(getMouseActions().getLeft());
    String[] actions = ViewerToolBar.ACTIONS;
    int idx = 0;
    for (int i = 0; i < actions.length; i++) {
      if (MouseActions.normalize(actions[i]).equals(current)) {
        idx = i;
        break;
      }
    }
    getMouseActions().setLeft(actions[(idx + 1) % actions.length]);
  }

  public void setLut(String lut) {
    displayOp.setParamValue(
        "op.pseudocolor",
        PseudoColorOp.P_LUT,
        lut == null || lut.isBlank() ? PseudoColorOp.GRAY : lut);
    onLutChanged();
  }

  public String getLut() {
    Object value = displayOp.getParamValue("op.pseudocolor", PseudoColorOp.P_LUT);
    return value == null ? PseudoColorOp.GRAY : value.toString();
  }

  public void setInverseLut(boolean invert) {
    displayOp.setParamValue("op.pseudocolor", PseudoColorOp.P_INVERT, invert);
    onLutChanged();
  }

  public boolean isInverseLut() {
    Object value = displayOp.getParamValue("op.pseudocolor", PseudoColorOp.P_INVERT);
    return Boolean.TRUE.equals(value);
  }

  /** Subclasses that bake LUT into pixels (DICOM {@code View2d}) re-render here. */
  protected void onLutChanged() {
    if (!freezeParameters) {
      repaint();
    }
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
    for (int i = 0; i < seriesStack.size(); i++) {
      if (seriesStack.get(i) == series) {
        seriesIndex = i;
        break;
      }
    }
  }

  public void setSeriesStack(
      List<? extends MediaSeries<? extends MediaElement>> stack, int[] studies, int[] patients) {
    seriesStack.clear();
    studyOfSeries.clear();
    patientOfSeries.clear();
    if (stack != null) {
      for (int i = 0; i < stack.size(); i++) {
        seriesStack.add(stack.get(i));
        studyOfSeries.add(studies != null && i < studies.length ? studies[i] : 0);
        patientOfSeries.add(patients != null && i < patients.length ? patients[i] : 0);
      }
    }
    seriesIndex = 0;
    if (!seriesStack.isEmpty()) {
      setSeries(seriesStack.get(0));
      setFrameIndex(0);
    }
  }

  public int getSeriesIndex() {
    return seriesIndex;
  }

  public int getStudyIndex() {
    return studyOfSeries.isEmpty()
        ? 0
        : studyOfSeries.get(Math.min(seriesIndex, studyOfSeries.size() - 1));
  }

  public int getPatientIndex() {
    return patientOfSeries.isEmpty()
        ? 0
        : patientOfSeries.get(Math.min(seriesIndex, patientOfSeries.size() - 1));
  }

  public void nextFrame(int delta) {
    int max = Math.max(0, getFrameCount() - 1);
    int next = getFrameIndex() + delta;
    if (next < 0) {
      next = 0;
    }
    if (next > max) {
      next = max;
    }
    setFrameIndex(next);
  }

  public void firstFrame() {
    setFrameIndex(0);
  }

  public void lastFrame() {
    setFrameIndex(Math.max(0, getFrameCount() - 1));
  }

  public void nextSeries(int delta) {
    if (seriesStack.isEmpty()) {
      return;
    }
    int study = getStudyIndex();
    int step = delta < 0 ? -1 : 1;
    int i = seriesIndex + step;
    while (i >= 0 && i < seriesStack.size()) {
      if (studyOfSeries.get(i) == study) {
        showSeries(i);
        return;
      }
      i += step;
    }
  }

  public void firstSeries() {
    selectFirstSeriesFor(studyOfSeries, getStudyIndex());
  }

  public void lastSeries() {
    int study = getStudyIndex();
    for (int i = seriesStack.size() - 1; i >= 0; i--) {
      if (studyOfSeries.get(i) == study) {
        showSeries(i);
        return;
      }
    }
  }

  public void nextStudy(int delta) {
    selectFirstSeriesFor(studyOfSeries, getStudyIndex() + delta);
  }

  public void firstStudy() {
    selectExtreme(studyOfSeries, true);
  }

  public void lastStudy() {
    selectExtreme(studyOfSeries, false);
  }

  public void nextPatient(int delta) {
    selectFirstSeriesFor(patientOfSeries, getPatientIndex() + delta);
  }

  public void firstPatient() {
    selectExtreme(patientOfSeries, true);
  }

  public void lastPatient() {
    selectExtreme(patientOfSeries, false);
  }

  void showSeries(int index) {
    if (seriesStack.isEmpty()) {
      return;
    }
    seriesIndex = Math.max(0, Math.min(seriesStack.size() - 1, index));
    setSeries(seriesStack.get(seriesIndex));
    setFrameIndex(0);
  }

  void selectFirstSeriesFor(List<Integer> groups, int target) {
    for (int i = 0; i < groups.size(); i++) {
      if (groups.get(i) == target) {
        showSeries(i);
        return;
      }
    }
  }

  void selectExtreme(List<Integer> groups, boolean first) {
    if (groups.isEmpty()) {
      return;
    }
    int extreme = groups.get(0);
    for (int g : groups) {
      extreme = first ? Math.min(extreme, g) : Math.max(extreme, g);
    }
    selectFirstSeriesFor(groups, extreme);
  }

  public void toggleFullScreen() {
    fullScreen = !fullScreen;
  }

  public boolean isFullScreen() {
    return fullScreen;
  }

  public void toggleSegmentations() {
    setSegmentationsVisible(!segmentationsVisible);
  }

  @Override
  public boolean isSegmentationsVisible() {
    return segmentationsVisible;
  }

  @Override
  public void setSegmentationsVisible(boolean visible) {
    this.segmentationsVisible = visible;
  }

  public void applyPreset(int index) {
    // DICOM View2d applies VOI LUT presets
  }

  public SynchCineEvent lastCineEvent() {
    return lastCineEvent;
  }

  public String getMeasureTool() {
    return measureTool;
  }

  public String activeMeasureTool() {
    if (measureToolBar != null) {
      return MeasureTool.canonical(measureToolBar.getSelected());
    }
    return MeasureTool.canonical(measureTool);
  }

  public MeasureToolBar getMeasureToolBar() {
    return measureToolBar;
  }

  public void setMeasureToolBar(MeasureToolBar bar) {
    this.measureToolBar = bar;
  }

  public void setMeasureTool(String measureTool) {
    this.measureTool = MeasureTool.canonical(measureTool);
  }

  /** Stop an in-progress D/Y so the next A/G drag constructs a new graphic. */
  public void abandonDrawing() {
    Graphic current = drawing;
    drawing = null;
    if (current != null && ImageViewerEventManager.degenerate(current)) {
      graphics.remove(current);
    }
    repaint();
  }

  public Graphic getDrawing() {
    return drawing;
  }

  public void setDrawing(Graphic drawing) {
    this.drawing = drawing;
    repaint();
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

  public Layer getLayer(LayerType type) {
    return layers.get(type == null ? LayerType.IMAGE : type);
  }

  public boolean isLayerVisible(LayerType type) {
    Layer layer = getLayer(type);
    return layer != null && layer.isVisible();
  }

  public void setLayerVisible(LayerType type, boolean visible) {
    Layer layer = getLayer(type);
    if (layer != null) {
      layer.setVisible(visible);
      if (type == LayerType.ANNOTATION) {
        infoLayer.setVisible(visible);
      }
      repaint();
    }
  }

  public List<LayerItem> displayLayers() {
    List<LayerItem> items = new ArrayList<>();
    for (LayerType type :
        List.of(
            LayerType.IMAGE,
            LayerType.CROSSLINES,
            LayerType.ANNOTATION,
            LayerType.DRAW,
            LayerType.MEASURE)) {
      LayerItem item = new LayerItem(type);
      item.setSelected(isLayerVisible(type));
      items.add(item);
    }
    return items;
  }

  public int getCrosshairX() {
    return crosshairX;
  }

  public int getCrosshairY() {
    return crosshairY;
  }

  public boolean hasCrosshair() {
    return crosshairSet;
  }

  public boolean isCrosshairPainted() {
    return crosshairSet && isLayerVisible(LayerType.CROSSLINES);
  }

  public PixelInfo getPixelInfo() {
    return pixelInfo;
  }

  public void setCrosshairListener(CrosshairListener crosshairListener) {
    this.crosshairListener = crosshairListener;
  }

  public void setPannerListener(PannerListener pannerListener) {
    this.pannerListener = pannerListener;
  }

  public ContextMenuHandler getContextMenuHandler() {
    return contextMenuHandler;
  }

  public ShowPopup getShowPopup() {
    return showPopup;
  }

  public void showContextMenu(int x, int y) {
    showPopup.show(this, x, y);
  }

  public SequenceHandler getSequenceHandler() {
    return sequenceHandler;
  }

  public FocusHandler getFocusHandler() {
    return focusHandler;
  }

  public void selectInFocus() {
    focusHandler.focus(this);
  }

  public PropertyChangeHandler getPropertyChangeHandler() {
    return propertyChangeHandler;
  }

  public ViewProgress getViewProgress() {
    return viewProgress;
  }

  public DisplayByteLut getDisplayByteLut() {
    return displayByteLut;
  }

  public void setDisplayByteLut(DisplayByteLut lut) {
    this.displayByteLut = lut == null ? new DisplayByteLut(ByteLutCollection.GRAY) : lut;
    displayOp.setParamValue("op.pseudocolor", PseudoColorOp.P_LUT, displayByteLut.getName());
    displayOp.setParamValue(
        "op.pseudocolor",
        PseudoColorOp.P_INVERT,
        ByteLutCollection.INVERSE.equals(displayByteLut.getName()));
  }

  public FrameOfReferenceColor getFrameOfReferenceColor() {
    return frameOfReferenceColor;
  }

  public Color colorForFrameOfReference() {
    return frameOfReferenceColor.colorFor(frameOfReferenceUID);
  }

  public BufferedImage exportImage() {
    return new ExportImage().render(this);
  }

  public List<ViewButton> getViewButtons() {
    return List.copyOf(viewButtons);
  }

  public PlayViewButton getPlayButton() {
    return playButton;
  }

  public void addViewButton(ViewButton button) {
    if (button != null) {
      viewButtons.add(button);
    }
  }

  public boolean clickViewButton(int x, int y) {
    for (ViewButton button : viewButtons) {
      if (button.hit(x, y)) {
        button.apply(this);
        return true;
      }
    }
    return false;
  }

  public SliderCineListener cineListener() {
    int max = Math.max(0, getFrameCount() - 1);
    if (cine == null) {
      cine =
          new SliderCineListener(ActionW.CINE, 0, max, frameIndex) {
            @Override
            public void stateChanged(int value) {
              setFrameIndex(value);
            }
          };
    } else {
      cine.getSlider().setMaximum(max);
    }
    return cine;
  }

  public void toggleCine() {
    SliderCineListener listener = cineListener();
    if (listener.isCineRunning()) {
      listener.stop();
    } else {
      listener.start();
    }
    playButton.sync(this);
  }

  public ImagePrint getLastPrint() {
    return lastPrint;
  }

  public ImagePrint requestPrint(PrintOptions options) {
    PrintOptions opts = options == null ? new PrintOptions() : options;
    int w = Math.max(1, getWidth() <= 0 ? (source == null ? 1 : source.getWidth()) : getWidth());
    int h = Math.max(1, getHeight() <= 0 ? (source == null ? 1 : source.getHeight()) : getHeight());
    BufferedImage page = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = page.createGraphics();
    try {
      paintView(g, opts.isShowingAnnotations());
    } finally {
      g.dispose();
    }
    lastPrint = new ImagePrint(page, opts);
    return lastPrint;
  }

  public void setCrosshairFromView(int viewX, int viewY) {
    Point2D.Double img = viewToImage(viewX, viewY);
    setCrosshair((int) Math.round(img.x), (int) Math.round(img.y), true);
  }

  public void setCrosshair(int x, int y) {
    setCrosshair(x, y, true);
  }

  public void setCrosshair(int x, int y, boolean propagate) {
    this.crosshairX = x;
    this.crosshairY = y;
    this.crosshairSet = true;
    this.pixelInfo = PixelInfo.from(source, x, y, modalityLutSlope, modalityLutIntercept);
    if (crosshairListener != null) {
      crosshairListener.crosshairMoved(this, pixelInfo);
    }
    if (propagate && synchManager != null && synch != SynchView.NONE) {
      synchManager.onCrosshair(this);
    }
    repaint();
  }

  public Point2D.Double viewToImage(double viewX, double viewY) {
    if (source == null || getWidth() <= 0 || getHeight() <= 0) {
      return new Point2D.Double(viewX, viewY);
    }
    try {
      Point2D.Double out = new Point2D.Double();
      imageTransform(getWidth(), getHeight())
          .inverseTransform(new Point2D.Double(viewX, viewY), out);
      return out;
    } catch (Exception e) {
      return new Point2D.Double(viewX, viewY);
    }
  }

  public Point2D.Double imageToView(double imageX, double imageY) {
    if (source == null || getWidth() <= 0 || getHeight() <= 0) {
      return new Point2D.Double(imageX, imageY);
    }
    Point2D.Double out = new Point2D.Double();
    imageTransform(getWidth(), getHeight()).transform(new Point2D.Double(imageX, imageY), out);
    return out;
  }

  @Override
  public AffineTransform getAffineTransform() {
    if (source == null) {
      return new AffineTransform();
    }
    return imageTransform(Math.max(1, getWidth()), Math.max(1, getHeight()));
  }

  AffineTransform imageTransform(int w, int h) {
    double scale = resolvedScale(w, h);
    AffineTransform tx = new AffineTransform();
    tx.translate(w / 2.0 + panX, h / 2.0 + panY);
    tx.rotate(Math.toRadians(rotation));
    tx.scale(flip ? -scale : scale, scale);
    tx.translate(-source.getWidth() / 2.0, -source.getHeight() / 2.0);
    return tx;
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
      fireModelChanged();
      repaint();
    }
  }

  public void removeGraphic(Graphic graphic) {
    if (graphics.remove(graphic)) {
      fireModelChanged();
      fireSelection();
      repaint();
    }
  }

  public Graphic graphicAt(double x, double y) {
    for (int i = graphics.size() - 1; i >= 0; i--) {
      Graphic graphic = graphics.get(i);
      Shape shape = viewShape(graphic);
      if (shape == null) {
        continue;
      }
      if (shape.contains(x, y) || shape.intersects(x - 3, y - 3, 6, 6)) {
        return graphic;
      }
    }
    return null;
  }

  /** Graphics live in image space; paint/hit-test use the same affine as the pixels. */
  public Shape viewShape(Graphic graphic) {
    if (graphic == null || graphic.getShape() == null) {
      return null;
    }
    AffineTransform tx = graphicTransform();
    if (tx.isIdentity()) {
      return graphic.getShape();
    }
    return tx.createTransformedShape(graphic.getShape());
  }

  AffineTransform graphicTransform() {
    if (source == null || getWidth() <= 0 || getHeight() <= 0) {
      return new AffineTransform();
    }
    return imageTransform(getWidth(), getHeight());
  }

  public List<Graphic> getSelectedGraphics() {
    List<Graphic> selected = new ArrayList<>();
    for (Graphic graphic : graphics) {
      if (Boolean.TRUE.equals(graphic.getSelected())) {
        selected.add(graphic);
      }
    }
    return selected;
  }

  public void selectGraphic(Graphic graphic, boolean add) {
    if (graphic == null) {
      return;
    }
    if (!add) {
      deselectAllGraphics();
    }
    graphic.setSelected(!add || !Boolean.TRUE.equals(graphic.getSelected()));
    if (!add) {
      graphic.setSelected(true);
    }
    fireSelection();
    fireModelChanged();
    repaint();
  }

  public void selectAllGraphics() {
    for (Graphic graphic : graphics) {
      graphic.setSelected(true);
    }
    fireSelection();
    fireModelChanged();
    repaint();
  }

  public void deselectAllGraphics() {
    for (Graphic graphic : graphics) {
      graphic.setSelected(false);
    }
    fireSelection();
    fireModelChanged();
    repaint();
  }

  public void deleteSelectedGraphics() {
    graphics.removeIf(g -> Boolean.TRUE.equals(g.getSelected()));
    fireSelection();
    fireModelChanged();
    repaint();
  }

  public void selectIntersecting(Graphic area) {
    if (area == null || area.getShape() == null) {
      return;
    }
    Rectangle2D box = area.getShape().getBounds2D();
    for (Graphic graphic : graphics) {
      if (graphic == area || graphic.getShape() == null) {
        continue;
      }
      if (graphic.getShape().intersects(box)) {
        graphic.setSelected(true);
      }
    }
    fireSelection();
    fireModelChanged();
    repaint();
  }

  public void addGraphicSelectionListener(GraphicSelectionListener listener) {
    if (listener != null) {
      selectionListeners.add(listener);
    }
  }

  public void addGraphicModelChangeListener(GraphicModelChangeListener listener) {
    if (listener != null) {
      modelListeners.add(listener);
    }
  }

  void fireSelection() {
    List<Graphic> selected = getSelectedGraphics();
    for (GraphicSelectionListener listener : selectionListeners) {
      listener.handle(selected);
    }
  }

  void fireModelChanged() {
    for (GraphicModelChangeListener listener : modelListeners) {
      listener.handle();
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
      setFlip(false);
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
    paintView((Graphics2D) g, true);
  }

  public void paintView(Graphics2D g, boolean overlays) {
    if (g == null) {
      return;
    }
    int w = Math.max(1, getWidth());
    int h = Math.max(1, getHeight());
    g.setColor(Color.BLACK);
    g.fillRect(0, 0, w, h);
    if (source != null) {
      Graphics2D imgG = (Graphics2D) g.create();
      try {
        paintFlippedSource(imgG, w, h);
      } finally {
        imgG.dispose();
      }
      if (overlays) {
        paintMeasureGraphics(g);
      }
    }
    if (overlays) {
      paintDecorations(g);
    }
  }

  /**
   * Zoom/rotation stay a positive-scale CTM (headed-OK). Horizontal flip is a paint-time mirror
   * buffer: headed X11 {@code Graphics2D} drops both negative scale and dest-X swap on the
   * on-screen pipeline, the same path {@link #paintComponent(Graphics)} uses.
   */
  void paintFlippedSource(Graphics2D g2, int w, int h) {
    g2.setRenderingHint(
        RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    double scale = resolvedScale(w, h);
    AffineTransform tx = new AffineTransform();
    tx.translate(w / 2.0 + panX, h / 2.0 + panY);
    tx.rotate(Math.toRadians(rotation));
    tx.scale(scale, scale);
    tx.translate(-source.getWidth() / 2.0, -source.getHeight() / 2.0);
    g2.transform(tx);
    g2.drawImage(blitSource(), 0, 0, this);
  }

  BufferedImage blitSource() {
    return flip ? mirroredSource() : source;
  }

  BufferedImage mirroredSource() {
    if (flipBlit != null && flipBlitSrc == source) {
      return flipBlit;
    }
    flipBlit = horizontalMirror(source);
    flipBlitSrc = source;
    return flipBlit;
  }

  static BufferedImage horizontalMirror(BufferedImage src) {
    int w = src.getWidth();
    int h = src.getHeight();
    int type = src.getType();
    BufferedImage dst = new BufferedImage(w, h, type == 0 ? BufferedImage.TYPE_INT_ARGB : type);
    int bands = src.getRaster().getNumBands();
    int[] pixel = new int[bands];
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        src.getRaster().getPixel(x, y, pixel);
        dst.getRaster().setPixel(w - 1 - x, y, pixel);
      }
    }
    return dst;
  }

  protected void paintDecorations(Graphics2D g) {
    if (isLayerVisible(LayerType.ANNOTATION)) {
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
    if (isCrosshairPainted()) {
      Point2D.Double p = imageToView(crosshairX, crosshairY);
      int px = (int) Math.round(p.x);
      int py = (int) Math.round(p.y);
      int w = Math.max(1, getWidth());
      int h = Math.max(1, getHeight());
      g.setColor(Color.CYAN);
      g.drawLine(0, py, w, py);
      g.drawLine(px, 0, px, h);
    }
    paintMeasureGraphics(g);
  }

  void paintMeasureGraphics(Graphics2D g) {
    if (!isLayerVisible(LayerType.MEASURE) && !isLayerVisible(LayerType.DRAW)) {
      return;
    }
    for (Graphic graphic : graphics) {
      paintOneGraphic(g, graphic);
    }
  }

  void paintOneGraphic(Graphics2D g, Graphic graphic) {
    Shape shape = viewShape(graphic);
    if (shape == null) {
      return;
    }
    g.setPaint(graphic.getColorPaint() == null ? Color.YELLOW : graphic.getColorPaint());
    Stroke previous = g.getStroke();
    g.setStroke(strokeFor(graphic));
    g.draw(shape);
    g.setStroke(previous);
    paintGraphicLabel(g, graphic, shape);
    paintSelectionHandles(g, graphic);
  }

  void paintSelectionHandles(Graphics2D g, Graphic graphic) {
    if (g == null || graphic == null || !Boolean.TRUE.equals(graphic.getSelected())) {
      return;
    }
    for (Point2D.Double p : graphic.getPts()) {
      if (p == null) {
        continue;
      }
      Point2D.Double at = imageToView(p.x, p.y);
      int x = (int) Math.round(at.x) - 3;
      int y = (int) Math.round(at.y) - 3;
      g.setColor(Color.WHITE);
      g.fillRect(x, y, 7, 7);
      g.setColor(Color.BLACK);
      g.drawRect(x, y, 7, 7);
    }
  }

  void paintGraphicLabel(Graphics2D g, Graphic graphic, Shape shape) {
    if (g == null || graphic == null || shape == null) {
      return;
    }
    if (!Boolean.TRUE.equals(graphic.getLabelVisible())) {
      return;
    }
    String[] lines = graphic.getLabel();
    Rectangle2D box = shape.getBounds2D();
    float x = (float) (box.getX() + box.getWidth() / 2.0);
    float y = (float) (box.getY() + box.getHeight() / 2.0);
    if (lines != null) {
      for (String line : lines) {
        if (paintLabel(line)) {
          g.setColor(Color.BLACK);
          g.drawString(line, x + 1, y + 1);
          g.setPaint(graphic.getColorPaint() == null ? Color.YELLOW : graphic.getColorPaint());
          g.drawString(line, x, y);
          y += 14;
        }
      }
    }
    paintSelectedRoiStats(g, graphic, x, y);
  }

  static boolean paintLabel(String line) {
    return line != null && !line.isBlank() && !line.startsWith("0.0 ");
  }

  void paintSelectedRoiStats(Graphics2D g, Graphic graphic, float x, float y) {
    if (!(graphic instanceof GraphicArea) || graphic.getShape() == null) {
      return;
    }
    ImageRegionStatistics.Stats stats =
        ImageRegionStatistics.compute(
            getSourceImage(), graphic.getShape(), getModalityLutSlope(), getModalityLutIntercept());
    if (stats.getSamples() > 0) {
      g.setPaint(graphic.getColorPaint() == null ? Color.YELLOW : graphic.getColorPaint());
      g.drawString(stats.text(), x, y);
    }
  }

  static Stroke strokeFor(Graphic graphic) {
    float width = graphic.getLineThickness() == null ? 2.5f : graphic.getLineThickness();
    return new BasicStroke(Math.max(2.5f, width));
  }
}
