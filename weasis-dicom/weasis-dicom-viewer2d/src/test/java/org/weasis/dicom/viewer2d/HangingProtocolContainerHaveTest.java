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
import static org.junit.jupiter.api.Assertions.assertSame;

import java.awt.GridLayout;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.api.media.data.Series;

class HangingProtocolContainerHaveTest {

  @Test
  void applyHangingUsesOneByTwoAndTwoByTwoGrid() {
    View2dContainer container = new View2dContainer();
    container.applyHanging(1, 2);
    assertEquals(2, container.getLayoutCount());
    GridLayout oneByTwo = (GridLayout) container.getViewGrid().getLayout();
    assertEquals(1, oneByTwo.getRows());
    assertEquals(2, oneByTwo.getColumns());
    container.applyHanging(2, 2);
    assertEquals(4, container.getLayoutCount());
    GridLayout twoByTwo = (GridLayout) container.getViewGrid().getLayout();
    assertEquals(2, twoByTwo.getRows());
    assertEquals(2, twoByTwo.getColumns());
    container.applyHanging(1, 1);
    assertEquals(1, container.getLayoutCount());
  }

  @Test
  void hangSeriesPutsDifferentSeriesInExtraCells() {
    View2dContainer container = new View2dContainer();
    Series<MediaElement> pa = series("2.25.dx.pa");
    Series<MediaElement> lat = series("2.25.dx.lat");
    container.applyHanging(1, 2);
    container.hangSeries(List.of(pa, lat));
    assertEquals(2, container.getLayoutCount());
    assertSame(pa, container.getLayoutViews().get(0).getSeries());
    assertSame(lat, container.getLayoutViews().get(1).getSeries());
    assertNotSame(
        container.getLayoutViews().get(0).getSeries(),
        container.getLayoutViews().get(1).getSeries());
    assertEquals(2, container.getOpenSeries().size());
  }

  @Test
  void secondAddSeriesFillsCloneSlotInsteadOfReplacingPrimary() {
    View2dContainer container = new View2dContainer();
    Series<MediaElement> pa = series("2.25.dx.pa");
    Series<MediaElement> lat = series("2.25.dx.lat");
    container.applyHanging(1, 2);
    container.addSeries(pa);
    assertSame(pa, container.getLayoutViews().get(0).getSeries());
    assertSame(pa, container.getLayoutViews().get(1).getSeries());
    container.addSeries(lat);
    assertSame(pa, container.getLayoutViews().get(0).getSeries());
    assertSame(lat, container.getLayoutViews().get(1).getSeries());
  }

  static Series<MediaElement> series(String uid) {
    Series<MediaElement> series = new Series<>(uid);
    series.addMedia(new MediaElement());
    return series;
  }
}
