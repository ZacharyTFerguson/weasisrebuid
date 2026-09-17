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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.weasis.core.api.gui.Insertable;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.api.media.data.MediaSeries;
import org.weasis.core.api.media.data.TagW;
import org.weasis.core.ui.editor.image.DefaultView2d;
import org.weasis.core.ui.editor.image.GridMouseHandler;
import org.weasis.core.ui.editor.image.ImageViewerPlugin;
import org.weasis.core.ui.editor.image.MeasureToolBar;
import org.weasis.core.ui.editor.image.RotationToolBar;
import org.weasis.core.ui.editor.image.ScreenshotToolBar;
import org.weasis.core.ui.editor.image.SynchView;
import org.weasis.core.ui.editor.image.ViewTransferHandler;
import org.weasis.core.ui.editor.image.ViewerToolBar;
import org.weasis.core.ui.editor.image.ZoomToolBar;
import org.weasis.core.ui.util.ToolBarContainer;
import org.weasis.dicom.codec.KOSpecialElement;
import org.weasis.dicom.viewer2d.dockable.ImageTool;
import org.weasis.dicom.viewer2d.dockable.SegmentationTool;
import org.weasis.dicom.viewer2d.fusion.FusionColorBar;
import org.weasis.dicom.viewer2d.fusion.FusionController;

/** One tab: ImageViewerPlugin holding a {@link View2d}. MPR is {@code mpr.MprContainer}. */
public class View2dContainer extends ImageViewerPlugin<MediaElement> {

  public static final String NAME = "DICOM 2D";

  private final View2d view2d = new View2d();
  private final JPanel viewGrid = new JPanel(new GridLayout(1, 1));
  private final List<View2d> layout = new CopyOnWriteArrayList<>();
  private final DicomSynchManager synchManager = new DicomSynchManager();
  private final FusionController fusionController = new FusionController();
  private final ToolBarContainer toolbars = new ToolBarContainer();
  private final ViewerToolBar viewerToolBar = new ViewerToolBar();
  private final LutToolBar lutToolBar = new LutToolBar();
  private final ZoomToolBar zoomToolBar = new ZoomToolBar();
  private final RotationToolBar rotationToolBar = new RotationToolBar();
  private final ResetTools resetTools = new ResetTools();
  private final DcmHeaderToolBar headerToolBar = new DcmHeaderToolBar();
  private final ScreenshotToolBar screenshotToolBar = new ScreenshotToolBar();
  private final CineToolBar cineToolBar = new CineToolBar();
  private final MeasureToolBar measureToolBar = new MeasureToolBar();
  private final KeyObjectToolBar keyObjectToolBar = new KeyObjectToolBar();
  private final Basic3DToolBar basic3DToolBar = new Basic3DToolBar();
  private final SegmentationTool segmentationTool = new SegmentationTool();
  private final ImageTool imageTool = new ImageTool();
  private final ViewTransferHandler seriesDrop = new ViewTransferHandler();
  private int layoutIndex;

  public View2dContainer() {
    super(NAME);
    layout.add(view2d);
    fusionController.addTarget(view2d);
    segmentationTool.bind(view2d);
    imageTool.bind(view2d);
    bindToolBars();
    add(viewGrid, BorderLayout.CENTER);
    bindDrop(this);
    bindDrop(viewGrid);
    bindDrop(view2d);
    view2d.setSynchManager(synchManager);
    synchManager.add(view2d);
    View2dRegistry.register(view2d);
    View2dRegistry.select(view2d);
    armLayoutBounds();
    relayoutViews();
  }

  void armLayoutBounds() {
    addComponentListener(
        new ComponentAdapter() {
          @Override
          public void componentResized(ComponentEvent e) {
            DefaultView2d.dropBoundsCache();
          }
        });
  }

  void bindDrop(JComponent c) {
    c.setDropTarget(null);
    c.setTransferHandler(seriesDrop);
    c.putClientProperty(ImageViewerPlugin.class, this);
    c.putClientProperty(View2dContainer.class, this);
    ViewTransferHandler.armDrop(c);
  }

  void bindToolBars() {
    toolbars.registerToolBar(viewerToolBar);
    toolbars.registerToolBar(keyObjectToolBar);
    toolbars.registerToolBar(measureToolBar);
    toolbars.registerToolBar(lutToolBar);
    toolbars.registerToolBar(zoomToolBar);
    toolbars.registerToolBar(rotationToolBar);
    toolbars.registerToolBar(resetTools);
    toolbars.registerToolBar(headerToolBar);
    toolbars.registerToolBar(screenshotToolBar);
    toolbars.registerToolBar(cineToolBar);
    toolbars.registerToolBar(basic3DToolBar);
    toolbars.registerToolBar(fusionController.getColorBar());
    toolbars.registerToolBar(segmentationTool);
    toolbars.registerToolBar(imageTool);
    viewerToolBar.bind(view2d);
    keyObjectToolBar.bind(view2d);
    lutToolBar.bind(view2d);
    zoomToolBar.bind(view2d);
    rotationToolBar.bind(view2d);
    resetTools.bind(view2d);
    headerToolBar.bind(view2d);
    screenshotToolBar.bind(view2d);
    cineToolBar.bind(view2d);
    measureToolBar.bind(view2d);
    imageTool.bind(view2d);
    fillSeriesViewerUi();
  }

  void fillSeriesViewerUi() {
    List<Insertable> ui = getSeriesViewerUI().getToolBar();
    ui.clear();
    ui.add(viewerToolBar);
    ui.add(keyObjectToolBar);
    ui.add(measureToolBar);
    ui.add(lutToolBar);
    ui.add(zoomToolBar);
    ui.add(rotationToolBar);
    ui.add(resetTools);
    ui.add(headerToolBar);
    ui.add(screenshotToolBar);
    ui.add(cineToolBar);
    ui.add(basic3DToolBar);
    ui.add(fusionController.getColorBar());
    ui.add(segmentationTool);
    ui.add(imageTool);
  }

  public ToolBarContainer getToolBars() {
    return toolbars;
  }

  public ViewerToolBar getViewerToolBar() {
    return viewerToolBar;
  }

  public LutToolBar getLutToolBar() {
    return lutToolBar;
  }

  public ZoomToolBar getZoomToolBar() {
    return zoomToolBar;
  }

  public RotationToolBar getRotationToolBar() {
    return rotationToolBar;
  }

  public KeyObjectToolBar getKeyObjectToolBar() {
    return keyObjectToolBar;
  }

  public MeasureToolBar getMeasureToolBar() {
    return measureToolBar;
  }

  public Basic3DToolBar getBasic3DToolBar() {
    return basic3DToolBar;
  }

  public FusionController getFusionController() {
    return fusionController;
  }

  public FusionColorBar getFusionColorBar() {
    return fusionController.getColorBar();
  }

  public SegmentationTool getSegmentationTool() {
    return segmentationTool;
  }

  public ImageTool getImageTool() {
    return imageTool;
  }

  public DcmHeaderToolBar getHeaderToolBar() {
    return headerToolBar;
  }

  public ScreenshotToolBar getScreenshotToolBar() {
    return screenshotToolBar;
  }

  public View2d getView2d() {
    return view2d;
  }

  public DicomSynchManager getSynchManager() {
    return synchManager;
  }

  public List<View2d> getLayoutViews() {
    return List.copyOf(layout);
  }

  JPanel getViewGrid() {
    return viewGrid;
  }

  @Override
  public void setLayoutCount(int n) {
    int count = Math.max(1, n);
    growLayout(count);
    shrinkLayout(count);
    layoutIndex = Math.min(layoutIndex, layout.size() - 1);
    relayoutViews();
    revalidate();
    repaint();
  }

  void growLayout(int count) {
    while (layout.size() < count) {
      layout.add(emptyView2d());
    }
  }

  void shrinkLayout(int count) {
    while (layout.size() > count) {
      View2d removed = layout.remove(layout.size() - 1);
      synchManager.remove(removed);
      fusionController.removeTarget(removed);
      View2dRegistry.unregister(removed);
    }
  }

  View2d emptyView2d() {
    View2d extra = new View2d();
    extra.setSynchManager(synchManager);
    synchManager.add(extra);
    bindDrop(extra);
    View2dRegistry.register(extra);
    measureToolBar.attach(extra);
    extra.getMouseActions().setLeft(view2d.getMouseActions().getLeft());
    extra.setMeasureTool(view2d.getMeasureTool());
    fusionController.addTarget(extra);
    return extra;
  }

  void relayoutViews() {
    viewGrid.removeAll();
    viewGrid.setLayout(gridForCount(layout.size()));
    for (View2d v : layout) {
      bindDrop(v);
      viewGrid.add(v);
    }
    bindDrop(viewGrid);
    viewGrid.revalidate();
    viewGrid.repaint();
    DefaultView2d.dropBoundsCache();
  }

  static GridLayout gridForCount(int n) {
    int cols = Math.min(2, Math.max(1, n));
    int rows = (n + cols - 1) / cols;
    return new GridLayout(rows, cols);
  }

  @Override
  public int getLayoutCount() {
    return layout.size();
  }

  @Override
  public void resetDisplay() {
    resetTools.apply(org.weasis.core.ui.editor.image.ResetTools.ALL);
  }

  @Override
  public void applyPreset(int index) {
    focusedLayoutView().applyPreset(index);
  }

  View2d focusedLayoutView() {
    if (layoutIndex >= 0 && layoutIndex < layout.size()) {
      return layout.get(layoutIndex);
    }
    return view2d;
  }

  @Override
  public void selectAllGraphics() {
    focusedLayoutView().selectAllGraphics();
  }

  @Override
  public void deselectAllGraphics() {
    focusedLayoutView().deselectAllGraphics();
  }

  @Override
  public void deleteAllGraphics() {
    for (View2d v : layout) {
      wipeGraphics(v);
    }
  }

  static void wipeGraphics(View2d v) {
    v.abandonDrawing();
    v.selectAllGraphics();
    v.deleteSelectedGraphics();
  }

  @Override
  public DefaultView2d<?> canvasAt(Point screen) {
    DefaultView2d<?> cell = DefaultView2d.atScreen(screen, layout);
    return cell != null ? cell : super.canvasAt(screen);
  }

  public int getLayoutIndex() {
    return layoutIndex;
  }

  public void setLayoutIndex(int index) {
    if (index >= 0 && index < layout.size()) {
      layoutIndex = index;
      View2dRegistry.select(layout.get(index));
      refreshImageTool();
    }
  }

  public void applyFlip(boolean on) {
    View2d painted = paintedCell();
    for (View2d cell : layout) {
      cell.setFlip(on);
    }
    if (painted != null) {
      painted.setFlip(on);
      imageTool.bind(painted);
    }
    flushFlipPaint();
  }

  public void applyFilter(Object filter) {
    View2d painted = paintedCell();
    for (View2d cell : layout) {
      cell.setFilter(filter);
    }
    if (painted != null) {
      painted.setFilter(filter);
      lutToolBar.bind(painted);
    }
    flushFlipPaint();
  }

  public void applyWindow(boolean on) {
    View2d painted = paintedCell();
    for (View2d cell : layout) {
      cell.applyWindowChrome(on);
    }
    if (painted != null) {
      painted.applyWindowChrome(on);
      imageTool.bind(painted);
    }
    flushFlipPaint();
  }

  public void applyCrop(boolean on) {
    View2d painted = paintedCell();
    for (View2d cell : layout) {
      cell.applyCropChrome(on);
    }
    if (painted != null) {
      painted.applyCropChrome(on);
      imageTool.bind(painted);
    }
    flushFlipPaint();
  }

  public void applyBrightness(boolean on) {
    View2d painted = paintedCell();
    for (View2d cell : layout) {
      cell.applyBrightnessChrome(on);
    }
    if (painted != null) {
      painted.applyBrightnessChrome(on);
      imageTool.bind(painted);
    }
    flushFlipPaint();
  }

  public void applyAutoLevels(boolean on) {
    View2d painted = paintedCell();
    for (View2d cell : layout) {
      cell.applyAutoLevelsChrome(on);
    }
    if (painted != null) {
      painted.applyAutoLevelsChrome(on);
      imageTool.bind(painted);
    }
    flushFlipPaint();
  }

  public void applyMask(boolean on) {
    View2d painted = paintedCell();
    for (View2d cell : layout) {
      cell.applyMaskChrome(on);
    }
    if (painted != null) {
      painted.applyMaskChrome(on);
      imageTool.bind(painted);
    }
    flushFlipPaint();
  }

  public void applyShutter(boolean on) {
    View2d painted = paintedCell();
    for (View2d cell : layout) {
      cell.applyShutterChrome(on);
    }
    if (painted != null) {
      painted.applyShutterChrome(on);
      imageTool.bind(painted);
    }
    flushFlipPaint();
  }

  View2d paintedCell() {
    View2d showing = showingRasterCell();
    if (showing != null) {
      return showing;
    }
    for (View2d v : layout) {
      if (v.getSourceImage() != null) {
        return v;
      }
    }
    return focusedLayoutView();
  }

  View2d showingRasterCell() {
    View2d best = null;
    int area = 0;
    for (View2d v : layout) {
      if (!v.isShowing() || v.getSourceImage() == null) {
        continue;
      }
      int a = Math.max(1, v.getWidth()) * Math.max(1, v.getHeight());
      if (a > area) {
        area = a;
        best = v;
      }
    }
    return best;
  }

  void flushFlipPaint() {
    viewGrid.revalidate();
    revalidate();
    if (isShowing()) {
      paintImmediately(0, 0, Math.max(1, getWidth()), Math.max(1, getHeight()));
      return;
    }
    viewGrid.repaint();
    repaint();
  }

  void refreshImageTool() {
    View2d painted = paintedCell();
    imageTool.bind(painted);
    headerToolBar.bind(painted);
    screenshotToolBar.bind(painted);
  }

  public void cycleLayout(int delta) {
    if (layout.size() <= 1) {
      return;
    }
    setLayoutIndex(Math.floorMod(layoutIndex + delta, layout.size()));
  }

  @Override
  public boolean hasHangSlot() {
    return nextHangSlot() != 0;
  }

  @Override
  public synchronized void addSeries(MediaSeries<MediaElement> sequence) {
    super.addSeries(sequence);
    if (sequence == null || sequence.getMedias().isEmpty()) {
      return;
    }
    placeSeries(sequence);
  }

  @Override
  public void dropSeries(MediaSeries<MediaElement> sequence, JComponent onto) {
    if (sequence == null) {
      return;
    }
    rememberOpen(sequence);
    dropOnto(onto, sequence);
  }

  void rememberOpen(MediaSeries<MediaElement> sequence) {
    super.addSeries(sequence);
  }

  void dropOnto(JComponent onto, MediaSeries<MediaElement> sequence) {
    View2d cell = emptyHang(onto);
    if (cell != null) {
      hangCell(cell, sequence);
      return;
    }
    placeSeries(sequence);
  }

  View2d emptyHang(JComponent onto) {
    if (onto instanceof View2d cell && canFill(cell)) {
      return cell;
    }
    return firstUnpaintedExtra();
  }

  View2d firstUnpaintedExtra() {
    for (int i = 1; i < layout.size(); i++) {
      if (needsPaint(layout.get(i))) {
        return layout.get(i);
      }
    }
    return firstEmptyExtra();
  }

  View2d firstEmptyExtra() {
    for (int i = 1; i < layout.size(); i++) {
      if (layout.get(i).getSeries() == null) {
        return layout.get(i);
      }
    }
    return null;
  }

  boolean canFill(View2d cell) {
    return cell != null && cell != view2d && layout.contains(cell) && needsPaint(cell);
  }

  boolean needsPaint(View2d cell) {
    return cell.getSourceImage() == null || isCloneSlot(cell, view2d.getSeries());
  }

  boolean isEmptyHang(View2d cell) {
    return canFill(cell);
  }

  @Override
  public JComponent dropCellAt(Point p) {
    JComponent cell = cellAt(p);
    return cell != null ? cell : this;
  }

  @Override
  public JComponent dropCellAtScreen(Point screen) {
    if (screen == null) {
      return null;
    }
    View2d hit = firstView(viewOnScreen(screen), gridView(screen), localView(screen));
    if (hit == null) {
      dumpCells(screen);
    }
    return hit;
  }

  void dumpCells(Point screen) {
    System.err.println(
        "view-grid miss pointer="
            + screen
            + " gridBox="
            + showingBox(viewGrid)
            + " pluginBox="
            + showingBox(this)
            + " n="
            + layout.size());
  }

  View2d gridView(Point screen) {
    Rectangle box = showingBox(viewGrid);
    if (box == null || !box.contains(screen)) {
      return null;
    }
    return layoutCellAt(screen.x - box.x, screen.y - box.y, box.width, box.height);
  }

  View2d layoutCellAt(int x, int y, int w, int h) {
    int n = layout.size();
    int cols = Math.min(2, Math.max(1, n));
    int rows = (n + cols - 1) / cols;
    int idx = new GridMouseHandler().cellAt(w, h, rows, cols, x, y);
    return idx >= 0 && idx < n ? layout.get(idx) : null;
  }

  View2d localView(Point screen) {
    Point local = fromScreen(this, screen);
    if (local == null) {
      return null;
    }
    JComponent cell = cellAt(local);
    return cell instanceof View2d v ? v : null;
  }

  static View2d firstView(View2d a, View2d b, View2d c) {
    if (a != null) {
      return a;
    }
    return b != null ? b : c;
  }

  static Rectangle showingBox(JComponent c) {
    return DefaultView2d.liveScreenBox(c);
  }

  static Point fromScreen(JComponent c, Point screen) {
    if (missingShow(c, screen)) {
      return null;
    }
    return convertFromScreen(c, screen);
  }

  static boolean missingShow(JComponent c, Point screen) {
    return c == null || screen == null || !c.isShowing();
  }

  static Point convertFromScreen(JComponent c, Point screen) {
    try {
      Point local = new Point(screen);
      SwingUtilities.convertPointFromScreen(local, c);
      return local;
    } catch (IllegalComponentStateException e) {
      return null;
    }
  }

  View2d viewOnScreen(Point screen) {
    DefaultView2d<?> hit = DefaultView2d.atScreen(screen, layout);
    return hit instanceof View2d v ? v : null;
  }

  static boolean boxContains(Rectangle box, Point screen) {
    return box != null && screen != null && box.contains(screen);
  }

  static boolean shownContains(JComponent c, Point screen) {
    Rectangle box = showingBox(c);
    return box != null && screen != null && box.contains(screen);
  }

  JComponent cellAt(Point p) {
    if (p == null) {
      return null;
    }
    JComponent byBounds = cellContaining(p);
    if (byBounds != null) {
      return byBounds;
    }
    return layoutCell(SwingUtilities.getDeepestComponentAt(this, p.x, p.y));
  }

  JComponent cellContaining(Point p) {
    for (View2d v : layout) {
      if (cellBounds(v).contains(p)) {
        return v;
      }
    }
    return null;
  }

  Rectangle cellBounds(View2d v) {
    if (v.getParent() == null) {
      return v.getBounds();
    }
    return SwingUtilities.convertRectangle(v.getParent(), v.getBounds(), this);
  }

  JComponent layoutCell(Component c) {
    while (c != null) {
      if (c instanceof View2d view && layout.contains(view)) {
        return view;
      }
      c = c.getParent();
    }
    return this;
  }

  @Override
  public void hangSeries(List<MediaSeries<MediaElement>> series) {
    if (series == null || series.isEmpty()) {
      return;
    }
    growToHung(series.size());
    hangPrimary(series.getFirst());
    hangRest(series);
  }

  void growToHung(int n) {
    if (layout.size() < n) {
      setLayoutCount(n);
    }
  }

  void hangPrimary(MediaSeries<MediaElement> primary) {
    super.addSeries(primary);
    view2d.setSeries(primary);
    loadFirstMedia(primary);
  }

  void hangRest(List<MediaSeries<MediaElement>> series) {
    MediaSeries<MediaElement> primary = series.getFirst();
    for (int i = 1; i < layout.size(); i++) {
      MediaSeries<MediaElement> cell = seriesAt(series, i, primary);
      hangCell(layout.get(i), cell);
      rememberHung(cell, primary);
    }
  }

  static MediaSeries<MediaElement> seriesAt(
      List<MediaSeries<MediaElement>> series, int i, MediaSeries<MediaElement> primary) {
    return i < series.size() ? series.get(i) : primary;
  }

  void rememberHung(MediaSeries<MediaElement> cell, MediaSeries<MediaElement> primary) {
    if (cell != null && cell != primary) {
      super.addSeries(cell);
    }
  }

  void placeSeries(MediaSeries<MediaElement> sequence) {
    if (alreadyHung(sequence)) {
      return;
    }
    int slot = nextHangSlot();
    if (slot == 0) {
      putPrimary(sequence);
      return;
    }
    hangCell(layout.get(slot), sequence);
  }

  boolean alreadyHung(MediaSeries<MediaElement> sequence) {
    for (View2d cell : layout) {
      if (sameSeries(cell.getSeries(), sequence)) {
        return true;
      }
    }
    return false;
  }

  void putPrimary(MediaSeries<MediaElement> sequence) {
    view2d.setSeries(sequence);
    loadFirstMedia(sequence);
  }

  int nextHangSlot() {
    if (view2d.getSeries() == null) {
      return 0;
    }
    return firstCloneSlot();
  }

  int firstCloneSlot() {
    MediaSeries<?> primary = view2d.getSeries();
    for (int i = 1; i < layout.size(); i++) {
      if (isCloneSlot(layout.get(i), primary)) {
        return i;
      }
    }
    return 0;
  }

  static boolean isCloneSlot(View2d cell, MediaSeries<?> primary) {
    MediaSeries<?> hung = cell.getSeries();
    return hung == null || sameSeries(hung, primary);
  }

  static boolean sameSeries(MediaSeries<?> a, MediaSeries<?> b) {
    if (a == null || b == null) {
      return false;
    }
    return a == b || sameUid(a, b);
  }

  static boolean sameUid(MediaSeries<?> a, MediaSeries<?> b) {
    String uid = seriesUid(a);
    return !uid.isEmpty() && uid.equals(seriesUid(b));
  }

  static String seriesUid(MediaSeries<?> series) {
    Object v = series.getTagValue(TagW.SeriesInstanceUID);
    return v == null ? "" : v.toString();
  }

  void hangCell(View2d cell, MediaSeries<MediaElement> sequence) {
    if (cell == null || sequence == null) {
      return;
    }
    View2d from = paintedView(sequence);
    cell.setSeries(seriesToHang(sequence, from));
    loadInto(cell, cell.getSeries());
    copyPaint(cell, from != null ? from : paintedView(sequence));
    cell.repaint();
    refreshImageTool();
  }

  MediaSeries<MediaElement> seriesToHang(MediaSeries<MediaElement> sequence, View2d from) {
    if (from != null && from.getSeries() != null) {
      return asMedia(from.getSeries());
    }
    return loadable(sequence);
  }

  View2d paintedView(MediaSeries<MediaElement> sequence) {
    for (View2d v : layout) {
      if (v.getSourceImage() != null && samePaintSource(v.getSeries(), sequence)) {
        return v;
      }
    }
    return null;
  }

  static boolean samePaintSource(MediaSeries<?> hung, MediaSeries<?> drop) {
    return sameSeries(hung, drop) || sameMediaUri(hung, drop);
  }

  static boolean sameMediaUri(MediaSeries<?> a, MediaSeries<?> b) {
    String uri = firstUri(a);
    return !uri.isEmpty() && uri.equals(firstUri(b));
  }

  static String firstUri(MediaSeries<?> series) {
    if (series == null || series.getMedias().isEmpty()) {
      return "";
    }
    URI uri = series.getMedias().getFirst().getMediaURI();
    return uri == null ? "" : uri.toString();
  }

  static void copyPaint(View2d cell, View2d from) {
    if (from == null || cell == from) {
      return;
    }
    cell.copyDisplay(from);
  }

  MediaSeries<MediaElement> loadable(MediaSeries<MediaElement> sequence) {
    return hasUri(sequence) ? sequence : hungSameUid(sequence);
  }

  static boolean hasUri(MediaSeries<?> sequence) {
    if (sequence == null || sequence.getMedias().isEmpty()) {
      return false;
    }
    return sequence.getMedias().getFirst().getMediaURI() != null;
  }

  MediaSeries<MediaElement> hungSameUid(MediaSeries<MediaElement> sequence) {
    for (View2d v : layout) {
      if (sameSeries(v.getSeries(), sequence) && hasUri(v.getSeries())) {
        return asMedia(v.getSeries());
      }
    }
    return sequence;
  }

  @SuppressWarnings("unchecked")
  static MediaSeries<MediaElement> asMedia(MediaSeries<? extends MediaElement> series) {
    return (MediaSeries<MediaElement>) series;
  }

  void loadFirstMedia(MediaSeries<MediaElement> sequence) {
    loadInto(view2d, sequence);
    view2d.setSynch(SynchView.STACK);
    refreshImageTool();
  }

  void loadInto(View2d cell, MediaSeries<? extends MediaElement> sequence) {
    if (sequence == null || sequence.getMedias().isEmpty()) {
      return;
    }
    URI uri = sequence.getMedias().getFirst().getMediaURI();
    if (uri == null) {
      return;
    }
    tryLoad(cell, uri);
  }

  void tryLoad(View2d cell, URI uri) {
    File file = fileOf(uri);
    if (file == null || !file.isFile()) {
      return;
    }
    try {
      cell.load(file);
    } catch (Exception e) {
      cell.setGeometryWarning("Unable to open DICOM");
    }
  }

  static File fileOf(URI uri) {
    if (uri == null) {
      return null;
    }
    try {
      return new File(uri);
    } catch (Exception e) {
      return uri.getPath() == null ? null : new File(uri.getPath());
    }
  }

  @Override
  public void applyOverlay(MediaSeries<MediaElement> sequence) {
    if (sequence == null) {
      return;
    }
    applyKoFrom(sequence);
  }

  void applyKoFrom(MediaSeries<MediaElement> sequence) {
    for (MediaElement media : sequence.getMedias()) {
      applyKoMedia(media);
    }
  }

  void applyKoMedia(MediaElement media) {
    if (media instanceof KOSpecialElement ko) {
      applyKo(ko);
    }
  }

  void applyKo(KOSpecialElement ko) {
    for (View2d cell : layout) {
      applyKoTo(cell, ko);
    }
  }

  void applyKoTo(View2d cell, KOSpecialElement ko) {
    if (cell == null) {
      return;
    }
    cell.getKoManager().applyDocument(ko);
    cell.applyKeyImageFilter();
    keyObjectToolBar.syncFilter();
  }

  @Override
  public void close() {
    View2dRegistry.unregister(view2d);
    for (View2d v : layout) {
      View2dRegistry.unregister(v);
    }
    super.close();
  }
}
