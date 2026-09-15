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
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.TransferHandler;
import org.weasis.core.api.media.data.MediaSeries;
import org.weasis.core.api.media.data.SeriesThumbnail;
import org.weasis.core.ui.util.UriListFlavor;

/**
 * Drop files / URI-list / explorer series onto a view. Explorer thumbnails drag {@link MediaSeries}
 * onto the selected {@link ImageViewerPlugin} (hang slot, not a new tab).
 */
public class ViewTransferHandler extends TransferHandler {

  public static final DataFlavor SERIES_FLAVOR = new DataFlavor(MediaSeries.class, "MediaSeries");

  private List<File> lastFiles = List.of();
  private MediaSeries<?> lastSeries;

  @Override
  public boolean canImport(JComponent comp, DataFlavor[] flavors) {
    return canImportFlavors(flavors);
  }

  @Override
  public boolean canImport(TransferSupport support) {
    return support != null && canImportFlavors(support.getDataFlavors());
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
      return seriesTransferable(thumb.getSeries());
    }
    return super.createTransferable(c);
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
    return importTransferable(support.getComponent(), support.getTransferable());
  }

  boolean importTransferable(Component comp, Transferable t) {
    MediaSeries<?> series = seriesFrom(t);
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
    return pluginProperty(comp);
  }

  static ImageViewerPlugin<?> pluginProperty(JComponent comp) {
    if (comp == null) {
      return null;
    }
    Object host = comp.getClientProperty(ImageViewerPlugin.class);
    return host instanceof ImageViewerPlugin<?> plugin ? plugin : null;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  static void addToPlugin(ImageViewerPlugin<?> plugin, MediaSeries<?> series) {
    dropInto(plugin, plugin, series);
  }

  MediaSeries<?> seriesFrom(Transferable t) {
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

  static final class SeriesSelection implements Transferable {
    private final MediaSeries<?> series;

    SeriesSelection(MediaSeries<?> series) {
      this.series = series;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
      return new DataFlavor[] {SERIES_FLAVOR};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
      return SERIES_FLAVOR.equals(flavor);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
      if (!isDataFlavorSupported(flavor)) {
        throw new UnsupportedFlavorException(flavor);
      }
      return series;
    }
  }
}
