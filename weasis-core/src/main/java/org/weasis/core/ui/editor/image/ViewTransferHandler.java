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

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.IllegalComponentStateException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceAdapter;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceMotionListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import org.weasis.core.api.media.data.MediaSeries;
import org.weasis.core.api.media.data.SeriesThumbnail;
import org.weasis.core.api.service.UICore;
import org.weasis.core.ui.util.UriListFlavor;

/**
 * Drop files / URI-list / explorer series onto a view. Explorer thumbnails drag {@link MediaSeries}
 * onto the selected {@link ImageViewerPlugin} (hang slot, not a new tab).
 */
public class ViewTransferHandler extends TransferHandler {

  public static final DataFlavor SERIES_FLAVOR = new DataFlavor(MediaSeries.class, "MediaSeries");

  private static MediaSeries<?> dragging;
  private static MediaSeries<?> lastDragged;
  private static Point lastOver;
  private static String lastMiss = "";

  static {
    DragFill.arm();
  }

  private List<File> lastFiles = List.of();
  private MediaSeries<?> lastSeries;

  @Override
  public boolean canImport(JComponent comp, DataFlavor[] flavors) {
    return dragged() != null || canImportFlavors(flavors);
  }

  @Override
  public boolean canImport(TransferSupport support) {
    return support != null && (dragged() != null || canImportFlavors(support.getDataFlavors()));
  }

  boolean canImportFlavors(DataFlavor[] flavors) {
    return seriesFlavor(flavors) || fileFlavor(flavors) || uriFlavor(flavors);
  }

  boolean seriesFlavor(DataFlavor[] flavors) {
    return ImageTransferHandler.flavorIn(flavors, SERIES_FLAVOR);
  }

  boolean fileFlavor(DataFlavor[] flavors) {
    return ImageTransferHandler.flavorIn(flavors, DataFlavor.javaFileListFlavor);
  }

  boolean uriFlavor(DataFlavor[] flavors) {
    return ImageTransferHandler.flavorIn(flavors, UriListFlavor.flavor);
  }

  @Override
  public int getSourceActions(JComponent c) {
    return c instanceof SeriesThumbnail ? COPY : NONE;
  }

  @Override
  protected Transferable createTransferable(JComponent c) {
    if (c instanceof SeriesThumbnail thumb) {
      beginDrag(thumb.getSeries());
      return seriesTransferable(thumb.getSeries());
    }
    return super.createTransferable(c);
  }

  public static void beginDrag(MediaSeries<?> series) {
    dragging = series;
    lastDragged = series;
    lastOver = null;
    DragFill.arm();
  }

  public static void endDrag() {
    dragging = null;
  }

  public static void clearDragged() {
    dragging = null;
    lastDragged = null;
    lastOver = null;
  }

  public static MediaSeries<?> dragging() {
    return dragging;
  }

  public static MediaSeries<?> lastDragged() {
    return lastDragged;
  }

  public static MediaSeries<?> dragged() {
    return dragging != null ? dragging : lastDragged;
  }

  /** Headed fill: toolkit mouse-release while {@link #dragging()} (not native drop). */
  public static void hangFromAwt(AWTEvent event) {
    DragFill.INSTANCE.eventDispatched(event);
  }

  @Override
  protected void exportDone(JComponent source, Transferable data, int action) {
    hangAtPointer();
    endDrag();
    super.exportDone(source, data, action);
  }

  public static boolean hangAtPointer() {
    try {
      return hangScreen(overOrPointer());
    } catch (Exception e) {
      return false;
    }
  }

  static Point overOrPointer() {
    return lastOver != null ? lastOver : pointerLocation();
  }

  static Point pointerLocation() {
    PointerInfo info = MouseInfo.getPointerInfo();
    return info == null ? null : info.getLocation();
  }

  static boolean hangScreen(Point screen) {
    return screen != null && new ViewTransferHandler().hangAtScreen(screen);
  }

  public static void overAt(Point screen) {
    lastOver = screen;
  }

  public static Point lastOver() {
    return lastOver;
  }

  public static String lastMiss() {
    return lastMiss;
  }

  /**
   * Headed X11: hang the layout cell under the pointer. Native {@code exportAsDrag} is not used.
   */
  public boolean hangAtScreen(Point screen) {
    MediaSeries<?> series = dragged();
    JComponent cell = screenView(screen);
    if (series != null && cell != null && dropSeries(cell, series)) {
      lastMiss = "";
      return true;
    }
    if (series != null) {
      dumpMiss(screen, cell, series);
    }
    return false;
  }

  static void dumpMiss(Point screen, JComponent cell, MediaSeries<?> series) {
    lastMiss = missText(screen, cell, series);
    System.err.println(lastMiss);
  }

  static String missText(Point screen, JComponent cell, MediaSeries<?> series) {
    ImageViewerPlugin<?> plugin = UICore.getInstance().getFocusedImagePlugin();
    return "hangAtScreen miss pointer="
        + screen
        + " series="
        + series
        + " cell="
        + cell
        + " focused="
        + plugin
        + " focusedBox="
        + screenBox(plugin)
        + " dragging="
        + dragging();
  }

  static JComponent screenView(Point screen) {
    if (screen == null) {
      return null;
    }
    JComponent hit = cellFrom(pluginAt(screen), screen);
    if (hit != null) {
      return hit;
    }
    return cellFrom(UICore.getInstance().getFocusedImagePlugin(), screen);
  }

  static JComponent cellFrom(ImageViewerPlugin<?> plugin, Point screen) {
    return plugin == null ? null : plugin.dropCellAtScreen(screen);
  }

  static ImageViewerPlugin<?> pluginAt(Point screen) {
    ImageViewerPlugin<?> focused = UICore.getInstance().getFocusedImagePlugin();
    if (covers(focused, screen)) {
      return focused;
    }
    return coveredOpen(screen);
  }

  static ImageViewerPlugin<?> coveredOpen(Point screen) {
    for (ViewerPlugin<?> p : UICore.getInstance().getOpenViewerPlugins()) {
      if (p instanceof ImageViewerPlugin<?> image && covers(image, screen)) {
        return image;
      }
    }
    return null;
  }

  static boolean covers(JComponent c, Point screen) {
    Rectangle box = screenBox(c);
    return box != null && screen != null && box.contains(screen);
  }

  static Rectangle screenBox(JComponent c) {
    if (c == null || !c.isShowing()) {
      return null;
    }
    try {
      return new Rectangle(c.getLocationOnScreen(), c.getSize());
    } catch (IllegalComponentStateException e) {
      return null;
    }
  }

  public Transferable seriesTransferable(MediaSeries<?> series) {
    return new SeriesSelection(series);
  }

  @Override
  public boolean importData(JComponent comp, Transferable t) {
    return importAt(comp, null, t);
  }

  @Override
  public boolean importData(TransferSupport support) {
    if (support == null || !canImport(support)) {
      return false;
    }
    Point p = support.isDrop() ? support.getDropLocation().getDropPoint() : null;
    return importAt(support.getComponent(), p, support.getTransferable());
  }

  /** Headed glass / CContentArea drops: hit-test the View2d under {@code drop}, then hangCell. */
  public boolean importAt(Component host, Point drop, Transferable t) {
    MediaSeries<?> series = seriesFrom(t);
    JComponent onto = asJc(cellAt(host, drop));
    return series != null && onto != null ? dropSeries(onto, series) : importFilesFrom(t) > 0;
  }

  Component cellAt(Component host, Point p) {
    if (host == null) {
      return null;
    }
    if (p == null) {
      JComponent view = viewUnder(host);
      return view != null ? view : host;
    }
    return viewAt(host, p);
  }

  static JComponent viewAt(Component host, Point p) {
    if (host == null || p == null) {
      return null;
    }
    JComponent view = viewUnder(SwingUtilities.getDeepestComponentAt(host, p.x, p.y));
    if (view != null) {
      return view;
    }
    return cellInPlugin(pluginOf(asJc(host)), host, p);
  }

  static JComponent cellInPlugin(ImageViewerPlugin<?> plugin, Component host, Point p) {
    if (plugin == null) {
      return asJc(host);
    }
    return firstCell(
        screenCell(plugin, host, p), plugin.dropCellAt(pointIn(host, p, plugin)), plugin);
  }

  static JComponent firstCell(JComponent a, JComponent b, JComponent c) {
    if (a != null) {
      return a;
    }
    return b != null ? b : c;
  }

  static JComponent screenCell(ImageViewerPlugin<?> plugin, Component host, Point p) {
    if (missing(plugin, host, p) || !host.isShowing()) {
      return null;
    }
    return plugin.dropCellAtScreen(toScreen(host, p));
  }

  static boolean missing(ImageViewerPlugin<?> plugin, Component host, Point p) {
    return plugin == null || host == null || p == null;
  }

  static Point toScreen(Component host, Point p) {
    Point screen = new Point(p);
    SwingUtilities.convertPointToScreen(screen, host);
    return screen;
  }

  static Point pointIn(Component host, Point p, Component plugin) {
    if (host.isShowing() && plugin.isShowing()) {
      Point screen = toScreen(host, p);
      SwingUtilities.convertPointFromScreen(screen, plugin);
      return screen;
    }
    return SwingUtilities.convertPoint(host, p, plugin);
  }

  static JComponent asJc(Component c) {
    return c instanceof JComponent jc ? jc : null;
  }

  static JComponent viewUnder(Component c) {
    while (c != null) {
      if (c instanceof DefaultView2d<?>) {
        return (JComponent) c;
      }
      c = c.getParent();
    }
    return null;
  }

  public static void armDrop(JComponent host) {
    if (host != null) {
      host.setDropTarget(new DropTarget(host, COPY_OR_MOVE, new SeriesDrop(host), true));
    }
  }

  boolean importTransferable(Component comp, Transferable t) {
    return importAt(comp, null, t);
  }

  public boolean dropSeries(JComponent target, MediaSeries<?> series) {
    if (series == null) {
      return false;
    }
    lastSeries = series;
    dropInto(pluginOf(target), target, series);
    return true;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  static void dropInto(ImageViewerPlugin<?> plugin, JComponent target, MediaSeries<?> series) {
    if (plugin != null) {
      plugin.dropSeries((MediaSeries) series, target);
    }
  }

  static ImageViewerPlugin<?> pluginOf(JComponent comp) {
    Component c = comp;
    while (c != null) {
      if (c instanceof ImageViewerPlugin<?> plugin) {
        return plugin;
      }
      c = c.getParent();
    }
    return UICore.getInstance().getFocusedImagePlugin();
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  static void addToPlugin(ImageViewerPlugin<?> plugin, MediaSeries<?> series) {
    dropInto(plugin, plugin, series);
  }

  MediaSeries<?> seriesFrom(Transferable t) {
    MediaSeries<?> read = readIfSeries(t);
    return read != null ? read : lastDragged;
  }

  MediaSeries<?> readIfSeries(Transferable t) {
    if (t == null || !t.isDataFlavorSupported(SERIES_FLAVOR)) {
      return null;
    }
    return readSeries(t);
  }

  MediaSeries<?> readSeries(Transferable t) {
    try {
      Object data = t.getTransferData(SERIES_FLAVOR);
      return data instanceof MediaSeries<?> series ? series : null;
    } catch (Exception e) {
      return null;
    }
  }

  int importFilesFrom(Transferable t) {
    return 0;
  }

  public int importFiles(List<File> files) {
    if (files == null || files.isEmpty()) {
      return 0;
    }
    lastFiles = List.copyOf(files);
    return lastFiles.size();
  }

  public List<File> lastFiles() {
    return lastFiles;
  }

  public MediaSeries<?> lastSeries() {
    return lastSeries;
  }

  static final class SeriesDrop extends DropTargetAdapter {
    private final JComponent host;

    SeriesDrop(JComponent host) {
      this.host = host;
    }

    @Override
    public void dragEnter(DropTargetDragEvent e) {
      acceptIfDragging(e);
    }

    @Override
    public void dragOver(DropTargetDragEvent e) {
      rememberOver(e);
      acceptIfDragging(e);
    }

    void rememberOver(DropTargetDragEvent e) {
      if (host.isShowing() && takes(e)) {
        lastOver = toScreen(host, e.getLocation());
      }
    }

    @Override
    public void drop(DropTargetDropEvent e) {
      e.acceptDrop(dropAction(e));
      e.dropComplete(fill(e));
    }

    boolean fill(DropTargetDropEvent e) {
      ViewTransferHandler handler = new ViewTransferHandler();
      if (host.isShowing()) {
        return handler.hangAtScreen(toScreen(host, e.getLocation()));
      }
      return handler.importAt(host, e.getLocation(), e.getTransferable());
    }

    static int dropAction(DropTargetDropEvent e) {
      int action = e.getDropAction();
      return action == NONE ? COPY : action;
    }

    static void acceptIfDragging(DropTargetDragEvent e) {
      if (takes(e)) {
        e.acceptDrag(COPY_OR_MOVE);
      } else {
        e.rejectDrag();
      }
    }

    static boolean takes(DropTargetDragEvent e) {
      return dragged() != null
          || ImageTransferHandler.flavorIn(e.getCurrentDataFlavors(), SERIES_FLAVOR);
    }
  }

  static final class DragFill extends DragSourceAdapter
      implements DragSourceMotionListener, AWTEventListener {
    static final DragFill INSTANCE = new DragFill();
    private boolean armed;

    static void arm() {
      INSTANCE.armOnce();
    }

    void armOnce() {
      if (armed) {
        return;
      }
      armed = true;
      DragSource src = DragSource.getDefaultDragSource();
      src.addDragSourceListener(this);
      src.addDragSourceMotionListener(this);
      Toolkit.getDefaultToolkit()
          .addAWTEventListener(this, AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
    }

    @Override
    public void dragDropEnd(DragSourceDropEvent e) {
      if (dragged() == null) {
        return;
      }
      hangScreen(lastOver != null ? lastOver : dropPoint(e));
    }

    static Point dropPoint(DragSourceDropEvent e) {
      return e == null ? pointerLocation() : new Point(e.getX(), e.getY());
    }

    @Override
    public void dragMouseMoved(DragSourceDragEvent e) {
      if (e != null) {
        lastOver = new Point(e.getX(), e.getY());
      }
    }

    @Override
    public void eventDispatched(AWTEvent event) {
      if (event instanceof MouseEvent me) {
        onMouse(me);
      }
    }

    static void onMouse(MouseEvent me) {
      armPress(me);
      trackMove(me);
      if (releaseWhileDrag(me) != null) {
        hangRelease(me);
      }
    }

    static void armPress(MouseEvent me) {
      if (me.getID() != MouseEvent.MOUSE_PRESSED || !SwingUtilities.isLeftMouseButton(me)) {
        return;
      }
      if (me.getComponent() instanceof SeriesThumbnail thumb) {
        beginDrag(thumb.getSeries());
      }
    }

    static void trackMove(MouseEvent me) {
      if (dragging() != null && me.getID() == MouseEvent.MOUSE_DRAGGED) {
        lastOver = me.getLocationOnScreen();
      }
    }

    static void hangRelease(MouseEvent me) {
      Point screen = me.getLocationOnScreen();
      lastOver = screen;
      hangScreen(screen);
      endDrag();
    }

    static MouseEvent releaseWhileDrag(AWTEvent event) {
      if (dragging() == null || !(event instanceof MouseEvent me)) {
        return null;
      }
      return me.getID() == MouseEvent.MOUSE_RELEASED ? me : null;
    }
  }

  static final class SeriesSelection implements Transferable {
    private final MediaSeries<?> series;

    SeriesSelection(MediaSeries<?> series) {
      this.series = series;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
      return new DataFlavor[] {SERIES_FLAVOR, DataFlavor.stringFlavor};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
      return SERIES_FLAVOR.equals(flavor) || DataFlavor.stringFlavor.equals(flavor);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
      if (SERIES_FLAVOR.equals(flavor)) {
        return series;
      }
      if (DataFlavor.stringFlavor.equals(flavor)) {
        return String.valueOf(series);
      }
      throw new UnsupportedFlavorException(flavor);
    }
  }
}
