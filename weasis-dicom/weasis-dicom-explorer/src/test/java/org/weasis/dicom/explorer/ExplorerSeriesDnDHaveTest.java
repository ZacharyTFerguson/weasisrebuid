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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.List;
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

      ViewTransferHandler drop = (ViewTransferHandler) container.getTransferHandler();
      assertTrue(
          drop.canImport(
              container.getView2d(), new DataFlavor[] {ViewTransferHandler.SERIES_FLAVOR}));
      assertTrue(drop.dropSeries(container.getView2d(), thumb.getSeries()));
      assertEquals(1, core.getOpenViewerPlugins().size());
      assertSame(plugin, core.getOpenViewerPlugins().getFirst());
      assertEquals("2.25.dx.pa", seriesUid(container.getLayoutViews().get(0).getSeries()));
      assertEquals("2.25.dx.lat", seriesUid(container.getLayoutViews().get(1).getSeries()));
      assertNotEquals(
          seriesUid(container.getLayoutViews().get(0).getSeries()),
          seriesUid(container.getLayoutViews().get(1).getSeries()));
      assertSame(thumb.getSeries(), drop.lastSeries());
    } finally {
      HangingProtocolOpenHaveTest.close(core, factory);
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
