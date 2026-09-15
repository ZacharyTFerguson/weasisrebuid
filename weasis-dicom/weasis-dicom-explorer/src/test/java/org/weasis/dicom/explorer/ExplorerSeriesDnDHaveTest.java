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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragSource;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import org.dcm4che3.data.UID;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.api.media.data.MediaSeries;
import org.weasis.core.api.media.data.Series;
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
      assertTrue(transferable.isDataFlavorSupported(DataFlavor.stringFlavor));

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
      assertEquals(1, core.getOpenViewerPlugins().size());
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

  @Test
  void glassHostDropFills2x2BottomLeftWithoutNewTab() {
    UICore core = new UICore();
    View2dFactory factory = new View2dFactory();
    core.registerSeriesViewerFactory(factory);
    try {
      PluginOpeningStrategy opening = new PluginOpeningStrategy(core);
      View2dContainer container = (View2dContainer) opening.open(dx("DX", "1", "2.25.dx.pa"));
      container.setLayoutCount(4);
      layoutPlugin(container);
      JPanel glass = hostOver(container);
      View2d bottomLeft = container.getLayoutViews().get(2);
      assertNull(bottomLeft.getSeries());

      SeriesPane pane = new SeriesPane();
      pane.showThumbnails(List.of(dx("DX", "1", "2.25.dx.lat")));
      SeriesThumbnail thumb = pane.thumbnails().getFirst();
      Transferable transferable = new ViewTransferHandler().seriesTransferable(thumb.getSeries());
      ViewTransferHandler drop = new ViewTransferHandler();
      ViewTransferHandler.beginDrag(thumb.getSeries());
      try {
        assertTrue(drop.importAt(glass, overCell(container, glass, 2), transferable));
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

  @Test
  void alreadyHungSeriesFillsEmptyBottomLeftCell() {
    UICore core = new UICore();
    View2dFactory factory = new View2dFactory();
    core.registerSeriesViewerFactory(factory);
    try {
      PluginOpeningStrategy opening = new PluginOpeningStrategy(core);
      View2dContainer container = (View2dContainer) opening.open(dx("DX", "1", "2.25.dx.pa"));
      container.setLayoutCount(4);
      layoutPlugin(container);
      JPanel glass = hostOver(container);
      MediaSeries<?> chest = container.getLayoutViews().get(0).getSeries();
      View2d bottomLeft = container.getLayoutViews().get(2);
      assertNull(bottomLeft.getSeries());
      Transferable transferable = new ViewTransferHandler().seriesTransferable(chest);
      ViewTransferHandler drop = new ViewTransferHandler();
      ViewTransferHandler.beginDrag(chest);
      try {
        assertTrue(drop.importAt(glass, overCell(container, glass, 2), transferable));
      } finally {
        ViewTransferHandler.endDrag();
      }
      assertEquals(1, core.getOpenViewerPlugins().size());
      assertEquals("2.25.dx.pa", seriesUid(container.getLayoutViews().get(0).getSeries()));
      assertEquals("2.25.dx.pa", seriesUid(bottomLeft.getSeries()));
      assertNull(container.getLayoutViews().get(1).getSeries());
    } finally {
      HangingProtocolOpenHaveTest.close(core, factory);
    }
  }

  @Test
  void lastDraggedSurvivesExportDoneAndFillsBottomLeft() {
    UICore core = new UICore();
    View2dFactory factory = new View2dFactory();
    core.registerSeriesViewerFactory(factory);
    try {
      PluginOpeningStrategy opening = new PluginOpeningStrategy(core);
      View2dContainer container = (View2dContainer) opening.open(dx("DX", "1", "2.25.dx.pa"));
      container.setLayoutCount(4);
      layoutPlugin(container);
      JPanel glass = hostOver(container);
      MediaSeries<?> chest = container.getLayoutViews().get(0).getSeries();
      View2d bottomLeft = container.getLayoutViews().get(2);
      ViewTransferHandler drop = new ViewTransferHandler();
      ViewTransferHandler.beginDrag(chest);
      ViewTransferHandler.endDrag();
      assertNull(ViewTransferHandler.dragging());
      assertSame(chest, ViewTransferHandler.lastDragged());
      assertTrue(drop.canImport(glass, new DataFlavor[0]));
      assertTrue(drop.importAt(glass, overCell(container, glass, 2), stringOnly(chest)));
      assertEquals(1, core.getOpenViewerPlugins().size());
      assertEquals("2.25.dx.pa", seriesUid(container.getLayoutViews().get(0).getSeries()));
      assertEquals("2.25.dx.pa", seriesUid(bottomLeft.getSeries()));
      assertNull(container.getLayoutViews().get(1).getSeries());
    } finally {
      HangingProtocolOpenHaveTest.close(core, factory);
    }
  }

  @Test
  void explorerSeriesWithoutUriCopiesPaintedImageOntoBottomLeft() {
    UICore core = new UICore();
    View2dFactory factory = new View2dFactory();
    core.registerSeriesViewerFactory(factory);
    try {
      PluginOpeningStrategy opening = new PluginOpeningStrategy(core);
      View2dContainer container = (View2dContainer) opening.open(dx("DX", "1", "2.25.dx.pa"));
      container.setLayoutCount(4);
      layoutPlugin(container);
      JPanel glass = hostOver(container);
      View2d primary = container.getLayoutViews().get(0);
      BufferedImage img = new BufferedImage(4, 4, BufferedImage.TYPE_BYTE_GRAY);
      primary.setSourceImage(img);
      View2d bottomLeft = container.getLayoutViews().get(2);
      Series<MediaElement> explorer = new Series<>(seriesUid(primary.getSeries()));
      explorer.addMedia(new MediaElement());
      ViewTransferHandler drop = new ViewTransferHandler();
      ViewTransferHandler.beginDrag(explorer);
      try {
        assertTrue(
            drop.importAt(glass, overCell(container, glass, 2), drop.seriesTransferable(explorer)));
      } finally {
        ViewTransferHandler.endDrag();
      }
      assertSame(img, bottomLeft.getSourceImage());
      assertEquals("2.25.dx.pa", seriesUid(bottomLeft.getSeries()));
    } finally {
      HangingProtocolOpenHaveTest.close(core, factory);
    }
  }

  @Test
  void overAtIsUsedByHangAtPointer() {
    Point screen = new Point(12, 34);
    ViewTransferHandler.overAt(screen);
    assertEquals(screen, ViewTransferHandler.lastOver());
    ViewTransferHandler.clearDragged();
  }

  @Test
  void missedDropPointFillsFirstEmptyExtraNotAlreadyHungNoop() {
    UICore core = new UICore();
    View2dFactory factory = new View2dFactory();
    core.registerSeriesViewerFactory(factory);
    try {
      PluginOpeningStrategy opening = new PluginOpeningStrategy(core);
      View2dContainer container = (View2dContainer) opening.open(dx("DX", "1", "2.25.dx.pa"));
      container.setLayoutCount(4);
      layoutPlugin(container);
      MediaSeries<?> chest = container.getLayoutViews().get(0).getSeries();
      ViewTransferHandler drop = new ViewTransferHandler();
      ViewTransferHandler.beginDrag(chest);
      try {
        assertTrue(drop.importAt(container, new Point(470, 390), stringOnly(chest)));
      } finally {
        ViewTransferHandler.endDrag();
      }
      assertEquals(1, core.getOpenViewerPlugins().size());
      assertEquals("2.25.dx.pa", seriesUid(container.getLayoutViews().get(0).getSeries()));
      assertEquals("2.25.dx.pa", seriesUid(container.getLayoutViews().get(1).getSeries()));
      assertNull(container.getLayoutViews().get(2).getSeries());
    } finally {
      HangingProtocolOpenHaveTest.close(core, factory);
    }
  }

  @Test
  void dropCellAtFindsBottomLeftThroughToolbarInsets() {
    UICore core = new UICore();
    View2dFactory factory = new View2dFactory();
    core.registerSeriesViewerFactory(factory);
    try {
      PluginOpeningStrategy opening = new PluginOpeningStrategy(core);
      View2dContainer container = (View2dContainer) opening.open(dx("DX", "1", "2.25.dx.pa"));
      container.setLayoutCount(4);
      layoutPlugin(container);
      container.setBorder(javax.swing.BorderFactory.createEmptyBorder(48, 12, 8, 8));
      container.doLayout();
      View2d bottomLeft = container.getLayoutViews().get(2);
      assertSame(bottomLeft, container.dropCellAt(cellCenter(container, 2)));
    } finally {
      HangingProtocolOpenHaveTest.close(core, factory);
    }
  }

  static JPanel hostOver(View2dContainer container) {
    JPanel glass = new JPanel(null);
    container.setBounds(0, 0, 480, 400);
    glass.add(container);
    glass.setSize(480, 400);
    return glass;
  }

  static Point overCell(View2dContainer container, JPanel glass, int index) {
    return SwingUtilities.convertPoint(container, cellCenter(container, index), glass);
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

  @Test
  void thumbMouseReleasedHangsAtScreenWithoutThrowing() {
    SeriesPane pane = new SeriesPane();
    pane.showThumbnails(List.of(dx("DX", "1", "2.25.dx.lat")));
    SeriesThumbnail thumb = pane.thumbnails().getFirst();
    ViewTransferHandler.beginDrag(thumb.getSeries());
    thumb.dispatchEvent(
        new MouseEvent(
            thumb,
            MouseEvent.MOUSE_RELEASED,
            0L,
            InputEvent.BUTTON1_DOWN_MASK,
            0,
            0,
            40,
            40,
            1,
            false,
            MouseEvent.BUTTON1));
    ViewTransferHandler.endDrag();
  }

  @Test
  void leftPressBeginsSeriesDragWithoutExportAsDrag() {
    SeriesPane pane = new SeriesPane();
    pane.showThumbnails(List.of(dx("DX", "1", "2.25.dx.lat")));
    SeriesThumbnail thumb = pane.thumbnails().getFirst();
    thumb.dispatchEvent(
        new MouseEvent(
            thumb, MouseEvent.MOUSE_PRESSED, 0L, InputEvent.BUTTON1_DOWN_MASK, 0, 0, 1, false,
            MouseEvent.BUTTON1));
    assertNotNull(ViewTransferHandler.dragging());
    assertSame(thumb.getSeries(), ViewTransferHandler.lastDragged());
    ViewTransferHandler.clearDragged();
  }

  @Test
  void leftDragBelowThresholdDoesNotExport() {
    SeriesPane pane = new SeriesPane();
    pane.showThumbnails(List.of(dx("DX", "1", "2.25.dx.lat")));
    SeriesThumbnail thumb = pane.thumbnails().getFirst();
    int shy = Math.max(0, DragSource.getDragThreshold() - 1);
    if (DragSource.getDragThreshold() > 0) {
      CountExport below =
          dragAt(thumb, InputEvent.BUTTON1_DOWN_MASK, MouseEvent.BUTTON1, shy, 0, shy, 0);
      assertEquals(0, below.exports);
      assertNotNull(ViewTransferHandler.dragging());
      ViewTransferHandler.endDrag();
    }
  }

  @Test
  void leftDragAboveThresholdBeginsSeriesDragWithoutExportAsDrag() {
    SeriesPane pane = new SeriesPane();
    pane.showThumbnails(List.of(dx("DX", "1", "2.25.dx.lat")));
    SeriesThumbnail thumb = pane.thumbnails().getFirst();
    CountExport once =
        dragAt(
            thumb,
            InputEvent.BUTTON1_DOWN_MASK,
            MouseEvent.BUTTON1,
            far(),
            far(),
            far() + 8,
            far());
    assertEquals(0, once.exports);
    assertNotNull(ViewTransferHandler.dragging());
    ViewTransferHandler.endDrag();
  }

  @Test
  void awtReleaseWhileDraggingHangsAtEventScreenAndEndsDrag() {
    SeriesPane pane = new SeriesPane();
    pane.showThumbnails(List.of(dx("DX", "1", "2.25.dx.lat")));
    SeriesThumbnail thumb = pane.thumbnails().getFirst();
    ViewTransferHandler.beginDrag(thumb.getSeries());
    MouseEvent release =
        new MouseEvent(
            thumb, MouseEvent.MOUSE_RELEASED, 0L, 0, 1, 1, 80, 120, 1, false, MouseEvent.BUTTON1);
    ViewTransferHandler.hangFromAwt(release);
    assertEquals(80, ViewTransferHandler.lastOver().x);
    assertEquals(120, ViewTransferHandler.lastOver().y);
    assertNull(ViewTransferHandler.dragging());
    ViewTransferHandler.clearDragged();
  }

  @Test
  void nonLeftPressDoesNotExport() {
    SeriesPane pane = new SeriesPane();
    pane.showThumbnails(List.of(dx("DX", "1", "2.25.dx.lat")));
    SeriesThumbnail thumb = pane.thumbnails().getFirst();
    CountExport right =
        dragAt(
            thumb,
            InputEvent.BUTTON3_DOWN_MASK,
            MouseEvent.BUTTON3,
            far(),
            far(),
            far() + 8,
            far());
    assertEquals(0, right.exports);
    ViewTransferHandler.clearDragged();
  }

  static CountExport dragAt(
      SeriesThumbnail thumb, int mods, int button, int x1, int y1, int x2, int y2) {
    CountExport handler = new CountExport();
    thumb.setTransferHandler(handler);
    thumb.dispatchEvent(
        new MouseEvent(thumb, MouseEvent.MOUSE_PRESSED, 0L, mods, 0, 0, 1, false, button));
    thumb.dispatchEvent(
        new MouseEvent(thumb, MouseEvent.MOUSE_DRAGGED, 0L, mods, x1, y1, 1, false));
    thumb.dispatchEvent(
        new MouseEvent(thumb, MouseEvent.MOUSE_DRAGGED, 0L, mods, x2, y2, 1, false));
    return handler;
  }

  static int far() {
    return DragSource.getDragThreshold() + 4;
  }

  static final class CountExport extends ViewTransferHandler {
    int exports;
    InputEvent last;

    @Override
    public void exportAsDrag(javax.swing.JComponent c, InputEvent e, int action) {
      exports++;
      last = e;
    }
  }

  static String seriesUid(MediaSeries<?> series) {
    Object v = series == null ? null : series.getTagValue(TagW.SeriesInstanceUID);
    return v == null ? "" : v.toString();
  }

  static Transferable stringOnly(MediaSeries<?> series) {
    return new StringOnly(series);
  }

  static final class StringOnly implements Transferable {
    private final MediaSeries<?> series;

    StringOnly(MediaSeries<?> series) {
      this.series = series;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
      return new DataFlavor[] {DataFlavor.stringFlavor};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
      return DataFlavor.stringFlavor.equals(flavor);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) {
      return String.valueOf(series);
    }
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
