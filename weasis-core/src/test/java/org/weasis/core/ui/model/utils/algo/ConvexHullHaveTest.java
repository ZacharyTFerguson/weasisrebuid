/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.utils.algo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.weasis.core.ui.model.graphic.imp.area.PolygonGraphic;
import org.weasis.core.ui.model.utils.bean.AdvancedShape;

class ConvexHullHaveTest {

  @Test
  void hullDropsInteriorPointAndMerContainsAll() {
    List<Point2D.Double> pts =
        List.of(
            new Point2D.Double(0, 0),
            new Point2D.Double(3, 0),
            new Point2D.Double(1, 1),
            new Point2D.Double(0, 3));
    List<Point2D.Double> hull = ConvexHull.hull(pts);
    assertEquals(3, hull.size());
    assertFalse(hull.stream().anyMatch(p -> p.getX() == 1.0 && p.getY() == 1.0));
    Rectangle2D box = MinimumEnclosingRectangle.of(pts);
    assertEquals(0.0, box.getX(), 1e-9);
    assertEquals(0.0, box.getY(), 1e-9);
    assertEquals(3.0, box.getMaxX(), 1e-9);
    assertEquals(3.0, box.getMaxY(), 1e-9);
    assertTrue(box.contains(1, 1));
    assertEquals(3.0, box.getWidth(), 1e-9);
    assertEquals(3.0, box.getHeight(), 1e-9);
  }

  @Test
  void polygonAdvancedShapeUsesHull() {
    PolygonGraphic poly = new PolygonGraphic();
    poly.setPts(
        List.of(
            new Point2D.Double(0, 0),
            new Point2D.Double(3, 0),
            new Point2D.Double(1, 1),
            new Point2D.Double(0, 3)));
    AdvancedShape adv = poly.advancedShape();
    assertNotNull(adv.getShape());
    assertEquals(3, adv.getHull().size());
    assertEquals(3.0, adv.getBounds().getWidth(), 1e-9);
    assertEquals(3.0, adv.getBounds().getHeight(), 1e-9);
  }
}
