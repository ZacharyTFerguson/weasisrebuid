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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.api.media.data.Series;
import org.weasis.core.ui.editor.image.ImageViewerPlugin;
import org.weasis.core.ui.editor.image.ViewTransferHandler;

class LayoutNEmptyCellsHaveTest {

  @Test
  void layoutNExtraCellsStayEmptyNotClonesOfPrimary() {
    View2dContainer container = new View2dContainer();
    Series<MediaElement> pa = series("2.25.dx.pa");
    Series<MediaElement> lat = series("2.25.dx.lat");
    container.addSeries(pa);
    DicomView2dCommands cmd = new DicomView2dCommands(container.getView2d());
    assertEquals("layout -n 4", cmd.layout("-n", "4"));
    assertEquals(4, container.getLayoutCount());
    assertSame(pa, container.getLayoutViews().get(0).getSeries());
    assertNull(container.getLayoutViews().get(1).getSeries());
    assertNull(container.getLayoutViews().get(2).getSeries());
    assertNull(container.getLayoutViews().get(3).getSeries());
    container.addSeries(lat);
    assertSame(pa, container.getLayoutViews().get(0).getSeries());
    assertSame(lat, container.getLayoutViews().get(1).getSeries());
    assertNotSame(
        container.getLayoutViews().get(0).getSeries(),
        container.getLayoutViews().get(1).getSeries());
    assertNull(container.getLayoutViews().get(2).getSeries());
    assertNull(container.getLayoutViews().get(3).getSeries());
  }

  @Test
  void hangOneByTwoTwoDxStayDifferentAfterLayoutN() {
    View2dContainer container = new View2dContainer();
    Series<MediaElement> pa = series("2.25.dx.pa");
    Series<MediaElement> lat = series("2.25.dx.lat");
    container.applyHanging(1, 2);
    container.addSeries(pa);
    container.addSeries(lat);
    assertSame(pa, container.getLayoutViews().get(0).getSeries());
    assertSame(lat, container.getLayoutViews().get(1).getSeries());
    new DicomView2dCommands(container.getView2d()).layout("-n", "4");
    assertEquals(4, container.getLayoutCount());
    assertSame(pa, container.getLayoutViews().get(0).getSeries());
    assertSame(lat, container.getLayoutViews().get(1).getSeries());
    assertNotSame(
        container.getLayoutViews().get(0).getSeries(),
        container.getLayoutViews().get(1).getSeries());
    assertNull(container.getLayoutViews().get(2).getSeries());
    assertNull(container.getLayoutViews().get(3).getSeries());
  }

  @Test
  void layoutNStillGrowsWhenView2dClientPropertyMissing() {
    View2dContainer container = new View2dContainer();
    Series<MediaElement> pa = series("2.25.dx.pa");
    container.addSeries(pa);
    container.applyHanging(1, 2);
    container.addSeries(series("2.25.dx.lat"));
    container.getView2d().putClientProperty(View2dContainer.class, null);
    container.getView2d().putClientProperty(ImageViewerPlugin.class, null);
    assertEquals("layout -n 4", new DicomView2dCommands(container.getView2d()).layout("-n4"));
    assertEquals(4, container.getLayoutCount());
    assertSame(pa, container.getLayoutViews().get(0).getSeries());
    assertNull(container.getLayoutViews().get(2).getSeries());
    assertNull(container.getLayoutViews().get(3).getSeries());
  }

  @Test
  void seriesDragDoesNotWindowLevel() {
    View2d view = new View2d();
    view.setWindowLevel(400, 40);
    Series<MediaElement> series = series("2.25.dx.pa");
    ViewTransferHandler.beginDrag(series);
    try {
      view.getEventManager()
          .mousePressed(
              new MouseEvent(
                  view,
                  MouseEvent.MOUSE_PRESSED,
                  0L,
                  InputEvent.BUTTON1_DOWN_MASK,
                  10,
                  10,
                  1,
                  false,
                  MouseEvent.BUTTON1));
      view.getEventManager()
          .mouseDragged(
              new MouseEvent(
                  view,
                  MouseEvent.MOUSE_DRAGGED,
                  0L,
                  InputEvent.BUTTON1_DOWN_MASK,
                  10,
                  60,
                  1,
                  false));
      assertEquals(40.0, view.getLevel(), 1e-9);
      assertEquals(400.0, view.getWindow(), 1e-9);
    } finally {
      ViewTransferHandler.endDrag();
    }
  }

  static Series<MediaElement> series(String uid) {
    Series<MediaElement> series = new Series<>(uid);
    series.addMedia(new MediaElement());
    return series;
  }
}
