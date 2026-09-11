/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.graphic.imp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Point2D;
import org.junit.jupiter.api.Test;
import org.weasis.core.ui.model.graphic.DragGraphic;

class PointGraphicTest {

  @Test
  void oneHandlePointAndCopy() {
    PointGraphic g = new PointGraphic();
    assertEquals(1, g.getPtsNumber());
    assertInstanceOf(DragGraphic.class, g);
    g.setHandlePoint(0, new Point2D.Double(4, 8));
    assertEquals(4, g.getHandlePoint(0).x, 1e-9);
    PointGraphic copy = (PointGraphic) g.copy();
    assertNotSame(g, copy);
    assertEquals(8, copy.getHandlePoint(0).y, 1e-9);
    g.setSelected(true);
    assertTrue(g.getSelected());
  }
}
