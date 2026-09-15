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

import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
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

  private List<File> lastFiles = List.of();
  private MediaSeries<?> lastSeries;

  @Override
  public boolean canImport(JComponent comp, DataFlavor[] flavors) {
    return dragging != null || canImportFlavors(flavors);
  }

  @Override
  public boolean canImport(TransferSupport support) {
    return support != null && (dragging != null || canImportFlavors(support.getDataFlavors()));
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
  }

  public static void endDrag() {
    dragging = null;
  }

  public static MediaSeries<?> dragging() {
    return dragging;
  }

  @Override
  protected void exportDone(JComponent source, Transferable data, int action) {
    endDrag();
    super.exportDone(source, data, action);
  }

  public Transferable seriesTransferable(MediaSeries<?> series) {
    return new SeriesSelection(series);
  }

  @Override
  public boolean importData(JComponent comp, Transferable t) {
    return importTransferable(comp, t);
  }

  @Override
  public boolean importData(TransferSupport support) {
    if (support == null || !canImport(support)) {
      return false;
    }
    Component onto = cellOf(support);
    return importTransferable(onto, support.getTransferable());
  }

  Component cellOf(TransferSupport support) {
    Component host = support.getComponent();
    JComponent view = viewUnder(host);
    if (view != null) {
      return view;
    }
    if (!support.isDrop()) {
      return host;
    }
    return viewAt(host, support.getDropLocation().getDropPoint());
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
    Point inPlugin = SwingUtilities.convertPoint(host, p, plugin);
    JComponent cell = plugin.dropCellAt(inPlugin);
    return cell != null ? cell : plugin;
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

  boolean importTransferable(Component comp, Transferable t) {
    MediaSeries<?> series = seriesFrom(t);
    if (series == null) {
      series = dragging;
    }
    if (series != null && comp instanceof JComponent jc) {
      return dropSeries(jc, series);
    }
    return importFilesFrom(t) > 0;
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
    if (comp instanceof ImageViewerPlugin<?> plugin) {
      return plugin;
    }
    ImageViewerPlugin<?> fromProp = pluginProperty(comp);
    if (fromProp != null) {
      return fromProp;
    }
    return UICore.getInstance().getFocusedImagePlugin();
  }

  static ImageViewerPlugin<?> pluginProperty(JComponent comp) {
    Component c = comp;
    while (c instanceof JComponent jc) {
      Object host = jc.getClientProperty(ImageViewerPlugin.class);
      if (host instanceof ImageViewerPlugin<?> plugin) {
        return plugin;
      }
      c = jc.getParent();
    }
    return null;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  static void addToPlugin(ImageViewerPlugin<?> plugin, MediaSeries<?> series) {
    dropInto(plugin, plugin, series);
  }

  MediaSeries<?> seriesFrom(Transferable t) {
    if (t == null) {
      return null;
    }
    if (t.isDataFlavorSupported(SERIES_FLAVOR)) {
      return readSeries(t);
    }
    return dragging;
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
