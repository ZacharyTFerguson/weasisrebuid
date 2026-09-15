/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.utils.imp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import org.junit.jupiter.api.Test;

class DefaultGraphicLabelHaveTest {

  @Test
  void paintDrawsMeasurementTextAndBoundsCoverTheInk() {
    DefaultGraphicLabel label = new DefaultGraphicLabel();
    label.setLabels(new String[] {"12.0 mm"});
    label.setOffset(4, 2);
    BufferedImage image = new BufferedImage(120, 40, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = image.createGraphics();
    g.setColor(Color.BLACK);
    g.fillRect(0, 0, 120, 40);
    g.setColor(Color.YELLOW);
    label.paint(g, 8, 6);
    Rectangle2D box = label.bounds(g, 8, 6);
    assertTrue(box.getWidth() > 20);
    assertTrue(box.getHeight() > 8);
    assertTrue(label.contains(box.getCenterX(), box.getCenterY(), g, 8, 6));
    assertFalse(label.contains(0, 0, g, 8, 6));
    assertTrue(nonBlackPixels(image) > 8);
    g.dispose();
  }

  @Test
  void copyAndMoveKeepLabelsIndependentOfSourceArray() {
    String[] src = {"a", "b"};
    DefaultGraphicLabel label = new DefaultGraphicLabel();
    label.setLabels(src);
    src[0] = "z";
    assertEquals("a", label.getLabels()[0]);
    DefaultGraphicLabel copy = label.copy();
    copy.move(3, 5);
    assertEquals(3.0, copy.getOffsetX(), 1e-9);
    assertEquals(5.0, copy.getOffsetY(), 1e-9);
    assertEquals(0.0, label.getOffsetX(), 1e-9);
    copy.getLabels()[0] = "mut";
    assertEquals("a", label.getLabels()[0]);
    assertEquals("a", copy.getLabels()[0]);
    label.setLabels(null);
    assertEquals(0, label.getLabels().length);
    assertNotEquals(copy.getLabels().length, 0);
  }

  static int nonBlackPixels(BufferedImage image) {
    int n = 0;
    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        if ((image.getRGB(x, y) & 0xFFFFFF) != 0) {
          n++;
        }
      }
    }
    return n;
  }
}
