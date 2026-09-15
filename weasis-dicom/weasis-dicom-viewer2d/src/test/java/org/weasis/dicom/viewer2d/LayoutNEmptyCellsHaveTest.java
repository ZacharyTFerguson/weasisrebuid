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

import org.junit.jupiter.api.Test;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.api.media.data.Series;

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

  static Series<MediaElement> series(String uid) {
    Series<MediaElement> series = new Series<>(uid);
    series.addMedia(new MediaElement());
    return series;
  }
}
