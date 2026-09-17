/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import org.junit.jupiter.api.Test;
import org.weasis.core.ui.editor.image.DefaultView2d;
import org.weasis.core.ui.editor.image.ImageRegionStatistics;
import org.weasis.core.ui.model.graphic.imp.area.RectangleGraphic;

class ImageStatisticsHaveTest {

  @Test
  void viewImageStatisticsMatchSelectedRoi() {
    DefaultView2d<?> view = new DefaultView2d<>();
    view.setSourceImage(gray(new int[][] {{10, 200}, {10, 200}}));
    RectangleGraphic roi = new RectangleGraphic();
    roi.setHandlePoint(0, new Point2D.Double(0, 0));
    roi.setHandlePoint(1, new Point2D.Double(1, 2));
    roi.setSelected(true);
    view.addGraphic(roi);
    ImageRegionStatistics.Stats region = ImageRegionStatistics.compute(view);
    ImageStatistics stats = view.imageStatistics();
    assertEquals(2, stats.getSamples());
    assertEquals(10.0, stats.getMin(), 1e-9);
    assertEquals(10.0, stats.getMax(), 1e-9);
    assertEquals(region.getSamples(), stats.getSamples());
    assertEquals(region.getMin(), stats.getMin(), 1e-9);
    assertEquals(region.getMax(), stats.getMax(), 1e-9);
    assertEquals(region.getMean(), stats.getMean(), 1e-9);
    assertEquals(region.getStdev(), stats.getStdev(), 1e-9);
  }

  static BufferedImage gray(int[][] pixels) {
    BufferedImage image =
        new BufferedImage(pixels[0].length, pixels.length, BufferedImage.TYPE_BYTE_GRAY);
    for (int y = 0; y < pixels.length; y++) {
      for (int x = 0; x < pixels[y].length; x++) {
        image.getRaster().setSample(x, y, 0, pixels[y][x]);
      }
    }
    return image;
  }
}
