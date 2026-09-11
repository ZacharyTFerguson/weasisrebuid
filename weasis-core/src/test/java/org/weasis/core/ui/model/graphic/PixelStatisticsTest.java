/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.graphic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;
import org.junit.jupiter.api.Test;

class PixelStatisticsTest {

  @Test
  void closedShapeStats() {
    BufferedImage img = new BufferedImage(2, 2, BufferedImage.TYPE_BYTE_GRAY);
    img.getRaster().setSample(0, 0, 0, 10);
    img.getRaster().setSample(1, 0, 0, 20);
    img.getRaster().setSample(0, 1, 0, 30);
    img.getRaster().setSample(1, 1, 0, 40);
    PixelStatistics.Result r = PixelStatistics.of(img, new Rectangle2D.Double(0, 0, 2, 2));
    assertEquals(4, r.pixels());
    assertEquals(10, r.min(), 1e-9);
    assertEquals(40, r.max(), 1e-9);
    assertEquals(25, r.mean(), 1e-9);
    assertEquals(25, r.median(), 1e-9);
    assertTrue(r.stdev() > 0);
  }

  @Test
  void medianAndEntropyOfList() {
    PixelStatistics.Result r = PixelStatistics.of(List.of(1.0, 2.0, 2.0, 3.0));
    assertEquals(2.0, r.median(), 1e-9);
    assertTrue(r.entropy() > 0);
  }
}
