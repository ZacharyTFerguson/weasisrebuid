/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.graphic.imp.angle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.awt.geom.Point2D;
import org.junit.jupiter.api.Test;
import org.weasis.core.ui.editor.image.dockable.MeasureTool;
import org.weasis.core.ui.model.graphic.imp.area.ThreePointsCircleGraphic;

class AngleToolGraphicTest {

  @Test
  void rightAngleAtVertex() {
    AngleToolGraphic g = new AngleToolGraphic();
    g.setHandlePoint(0, new Point2D.Double(1, 0));
    g.setHandlePoint(1, new Point2D.Double(0, 0));
    g.setHandlePoint(2, new Point2D.Double(0, 1));
    assertEquals(90.0, g.getAngleDegrees(), 1e-6);
    assertNotNull(g.getShape());
  }

  @Test
  void cobbPerpendicularSegments() {
    CobbAngleToolGraphic g = new CobbAngleToolGraphic();
    g.setHandlePoint(0, new Point2D.Double(0, 0));
    g.setHandlePoint(1, new Point2D.Double(1, 0));
    g.setHandlePoint(2, new Point2D.Double(0, 0));
    g.setHandlePoint(3, new Point2D.Double(0, 1));
    assertEquals(90.0, g.getAngleDegrees(), 1e-6);
    assertNotNull(g.getShape());
  }

  @Test
  void threePointCircleUnitCircumradius() {
    ThreePointsCircleGraphic c = new ThreePointsCircleGraphic();
    c.setHandlePoint(0, new Point2D.Double(1, 0));
    c.setHandlePoint(1, new Point2D.Double(-1, 0));
    c.setHandlePoint(2, new Point2D.Double(0, 1));
    assertEquals(0.0, c.getCenter().x, 1e-9);
    assertEquals(0.0, c.getCenter().y, 1e-9);
    assertEquals(1.0, c.getRadius(), 1e-9);
    assertNotNull(c.getShape());
  }

  @Test
  void shortcutDCreatesDistance() {
    assertEquals(
        org.weasis.core.ui.model.graphic.imp.line.LineGraphic.class,
        MeasureTool.create("D").getClass());
    assertEquals(AngleToolGraphic.class, MeasureTool.create("A").getClass());
  }
}
