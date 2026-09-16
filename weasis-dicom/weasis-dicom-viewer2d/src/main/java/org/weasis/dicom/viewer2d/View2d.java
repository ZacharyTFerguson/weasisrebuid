/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d;

import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.weasis.core.api.image.FilterOp;
import org.weasis.core.api.image.OverlayOp;
import org.weasis.core.api.image.PseudoColorOp;
import org.weasis.core.api.image.ShutterOp;
import org.weasis.core.api.image.WindowAndPresetsOp;
import org.weasis.core.api.image.util.WindLevelParameters;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.ui.editor.image.DefaultView2d;
import org.weasis.core.ui.editor.image.ImageViewerEventManager;
import org.weasis.core.ui.model.graphic.imp.angle.AngleToolGraphic;
import org.weasis.core.ui.model.graphic.imp.angle.CobbToolGraphic;
import org.weasis.core.ui.model.graphic.imp.area.EllipseGraphic;
import org.weasis.core.ui.model.graphic.imp.area.PolygonGraphic;
import org.weasis.core.ui.model.graphic.imp.line.CurveGraphic;
import org.weasis.core.ui.model.graphic.imp.line.LineGraphic;
import org.weasis.core.ui.model.graphic.imp.line.PolylineGraphic;
import org.weasis.dicom.codec.DicomMediaIO;
import org.weasis.dicom.codec.WindowLevelPainter;
import org.weasis.dicom.codec.utils.DicomMediaUtils;
import org.weasis.dicom.codec.utils.InstanceSpacing;
import org.weasis.dicom.codec.utils.RoiStatistics;

/**
 * DICOM 2D view. Op chain: WindowAndPresets → Filter → PseudoColor → Shutter → Overlay → Affine.
 */
public class View2d extends DefaultView2d<MediaElement> {

  private Attributes dataset;
  private WindLevelParameters fileWl = new WindLevelParameters(400, 40);
  private double window = 400;
  private double level = 40;
  private File file;
  private List<Attributes> stackDatasets = List.of();
  private List<File> stackFiles = List.of();
  private int pixelFrameIndex;
  private Optional<InstanceSpacing.Resolved> resolvedInstanceSpacing = Optional.empty();

  private LineGraphic draftLineCaliper;
  private boolean draftLineAwaitingSecondClick;

  private PolylineGraphic draftPolylineCaliper;

  private CurveGraphic draftCurveCaliper;

  private PolygonGraphic draftPolygonCaliper;

  private AngleToolGraphic draftAngleCaliper;
  private int draftAngleClickStage;

  private CobbToolGraphic draftCobbCaliper;
  private int draftCobbClickStage;

  private EllipseGraphic draftEllipseCaliper;
  private boolean draftEllipseAwaitingSecondClick;

  static final String MULTI_FRAME_REFUSED = "multi-frame instance refused";

  public View2d() {
    super();
  }

  public void load(File dicom) throws Exception {
    loadStack(List.of(dicom));
  }

  public void loadStack(List<File> files) throws IOException {
    if (files == null || files.isEmpty()) {
      throw new IllegalArgumentException("empty stack");
    }
    List<StackEntry> entries = new ArrayList<>();
    for (File f : files) {
      DicomMediaIO io = DicomMediaIO.open(f);
      Attributes dcm = io.getDataset();
      if (files.size() > 1 && dcm.getInt(Tag.NumberOfFrames, 1) > 1) {
        throw new IllegalArgumentException(
            "multi-frame instance cannot be combined with other stack files");
      }
      entries.add(new StackEntry(f, dcm));
    }
    String seriesUid = entries.getFirst().dataset.getString(Tag.SeriesInstanceUID, "");
    for (StackEntry entry : entries) {
      if (!seriesUid.equals(entry.dataset.getString(Tag.SeriesInstanceUID, ""))) {
        throw new IllegalArgumentException("mixed SeriesInstanceUID in stack");
      }
    }
    entries.sort(
        Comparator.comparingInt((StackEntry e) -> e.dataset.getInt(Tag.InstanceNumber, 0))
            .thenComparing(e -> e.file.getName()));
    List<Attributes> datasets = new ArrayList<>();
    List<File> stack = new ArrayList<>();
    for (StackEntry entry : entries) {
      datasets.add(entry.dataset);
      stack.add(entry.file);
    }
    this.stackDatasets = List.copyOf(datasets);
    this.stackFiles = List.copyOf(stack);
    showStackFrame(0);
  }

  public void load(Attributes dataset) {
    this.dataset = Objects.requireNonNull(dataset, "dataset");
    this.stackDatasets = List.of(this.dataset);
    this.stackFiles = file != null ? List.of(file) : List.of();
    bindDataset(this.dataset);
  }

  private void bindDataset(Attributes active) {
    fileWl = DicomMediaUtils.windowLevel(active, 400, 40);
    this.window = fileWl.getWindow();
    this.level = fileWl.getLevel();
    applyDatasetFlags();
    render();
  }

  private void showStackFrame(int requestedIndex) {
    if (stackDatasets.isEmpty()) {
      return;
    }
    if (isMultiframePixelStack()) {
      Attributes active = stackDatasets.getFirst();
      this.dataset = active;
      if (!stackFiles.isEmpty()) {
        this.file = stackFiles.getFirst();
      }
      int frames = active.getInt(Tag.NumberOfFrames, 1);
      pixelFrameIndex = Math.max(0, Math.min(requestedIndex, frames - 1));
      super.setFrameIndex(pixelFrameIndex);
      bindDataset(active);
      return;
    }
    int clamped = Math.max(0, Math.min(requestedIndex, stackDatasets.size() - 1));
    super.setFrameIndex(clamped);
    pixelFrameIndex = 0;
    this.dataset = stackDatasets.get(clamped);
    if (!stackFiles.isEmpty()) {
      this.file = stackFiles.get(clamped);
    }
    bindDataset(this.dataset);
  }

  private boolean isMultiframePixelStack() {
    return stackDatasets.size() == 1 && stackDatasets.getFirst().getInt(Tag.NumberOfFrames, 1) > 1;
  }

  public int getStackSize() {
    if (isMultiframePixelStack()) {
      return stackDatasets.getFirst().getInt(Tag.NumberOfFrames, 1);
    }
    return stackDatasets.size();
  }

  int getPixelFrameIndex() {
    return pixelFrameIndex;
  }

  @Override
  public void setFrameIndex(int frameIndex) {
    if (stackDatasets.isEmpty()) {
      super.setFrameIndex(frameIndex);
      return;
    }
    showStackFrame(frameIndex);
  }

  private record StackEntry(File file, Attributes dataset) {}

  public Attributes getDataset() {
    return dataset;
  }

  public File getFile() {
    return file;
  }

  public Optional<InstanceSpacing.Resolved> getResolvedInstanceSpacing() {
    return resolvedInstanceSpacing;
  }

  public String formatLineMeasureLabel(LineGraphic line) {
    return MeasurementLabel.formatLine(line, resolvedInstanceSpacing);
  }

  /**
   * Adds a line caliper in image pixel coordinates and binds its label to {@link
   * #formatLineMeasureLabel}.
   */
  public void addLineCaliper(Point2D startImage, Point2D endImage) {
    if (startImage == null || endImage == null) {
      return;
    }
    LineGraphic line = new LineGraphic();
    line.setHandlePoint(0, copyPoint(startImage));
    line.setHandlePoint(1, copyPoint(endImage));
    applyLineCaliperLabel(line);
    addGraphic(line);
  }

  /**
   * Adds a polyline caliper in image pixel coordinates and binds its label to {@link
   * #formatPolylineMeasureLabel}.
   */
  public void addPolylineCaliper(List<? extends Point2D> imagePoints) {
    if (imagePoints == null || imagePoints.size() < 2) {
      return;
    }
    PolylineGraphic poly = new PolylineGraphic();
    for (int i = 0; i < imagePoints.size(); i++) {
      Point2D p = imagePoints.get(i);
      if (p == null) {
        return;
      }
      poly.setHandlePoint(i, copyPoint(p));
    }
    applyPolylineCaliperLabel(poly);
    addGraphic(poly);
  }

  /**
   * Adds an open curve caliper in image pixel coordinates and binds its label to {@link
   * #formatCurveMeasureLabel}.
   */
  public void addCurveCaliper(List<? extends Point2D> imagePoints) {
    if (imagePoints == null || imagePoints.size() < 3) {
      return;
    }
    CurveGraphic curve = new CurveGraphic();
    for (int i = 0; i < imagePoints.size(); i++) {
      Point2D p = imagePoints.get(i);
      if (p == null) {
        return;
      }
      curve.setHandlePoint(i, copyPoint(p));
    }
    applyCurveCaliperLabel(curve);
    addGraphic(curve);
  }

  /**
   * Adds a closed polygon caliper in image pixel coordinates and binds its label to {@link
   * #formatPolygonMeasureLabel}.
   */
  public void addPolygonCaliper(List<? extends Point2D> imagePoints) {
    if (imagePoints == null || imagePoints.size() < 3) {
      return;
    }
    PolygonGraphic poly = new PolygonGraphic();
    for (int i = 0; i < imagePoints.size(); i++) {
      Point2D p = imagePoints.get(i);
      if (p == null) {
        return;
      }
      poly.setHandlePoint(i, copyPoint(p));
    }
    applyPolygonCaliperLabel(poly);
    addGraphic(poly);
  }

  /**
   * Adds an angle caliper in image pixel coordinates and binds its label to {@link
   * #formatAngleMeasureLabel}. Handle order: arm end (0), vertex (1), arm end (2).
   */
  public void addAngleCaliper(Point2D armEndA, Point2D vertex, Point2D armEndB) {
    if (armEndA == null || vertex == null || armEndB == null) {
      return;
    }
    AngleToolGraphic angle = new AngleToolGraphic();
    angle.setHandlePoint(0, copyPoint(armEndA));
    angle.setHandlePoint(1, copyPoint(vertex));
    angle.setHandlePoint(2, copyPoint(armEndB));
    applyAngleCaliperLabel(angle);
    addGraphic(angle);
  }

  /**
   * Adds a Cobb caliper in image pixel coordinates and binds its label to {@link
   * #formatCobbMeasureLabel}. Handle order: upper endplate (0, 1), lower endplate (2, 3).
   */
  public void addCobbCaliper(
      Point2D upperStart, Point2D upperEnd, Point2D lowerStart, Point2D lowerEnd) {
    if (upperStart == null || upperEnd == null || lowerStart == null || lowerEnd == null) {
      return;
    }
    CobbToolGraphic cobb = new CobbToolGraphic();
    cobb.setHandlePoint(0, copyPoint(upperStart));
    cobb.setHandlePoint(1, copyPoint(upperEnd));
    cobb.setHandlePoint(2, copyPoint(lowerStart));
    cobb.setHandlePoint(3, copyPoint(lowerEnd));
    applyCobbCaliperLabel(cobb);
    addGraphic(cobb);
  }

  /**
   * Adds an ellipse ROI caliper in image pixel coordinates and binds its label to {@link
   * #formatEllipseMeasureLabel} on the graphic bbox {@link Ellipse2D}.
   */
  public void addEllipseCaliper(Point2D cornerA, Point2D cornerB) {
    if (cornerA == null || cornerB == null) {
      return;
    }
    EllipseGraphic ellipse = new EllipseGraphic();
    ellipse.setHandlePoint(0, copyPoint(cornerA));
    ellipse.setHandlePoint(1, copyPoint(cornerB));
    applyEllipseCaliperLabel(ellipse);
    addGraphic(ellipse);
  }

  /** Test hook: two-click line draw in view coordinates when left action is {@code draw}. */
  public void simulateLineDrawTwoClick(int viewX1, int viewY1, int viewX2, int viewY2) {
    lineDrawViewPressed(viewX1, viewY1);
    lineDrawViewReleased(viewX1, viewY1);
    lineDrawViewPressed(viewX2, viewY2);
    lineDrawViewReleased(viewX2, viewY2);
  }

  /** Test hook: two-click ellipse draw in view coordinates when left action is {@code ellipse}. */
  public void simulateEllipseDrawTwoClick(int viewX1, int viewY1, int viewX2, int viewY2) {
    ellipseDrawViewPressed(viewX1, viewY1);
    ellipseDrawViewReleased(viewX1, viewY1);
    ellipseDrawViewPressed(viewX2, viewY2);
    ellipseDrawViewReleased(viewX2, viewY2);
  }

  void lineDrawViewPressed(int viewX, int viewY) {
    Point2D.Double image = viewToImage(viewX, viewY);
    if (draftLineCaliper != null && draftLineAwaitingSecondClick) {
      draftLineCaliper.setHandlePoint(1, image);
      finalizeDraftLineCaliper();
      return;
    }
    draftLineCaliper = new LineGraphic();
    draftLineCaliper.setHandlePoint(0, image);
    draftLineCaliper.setHandlePoint(1, new Point2D.Double(image.x, image.y));
    draftLineAwaitingSecondClick = true;
    addGraphic(draftLineCaliper);
    repaint();
  }

  void lineDrawViewDragged(int viewX, int viewY) {
    if (draftLineCaliper == null) {
      return;
    }
    draftLineAwaitingSecondClick = false;
    draftLineCaliper.setHandlePoint(1, viewToImage(viewX, viewY));
    repaint();
  }

  void lineDrawViewReleased(int viewX, int viewY) {
    if (draftLineCaliper == null) {
      return;
    }
    if (!draftLineAwaitingSecondClick) {
      draftLineCaliper.setHandlePoint(1, viewToImage(viewX, viewY));
      finalizeDraftLineCaliper();
    }
  }

  private void finalizeDraftLineCaliper() {
    applyLineCaliperLabel(draftLineCaliper);
    draftLineCaliper = null;
    draftLineAwaitingSecondClick = false;
    repaint();
  }

  private void applyLineCaliperLabel(LineGraphic line) {
    line.setLabel(new String[] {formatLineMeasureLabel(line)});
  }

  void ellipseDrawViewPressed(int viewX, int viewY) {
    Point2D.Double image = viewToImage(viewX, viewY);
    if (draftEllipseCaliper != null && draftEllipseAwaitingSecondClick) {
      draftEllipseCaliper.setHandlePoint(1, image);
      finalizeDraftEllipseCaliper();
      return;
    }
    draftEllipseCaliper = new EllipseGraphic();
    draftEllipseCaliper.setHandlePoint(0, image);
    draftEllipseCaliper.setHandlePoint(1, new Point2D.Double(image.x, image.y));
    draftEllipseAwaitingSecondClick = true;
    addGraphic(draftEllipseCaliper);
    repaint();
  }

  void ellipseDrawViewDragged(int viewX, int viewY) {
    if (draftEllipseCaliper == null) {
      return;
    }
    draftEllipseAwaitingSecondClick = false;
    draftEllipseCaliper.setHandlePoint(1, viewToImage(viewX, viewY));
    repaint();
  }

  void ellipseDrawViewReleased(int viewX, int viewY) {
    if (draftEllipseCaliper == null) {
      return;
    }
    if (!draftEllipseAwaitingSecondClick) {
      draftEllipseCaliper.setHandlePoint(1, viewToImage(viewX, viewY));
      finalizeDraftEllipseCaliper();
    }
  }

  private void finalizeDraftEllipseCaliper() {
    applyEllipseCaliperLabel(draftEllipseCaliper);
    draftEllipseCaliper = null;
    draftEllipseAwaitingSecondClick = false;
    repaint();
  }

  private void applyEllipseCaliperLabel(EllipseGraphic ellipse) {
    if (ellipse.getShape() instanceof Ellipse2D roi) {
      ellipse.setLabel(new String[] {formatEllipseMeasureLabel(roi)});
    } else {
      ellipse.setLabel(new String[] {""});
    }
  }

  private static Point2D.Double copyPoint(Point2D p) {
    return new Point2D.Double(p.getX(), p.getY());
  }

  boolean isLineDrawMouseAction(String normalizedLeftAction) {
    return org.weasis.core.ui.editor.image.MouseActions.DRAW.equals(normalizedLeftAction);
  }

  boolean isPolylineDrawMouseAction(String normalizedLeftAction) {
    return org.weasis.core.ui.editor.image.MouseActions.POLYLINE.equals(normalizedLeftAction);
  }

  boolean isCurveDrawMouseAction(String normalizedLeftAction) {
    return org.weasis.core.ui.editor.image.MouseActions.CURVE.equals(normalizedLeftAction);
  }

  boolean isPolygonDrawMouseAction(String normalizedLeftAction) {
    return org.weasis.core.ui.editor.image.MouseActions.POLYGON.equals(normalizedLeftAction);
  }

  boolean isAngleDrawMouseAction(String normalizedLeftAction) {
    return org.weasis.core.ui.editor.image.MouseActions.ANGLE.equals(normalizedLeftAction);
  }

  boolean isCobbDrawMouseAction(String normalizedLeftAction) {
    return org.weasis.core.ui.editor.image.MouseActions.COBB.equals(normalizedLeftAction);
  }

  boolean isEllipseDrawMouseAction(String normalizedLeftAction) {
    return org.weasis.core.ui.editor.image.MouseActions.ELLIPSE.equals(normalizedLeftAction);
  }

  /** Test hook: polyline vertex click in view coordinates when left action is {@code polyline}. */
  public void simulatePolylineDrawClick(int viewX, int viewY, int clickCount) {
    if (clickCount >= 2) {
      polylineDrawViewPressed(viewX, viewY, 1);
      polylineDrawViewReleased(viewX, viewY, 1);
    }
    polylineDrawViewPressed(viewX, viewY, clickCount);
    polylineDrawViewReleased(viewX, viewY, clickCount);
  }

  void polylineDrawViewPressed(int viewX, int viewY, int clickCount) {
    if (clickCount >= 2 && draftPolylineCaliper != null) {
      finalizeDraftPolylineCaliper();
      return;
    }
    Point2D.Double image = viewToImage(viewX, viewY);
    if (draftPolylineCaliper == null) {
      draftPolylineCaliper = new PolylineGraphic();
      draftPolylineCaliper.setHandlePoint(0, image);
      draftPolylineCaliper.setHandlePoint(1, new Point2D.Double(image.x, image.y));
      addGraphic(draftPolylineCaliper);
      repaint();
      return;
    }
    int last = draftPolylineCaliper.getPts().size() - 1;
    draftPolylineCaliper.setHandlePoint(last, image);
    draftPolylineCaliper.setHandlePoint(last + 1, new Point2D.Double(image.x, image.y));
    repaint();
  }

  void polylineDrawViewDragged(int viewX, int viewY) {
    if (draftPolylineCaliper == null) {
      return;
    }
    int last = draftPolylineCaliper.getPts().size() - 1;
    draftPolylineCaliper.setHandlePoint(last, viewToImage(viewX, viewY));
    repaint();
  }

  void polylineDrawViewReleased(int viewX, int viewY, int clickCount) {
    if (draftPolylineCaliper == null) {
      return;
    }
    if (clickCount >= 2) {
      finalizeDraftPolylineCaliper();
    }
  }

  /** Test hook: curve vertex click in view coordinates when left action is {@code curve}. */
  public void simulateCurveDrawClick(int viewX, int viewY, int clickCount) {
    if (clickCount >= 2) {
      curveDrawViewPressed(viewX, viewY, 1);
      curveDrawViewReleased(viewX, viewY, 1);
    }
    curveDrawViewPressed(viewX, viewY, clickCount);
    curveDrawViewReleased(viewX, viewY, clickCount);
  }

  void curveDrawViewPressed(int viewX, int viewY, int clickCount) {
    if (clickCount >= 2 && draftCurveCaliper != null) {
      finalizeDraftCurveCaliper();
      return;
    }
    Point2D.Double image = viewToImage(viewX, viewY);
    if (draftCurveCaliper == null) {
      draftCurveCaliper = new CurveGraphic();
      draftCurveCaliper.setHandlePoint(0, image);
      draftCurveCaliper.setHandlePoint(1, new Point2D.Double(image.x, image.y));
      addGraphic(draftCurveCaliper);
      repaint();
      return;
    }
    int last = draftCurveCaliper.getPts().size() - 1;
    draftCurveCaliper.setHandlePoint(last, image);
    draftCurveCaliper.setHandlePoint(last + 1, new Point2D.Double(image.x, image.y));
    repaint();
  }

  void curveDrawViewDragged(int viewX, int viewY) {
    if (draftCurveCaliper == null) {
      return;
    }
    int last = draftCurveCaliper.getPts().size() - 1;
    draftCurveCaliper.setHandlePoint(last, viewToImage(viewX, viewY));
    repaint();
  }

  void curveDrawViewReleased(int viewX, int viewY, int clickCount) {
    if (draftCurveCaliper == null) {
      return;
    }
    if (clickCount >= 2) {
      finalizeDraftCurveCaliper();
    }
  }

  /** Test hook: polygon vertex click in view coordinates when left action is {@code polygon}. */
  public void simulatePolygonDrawClick(int viewX, int viewY, int clickCount) {
    if (clickCount >= 2) {
      polygonDrawViewPressed(viewX, viewY, 1);
      polygonDrawViewReleased(viewX, viewY, 1);
    }
    polygonDrawViewPressed(viewX, viewY, clickCount);
    polygonDrawViewReleased(viewX, viewY, clickCount);
  }

  void polygonDrawViewPressed(int viewX, int viewY, int clickCount) {
    if (clickCount >= 2 && draftPolygonCaliper != null) {
      finalizeDraftPolygonCaliper();
      return;
    }
    Point2D.Double image = viewToImage(viewX, viewY);
    if (draftPolygonCaliper == null) {
      draftPolygonCaliper = new PolygonGraphic();
      draftPolygonCaliper.setHandlePoint(0, image);
      draftPolygonCaliper.setHandlePoint(1, new Point2D.Double(image.x, image.y));
      addGraphic(draftPolygonCaliper);
      repaint();
      return;
    }
    int last = draftPolygonCaliper.getPts().size() - 1;
    draftPolygonCaliper.setHandlePoint(last, image);
    draftPolygonCaliper.setHandlePoint(last + 1, new Point2D.Double(image.x, image.y));
    repaint();
  }

  void polygonDrawViewDragged(int viewX, int viewY) {
    if (draftPolygonCaliper == null) {
      return;
    }
    int last = draftPolygonCaliper.getPts().size() - 1;
    draftPolygonCaliper.setHandlePoint(last, viewToImage(viewX, viewY));
    repaint();
  }

  void polygonDrawViewReleased(int viewX, int viewY, int clickCount) {
    if (draftPolygonCaliper == null) {
      return;
    }
    if (clickCount >= 2) {
      finalizeDraftPolygonCaliper();
    }
  }

  private void finalizeDraftPolygonCaliper() {
    if (draftPolygonCaliper == null) {
      return;
    }
    trimTrailingRubberBand(draftPolygonCaliper);
    if (draftPolygonCaliper.getPts().size() < 3) {
      getGraphicList().remove(draftPolygonCaliper);
    } else {
      applyPolygonCaliperLabel(draftPolygonCaliper);
    }
    draftPolygonCaliper = null;
    repaint();
  }

  private static void trimTrailingRubberBand(PolygonGraphic poly) {
    List<Point2D.Double> pts = new ArrayList<>(poly.getPts());
    while (pts.size() > 2) {
      Point2D.Double last = pts.getLast();
      Point2D.Double prev = pts.get(pts.size() - 2);
      if (last.distance(prev) > 1e-6) {
        break;
      }
      pts.removeLast();
    }
    if (pts.size() > 1) {
      Point2D.Double last = pts.getLast();
      Point2D.Double prev = pts.get(pts.size() - 2);
      if (last.distance(prev) <= 1e-6) {
        pts.removeLast();
      }
    }
    poly.setPts(pts);
  }

  private void applyPolygonCaliperLabel(PolygonGraphic poly) {
    poly.setLabel(new String[] {formatPolygonMeasureLabel(poly)});
  }

  private void finalizeDraftPolylineCaliper() {
    if (draftPolylineCaliper == null) {
      return;
    }
    trimTrailingRubberBand(draftPolylineCaliper);
    if (draftPolylineCaliper.getPts().size() < 2) {
      getGraphicList().remove(draftPolylineCaliper);
    } else {
      applyPolylineCaliperLabel(draftPolylineCaliper);
    }
    draftPolylineCaliper = null;
    repaint();
  }

  private static void trimTrailingRubberBand(PolylineGraphic poly) {
    List<Point2D.Double> pts = new ArrayList<>(poly.getPts());
    while (pts.size() > 2) {
      Point2D.Double last = pts.getLast();
      Point2D.Double prev = pts.get(pts.size() - 2);
      if (last.distance(prev) > 1e-6) {
        break;
      }
      pts.removeLast();
    }
    if (pts.size() > 1) {
      Point2D.Double last = pts.getLast();
      Point2D.Double prev = pts.get(pts.size() - 2);
      if (last.distance(prev) <= 1e-6) {
        pts.removeLast();
      }
    }
    poly.setPts(pts);
  }

  private void applyPolylineCaliperLabel(PolylineGraphic poly) {
    poly.setLabel(new String[] {formatPolylineMeasureLabel(poly)});
  }

  private void finalizeDraftCurveCaliper() {
    if (draftCurveCaliper == null) {
      return;
    }
    trimTrailingRubberBand(draftCurveCaliper);
    if (draftCurveCaliper.getPts().size() < 3) {
      getGraphicList().remove(draftCurveCaliper);
    } else {
      applyCurveCaliperLabel(draftCurveCaliper);
    }
    draftCurveCaliper = null;
    repaint();
  }

  private static void trimTrailingRubberBand(CurveGraphic curve) {
    List<Point2D.Double> pts = new ArrayList<>(curve.getPts());
    while (pts.size() > 2) {
      Point2D.Double last = pts.getLast();
      Point2D.Double prev = pts.get(pts.size() - 2);
      if (last.distance(prev) > 1e-6) {
        break;
      }
      pts.removeLast();
    }
    if (pts.size() > 1) {
      Point2D.Double last = pts.getLast();
      Point2D.Double prev = pts.get(pts.size() - 2);
      if (last.distance(prev) <= 1e-6) {
        pts.removeLast();
      }
    }
    curve.setPts(pts);
  }

  private void applyCurveCaliperLabel(CurveGraphic curve) {
    curve.setLabel(new String[] {formatCurveMeasureLabel(curve)});
  }

  /** Test hook: angle handle click in view coordinates when left action is {@code angle}. */
  public void simulateAngleDrawClick(int viewX, int viewY) {
    angleDrawViewPressed(viewX, viewY);
    angleDrawViewReleased(viewX, viewY);
  }

  void angleDrawViewPressed(int viewX, int viewY) {
    Point2D.Double image = viewToImage(viewX, viewY);
    if (draftAngleCaliper == null) {
      draftAngleCaliper = new AngleToolGraphic();
      draftAngleCaliper.setHandlePoint(0, image);
      draftAngleCaliper.setHandlePoint(1, new Point2D.Double(image.x, image.y));
      draftAngleCaliper.setHandlePoint(2, new Point2D.Double(image.x, image.y));
      draftAngleClickStage = 1;
      addGraphic(draftAngleCaliper);
      repaint();
      return;
    }
    if (draftAngleClickStage == 1) {
      draftAngleCaliper.setHandlePoint(1, image);
      draftAngleClickStage = 2;
      repaint();
      return;
    }
    if (draftAngleClickStage == 2) {
      draftAngleCaliper.setHandlePoint(2, image);
      finalizeDraftAngleCaliper();
    }
  }

  void angleDrawViewReleased(int viewX, int viewY) {
    // Three-click placement; release does not advance stage beyond press.
  }

  private void finalizeDraftAngleCaliper() {
    if (draftAngleCaliper == null) {
      return;
    }
    applyAngleCaliperLabel(draftAngleCaliper);
    draftAngleCaliper = null;
    draftAngleClickStage = 0;
    repaint();
  }

  private void applyAngleCaliperLabel(AngleToolGraphic angle) {
    angle.setLabel(new String[] {formatAngleMeasureLabel(angle)});
  }

  /** Test hook: Cobb handle click in view coordinates when left action is {@code cobb}. */
  public void simulateCobbDrawClick(int viewX, int viewY) {
    cobbDrawViewPressed(viewX, viewY);
    cobbDrawViewReleased(viewX, viewY);
  }

  void cobbDrawViewPressed(int viewX, int viewY) {
    Point2D.Double image = viewToImage(viewX, viewY);
    if (draftCobbCaliper == null) {
      draftCobbCaliper = new CobbToolGraphic();
      draftCobbCaliper.setHandlePoint(0, image);
      draftCobbCaliper.setHandlePoint(1, new Point2D.Double(image.x, image.y));
      draftCobbCaliper.setHandlePoint(2, new Point2D.Double(image.x, image.y));
      draftCobbCaliper.setHandlePoint(3, new Point2D.Double(image.x, image.y));
      draftCobbClickStage = 1;
      addGraphic(draftCobbCaliper);
      repaint();
      return;
    }
    if (draftCobbClickStage == 1) {
      draftCobbCaliper.setHandlePoint(1, image);
      draftCobbClickStage = 2;
      repaint();
      return;
    }
    if (draftCobbClickStage == 2) {
      draftCobbCaliper.setHandlePoint(2, image);
      draftCobbClickStage = 3;
      repaint();
      return;
    }
    if (draftCobbClickStage == 3) {
      draftCobbCaliper.setHandlePoint(3, image);
      finalizeDraftCobbCaliper();
    }
  }

  void cobbDrawViewReleased(int viewX, int viewY) {
    // Four-click placement; release does not advance stage beyond press.
  }

  private void finalizeDraftCobbCaliper() {
    if (draftCobbCaliper == null) {
      return;
    }
    applyCobbCaliperLabel(draftCobbCaliper);
    draftCobbCaliper = null;
    draftCobbClickStage = 0;
    repaint();
  }

  private void applyCobbCaliperLabel(CobbToolGraphic cobb) {
    cobb.setLabel(new String[] {formatCobbMeasureLabel(cobb)});
  }

  public String formatPolylineMeasureLabel(PolylineGraphic polyline) {
    return MeasurementLabel.formatPolyline(polyline, resolvedInstanceSpacing);
  }

  public String formatCurveMeasureLabel(CurveGraphic curve) {
    return MeasurementLabel.formatCurve(curve, resolvedInstanceSpacing);
  }

  public String formatAngleMeasureLabel(AngleToolGraphic angle) {
    return MeasurementLabel.formatAngle(angle, resolvedInstanceSpacing);
  }

  public String formatCobbMeasureLabel(CobbToolGraphic cobb) {
    return MeasurementLabel.formatCobb(cobb, resolvedInstanceSpacing);
  }

  public String formatPolygonMeasureLabel(PolygonGraphic polygon) {
    return MeasurementLabel.formatPolygon(polygon, resolvedInstanceSpacing);
  }

  public String formatEllipseMeasureLabel(Ellipse2D roi) {
    if (dataset == null || roi == null) {
      return "";
    }
    return RoiStatistics.ellipse(dataset, roi).map(MeasurementLabel::formatEllipse).orElse("");
  }

  public double getWindow() {
    return window;
  }

  public double getLevel() {
    return level;
  }

  public void setWindowLevel(double window, double level) {
    this.window = window;
    this.level = level;
    getDisplayOpManager().setParamValue("op.window.presets", WindowAndPresetsOp.P_WINDOW, window);
    getDisplayOpManager().setParamValue("op.window.presets", WindowAndPresetsOp.P_LEVEL, level);
    render();
  }

  public WindLevelParameters getFileWindowLevel() {
    return fileWl;
  }

  @Override
  public void resetWinLevelDefaults() {
    if (fileWl != null) {
      setWindowLevel(fileWl.getWindow(), fileWl.getLevel());
    }
  }

  public void render() {
    if (dataset == null) {
      return;
    }
    BufferedImage painted =
        WindowLevelPainter.paintMonochrome2(dataset, pixelFrameIndex, window, level);
    painted = applyFilterAndColor(painted);
    painted = applyShutter(painted);
    painted = applyOverlay(painted);
    setSourceImage(painted);
  }

  BufferedImage applyFilterAndColor(BufferedImage src) {
    Object filter = getDisplayOpManager().getParamValue("op.filter", FilterOp.P_FILTER);
    Object invert = getDisplayOpManager().getParamValue("op.pseudocolor", PseudoColorOp.P_INVERT);
    if (!Boolean.TRUE.equals(invert) && (filter == null || FilterOp.NONE.equals(filter))) {
      return src;
    }
    if (Boolean.TRUE.equals(invert)) {
      WritableRaster raster = src.getRaster();
      byte[] data = ((DataBufferByte) raster.getDataBuffer()).getData();
      for (int i = 0; i < data.length; i++) {
        data[i] = (byte) (255 - (data[i] & 0xff));
      }
    }
    return src;
  }

  BufferedImage applyShutter(BufferedImage src) {
    Object enabled = getDisplayOpManager().getParamValue("op.shutter", ShutterOp.P_ENABLED);
    if (!Boolean.TRUE.equals(enabled) || dataset == null) {
      return src;
    }
    int left = intParam("op.shutter", ShutterOp.P_LEFT, 0);
    int right = intParam("op.shutter", ShutterOp.P_RIGHT, src.getWidth() - 1);
    int upper = intParam("op.shutter", ShutterOp.P_UPPER, 0);
    int lower = intParam("op.shutter", ShutterOp.P_LOWER, src.getHeight() - 1);
    byte[] data = ((DataBufferByte) src.getRaster().getDataBuffer()).getData();
    int w = src.getWidth();
    int h = src.getHeight();
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        if (x < left || x > right || y < upper || y > lower) {
          data[y * w + x] = 0;
        }
      }
    }
    return src;
  }

  BufferedImage applyOverlay(BufferedImage src) {
    Object enabled = getDisplayOpManager().getParamValue("op.overlay", OverlayOp.P_ENABLED);
    if (Boolean.FALSE.equals(enabled) || dataset == null || !dataset.contains(Tag.OverlayData)) {
      return src;
    }
    byte[] overlay;
    try {
      overlay = dataset.getBytes(Tag.OverlayData);
    } catch (Exception e) {
      return src;
    }
    if (overlay == null || overlay.length == 0) {
      return src;
    }
    byte[] data = ((DataBufferByte) src.getRaster().getDataBuffer()).getData();
    int n = Math.min(data.length, overlay.length * 8);
    for (int i = 0; i < n; i++) {
      int bit = (overlay[i / 8] >> (i % 8)) & 1;
      if (bit == 1) {
        data[i] = (byte) 255;
      }
    }
    return src;
  }

  void applyDatasetFlags() {
    if (dataset == null) {
      return;
    }
    String lossy = dataset.getString(Tag.LossyImageCompression, "");
    if ("01".equals(lossy) || dataset.contains(Tag.LossyImageCompressionRatio)) {
      setLossyLabel("LOSSY");
    } else {
      setLossyLabel("");
    }
    if ("RECTANGULAR".equalsIgnoreCase(dataset.getString(Tag.ShutterShape, ""))) {
      getDisplayOpManager().setParamValue("op.shutter", ShutterOp.P_ENABLED, Boolean.TRUE);
      getDisplayOpManager()
          .setParamValue(
              "op.shutter", ShutterOp.P_LEFT, dataset.getInt(Tag.ShutterLeftVerticalEdge, 0));
      getDisplayOpManager()
          .setParamValue(
              "op.shutter",
              ShutterOp.P_RIGHT,
              dataset.getInt(Tag.ShutterRightVerticalEdge, dataset.getInt(Tag.Columns, 0) - 1));
      getDisplayOpManager()
          .setParamValue(
              "op.shutter", ShutterOp.P_UPPER, dataset.getInt(Tag.ShutterUpperHorizontalEdge, 0));
      getDisplayOpManager()
          .setParamValue(
              "op.shutter",
              ShutterOp.P_LOWER,
              dataset.getInt(Tag.ShutterLowerHorizontalEdge, dataset.getInt(Tag.Rows, 0) - 1));
    }
    resolvedInstanceSpacing = InstanceSpacing.resolve(dataset);
    if (resolvedInstanceSpacing.isPresent()) {
      String warn = resolvedInstanceSpacing.get().warning();
      setGeometryWarning(warn == null ? "" : warn);
    } else {
      setGeometryWarning(MeasurementLabel.NO_USABLE_SPACING_WARNING);
    }
  }

  int intParam(String op, String key, int fallback) {
    Object v = getDisplayOpManager().getParamValue(op, key);
    if (v instanceof Number n) {
      return n.intValue();
    }
    return fallback;
  }

  @Override
  protected ImageViewerEventManager createEventManager() {
    return new View2dEventManager(this);
  }

  static final class View2dEventManager extends ImageViewerEventManager {
    private final View2d view2d;

    View2dEventManager(View2d view) {
      super(view);
      this.view2d = view;
    }

    @Override
    public void mousePressed(MouseEvent e) {
      if (cobbDrawActive(e)) {
        view2d.cobbDrawViewPressed(e.getX(), e.getY());
        return;
      }
      if (angleDrawActive(e)) {
        view2d.angleDrawViewPressed(e.getX(), e.getY());
        return;
      }
      if (polygonDrawActive(e)) {
        view2d.polygonDrawViewPressed(e.getX(), e.getY(), e.getClickCount());
        return;
      }
      if (polylineDrawActive(e)) {
        view2d.polylineDrawViewPressed(e.getX(), e.getY(), e.getClickCount());
        return;
      }
      if (curveDrawActive(e)) {
        view2d.curveDrawViewPressed(e.getX(), e.getY(), e.getClickCount());
        return;
      }
      if (ellipseDrawActive(e)) {
        view2d.ellipseDrawViewPressed(e.getX(), e.getY());
        return;
      }
      if (lineDrawActive(e)) {
        view2d.lineDrawViewPressed(e.getX(), e.getY());
        return;
      }
      super.mousePressed(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
      if (polygonDrawActive(e)) {
        view2d.polygonDrawViewDragged(e.getX(), e.getY());
        return;
      }
      if (polylineDrawActive(e)) {
        view2d.polylineDrawViewDragged(e.getX(), e.getY());
        return;
      }
      if (curveDrawActive(e)) {
        view2d.curveDrawViewDragged(e.getX(), e.getY());
        return;
      }
      if (ellipseDrawActive(e)) {
        view2d.ellipseDrawViewDragged(e.getX(), e.getY());
        return;
      }
      if (lineDrawActive(e)) {
        view2d.lineDrawViewDragged(e.getX(), e.getY());
        return;
      }
      super.mouseDragged(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      if (cobbDrawActive(e)) {
        view2d.cobbDrawViewReleased(e.getX(), e.getY());
        return;
      }
      if (angleDrawActive(e)) {
        view2d.angleDrawViewReleased(e.getX(), e.getY());
        return;
      }
      if (polygonDrawActive(e)) {
        view2d.polygonDrawViewReleased(e.getX(), e.getY(), e.getClickCount());
        return;
      }
      if (polylineDrawActive(e)) {
        view2d.polylineDrawViewReleased(e.getX(), e.getY(), e.getClickCount());
        return;
      }
      if (curveDrawActive(e)) {
        view2d.curveDrawViewReleased(e.getX(), e.getY(), e.getClickCount());
        return;
      }
      if (ellipseDrawActive(e)) {
        view2d.ellipseDrawViewReleased(e.getX(), e.getY());
        return;
      }
      if (lineDrawActive(e)) {
        view2d.lineDrawViewReleased(e.getX(), e.getY());
        return;
      }
      super.mouseReleased(e);
    }

    private boolean angleDrawActive(MouseEvent e) {
      String left =
          org.weasis.core.ui.editor.image.MouseActions.normalize(
              view2d.getMouseActions().getLeft());
      if (!view2d.isAngleDrawMouseAction(left)) {
        return false;
      }
      if (e.getID() == MouseEvent.MOUSE_DRAGGED) {
        return false;
      }
      return e.getButton() == MouseEvent.BUTTON1;
    }

    private boolean cobbDrawActive(MouseEvent e) {
      String left =
          org.weasis.core.ui.editor.image.MouseActions.normalize(
              view2d.getMouseActions().getLeft());
      if (!view2d.isCobbDrawMouseAction(left)) {
        return false;
      }
      if (e.getID() == MouseEvent.MOUSE_DRAGGED) {
        return false;
      }
      return e.getButton() == MouseEvent.BUTTON1;
    }

    private boolean polygonDrawActive(MouseEvent e) {
      String left =
          org.weasis.core.ui.editor.image.MouseActions.normalize(
              view2d.getMouseActions().getLeft());
      if (!view2d.isPolygonDrawMouseAction(left)) {
        return false;
      }
      if (e.getID() == MouseEvent.MOUSE_DRAGGED) {
        return (e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0;
      }
      return e.getButton() == MouseEvent.BUTTON1;
    }

    private boolean polylineDrawActive(MouseEvent e) {
      String left =
          org.weasis.core.ui.editor.image.MouseActions.normalize(
              view2d.getMouseActions().getLeft());
      if (!view2d.isPolylineDrawMouseAction(left)) {
        return false;
      }
      if (e.getID() == MouseEvent.MOUSE_DRAGGED) {
        return (e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0;
      }
      return e.getButton() == MouseEvent.BUTTON1;
    }

    private boolean curveDrawActive(MouseEvent e) {
      String left =
          org.weasis.core.ui.editor.image.MouseActions.normalize(
              view2d.getMouseActions().getLeft());
      if (!view2d.isCurveDrawMouseAction(left)) {
        return false;
      }
      if (e.getID() == MouseEvent.MOUSE_DRAGGED) {
        return (e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0;
      }
      return e.getButton() == MouseEvent.BUTTON1;
    }

    private boolean lineDrawActive(MouseEvent e) {
      String left =
          org.weasis.core.ui.editor.image.MouseActions.normalize(
              view2d.getMouseActions().getLeft());
      if (!view2d.isLineDrawMouseAction(left)) {
        return false;
      }
      if (e.getID() == MouseEvent.MOUSE_DRAGGED) {
        return (e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0;
      }
      return e.getButton() == MouseEvent.BUTTON1;
    }

    private boolean ellipseDrawActive(MouseEvent e) {
      String left =
          org.weasis.core.ui.editor.image.MouseActions.normalize(
              view2d.getMouseActions().getLeft());
      if (!view2d.isEllipseDrawMouseAction(left)) {
        return false;
      }
      if (e.getID() == MouseEvent.MOUSE_DRAGGED) {
        return (e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0;
      }
      return e.getButton() == MouseEvent.BUTTON1;
    }

    @Override
    protected void applyWindowLevel(int dx, int dy) {
      view2d.setWindowLevel(view2d.getWindow() + dx, view2d.getLevel() - dy);
    }
  }
}
