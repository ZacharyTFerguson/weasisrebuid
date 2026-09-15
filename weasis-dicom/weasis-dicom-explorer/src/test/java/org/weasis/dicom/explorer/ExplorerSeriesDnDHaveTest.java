/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import org.dcm4che3.data.UID;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.media.data.MediaSeries;
import org.weasis.core.api.media.data.SeriesThumbnail;
import org.weasis.core.api.media.data.TagW;
import org.weasis.core.api.service.UICore;
import org.weasis.core.ui.editor.image.ViewTransferHandler;
import org.weasis.core.ui.editor.image.ViewerPlugin;
import org.weasis.dicom.codec.DicomMime;
import org.weasis.dicom.explorer.main.SeriesPane;
import org.weasis.dicom.viewer2d.View2d;
import org.weasis.dicom.viewer2d.View2dContainer;
import org.weasis.dicom.viewer2d.View2dFactory;

class ExplorerSeriesDnDHaveTest {

  @Test
  void thumbnailSeriesDropFillsHangSlotWithoutNewTab() {
    UICore core = new UICore();
    View2dFactory factory = new View2dFactory();
    core.registerSeriesViewerFactory(factory);
    try {
      PluginOpeningStrategy opening = new PluginOpeningStrategy(core);
      ViewerPlugin<?> plugin = opening.open(dx("DX", "1", "2.25.dx.pa"));
      assertInstanceOf(View2dContainer.class, plugin);
      View2dContainer container = (View2dContainer) plugin;
      assertEquals(2, container.getLayoutCount());
      assertNull(container.getLayoutViews().get(1).getSeries());
      assertInstanceOf(ViewTransferHandler.class, container.getTransferHandler());
      assertInstanceOf(ViewTransferHandler.class, container.getView2d().getTransferHandler());

      SeriesPane pane = new SeriesPane();
      pane.showThumbnails(List.of(dx("DX", "1", "2.25.dx.lat")));
      SeriesThumbnail thumb = pane.thumbnails().getFirst();
      ViewTransferHandler drag = (ViewTransferHandler) thumb.getTransferHandler();
      assertEquals(TransferHandler.COPY, drag.getSourceActions(thumb));
      Transferable transferable = drag.seriesTransferable(thumb.getSeries());
      assertTrue(transferable.isDataFlavorSupported(ViewTransferHandler.SERIES_FLAVOR));

      View2d empty = container.getLayoutViews().get(1);
      ViewTransferHandler drop = (ViewTransferHandler) empty.getTransferHandler();
      assertTrue(drop.canImport(empty, new DataFlavor[] {ViewTransferHandler.SERIES_FLAVOR}));
      assertTrue(drop.importData(new TransferHandler.TransferSupport(empty, transferable)));
      assertTrue(drop.importData(empty, transferable));
      assertEquals(1, core.getOpenViewerPlugins().size());
      assertSame(plugin, core.getOpenViewerPlugins().getFirst());
      assertEquals("2.25.dx.pa", seriesUid(container.getLayoutViews().get(0).getSeries()));
      assertEquals("2.25.dx.lat", seriesUid(empty.getSeries()));
      assertNotEquals(
          seriesUid(container.getLayoutViews().get(0).getSeries()), seriesUid(empty.getSeries()));
      assertSame(thumb.getSeries(), drop.lastSeries());
      assertEquals(1, dragExports(thumb));
    } finally {
      HangingProtocolOpenHaveTest.close(core, factory);
    }
  }

  @Test
  void emptyNativeFlavorsFill2x2BottomLeftWithoutNewTab() {
    UICore core = new UICore();
    View2dFactory factory = new View2dFactory();
    core.registerSeriesViewerFactory(factory);
    try {
      PluginOpeningStrategy opening = new PluginOpeningStrategy(core);
      View2dContainer container = (View2dContainer) opening.open(dx("DX", "1", "2.25.dx.pa"));
      container.setLayoutCount(4);
      layoutPlugin(container);
      View2d bottomLeft = container.getLayoutViews().get(2);
      assertNull(bottomLeft.getSeries());
      assertNull(container.getLayoutViews().get(1).getSeries());

      SeriesPane pane = new SeriesPane();
      pane.showThumbnails(List.of(dx("DX", "1", "2.25.dx.lat")));
      SeriesThumbnail thumb = pane.thumbnails().getFirst();
      Transferable transferable = new ViewTransferHandler().seriesTransferable(thumb.getSeries());
      ViewTransferHandler drop = (ViewTransferHandler) bottomLeft.getTransferHandler();
      ViewTransferHandler.beginDrag(thumb.getSeries());
      try {
        assertTrue(drop.canImport(bottomLeft, new DataFlavor[0]));
        assertSame(bottomLeft, container.dropCellAt(cellCenter(container, 2)));
        assertTrue(drop.importData(bottomLeft, transferable));
      } finally {
        ViewTransferHandler.endDrag();
      }
      assertEquals(1, core.getOpenViewerPlugins().size());
      assertEquals("2.25.dx.pa", seriesUid(container.getLayoutViews().get(0).getSeries()));
      assertNull(container.getLayoutViews().get(1).getSeries());
      assertEquals("2.25.dx.lat", seriesUid(bottomLeft.getSeries()));
      assertNull(container.getLayoutViews().get(3).getSeries());
    } finally {
      HangingProtocolOpenHaveTest.close(core, factory);
    }
  }

  static void layoutPlugin(View2dContainer container) {
    container.setSize(480, 400);
    container.doLayout();
    List<View2d> views = container.getLayoutViews();
    int w = 200;
    int h = 160;
    for (int i = 0; i < views.size(); i++) {
      views.get(i).setBounds((i % 2) * w, (i / 2) * h, w, h);
    }
  }

  static Point cellCenter(View2dContainer container, int index) {
    View2d cell = container.getLayoutViews().get(index);
    Rectangle b = cell.getBounds();
    Point mid = new Point(b.x + b.width / 2, b.y + b.height / 2);
    if (cell.getParent() == null) {
      return SwingUtilities.convertPoint(cell, new Point(b.width / 2, b.height / 2), container);
    }
    return SwingUtilities.convertPoint(cell.getParent(), mid, container);
  }

  static int dragExports(SeriesThumbnail thumb) {
    CountExport handler = new CountExport();
    thumb.setTransferHandler(handler);
    int mods = InputEvent.BUTTON1_DOWN_MASK;
    thumb.dispatchEvent(new MouseEvent(thumb, MouseEvent.MOUSE_PRESSED, 0L, mods, 2, 2, 1, false));
    thumb.dispatchEvent(new MouseEvent(thumb, MouseEvent.MOUSE_DRAGGED, 0L, mods, 8, 8, 1, false));
    thumb.dispatchEvent(new MouseEvent(thumb, MouseEvent.MOUSE_DRAGGED, 0L, mods, 16, 8, 1, false));
    return handler.exports;
  }

  static final class CountExport extends ViewTransferHandler {
    int exports;

    @Override
    public void exportAsDrag(javax.swing.JComponent c, InputEvent e, int action) {
      exports++;
    }
  }

  static String seriesUid(MediaSeries<?> series) {
    Object v = series == null ? null : series.getTagValue(TagW.SeriesInstanceUID);
    return v == null ? "" : v.toString();
  }

  static ImportedInstance dx(String name, String id, String seriesUid) {
    return new ImportedInstance(
        "SYNTHETIC^" + name,
        "SYN-" + id,
        "2.25." + id + ".study",
        seriesUid,
        seriesUid + ".1",
        UID.DigitalXRayImageStorageForPresentation,
        "DX",
        seriesUid,
        "20260101",
        1,
        1,
        null,
        DicomMime.IMAGE_DICOM);
  }
}
