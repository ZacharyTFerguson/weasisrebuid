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
import static org.junit.jupiter.api.Assertions.assertNull;

import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Why paint needs a shape: the 2D viewer stroke path uses {@link
 * org.weasis.core.ui.model.graphic.Graphic#getShape()} for landed graphics; angle measurement
 * labels can render from handle geometry while {@link AngleToolGraphic#buildShape()} still left
 * the arm path null, so nothing was stroked between handles.
 *
 * <p>Why two segments through the vertex: handle 1 is the included-angle vertex; arms are the
 * pixel segments from handle 0 into that vertex and from the vertex to handle 2 — same handle
 * order as draw and {@link AngleToolGraphicMeasureTest}, not a closed polygon or arc decoration.
 *
 * <p>Why fail-closed on missing handles: partial drafts (fewer than three stored points) must not
 * draw a misleading arm; null shape matches {@link
 * org.weasis.core.ui.model.graphic.imp.line.LineGraphic#buildShape()} and polyline/polygon guards.
 */
class AngleToolGraphicShapeTest {

  @Test
  void threeHandlesBuildTwoSegmentArmPath() {
    AngleToolGraphic angle = rightAngleAtOrigin();
    Shape shape = angle.getShape();
    assertNotNull(shape);
    List<Point2D.Double> path = pathVertices(shape);
    assertEquals(3, path.size());
    assertPoint(10, 0, path.get(0));
    assertPoint(0, 0, path.get(1));
    assertPoint(0, 10, path.get(2));
  }

  @Test
  void twoStoredPointsLeaveShapeNull() {
    AngleToolGraphic angle = new AngleToolGraphic();
    angle.setPts(List.of(new Point2D.Double(0, 0), new Point2D.Double(10, 0)));
    assertNull(angle.getShape());
  }

  private static AngleToolGraphic rightAngleAtOrigin() {
    AngleToolGraphic angle = new AngleToolGraphic();
    angle.setHandlePoint(0, new Point2D.Double(10, 0));
    angle.setHandlePoint(1, new Point2D.Double(0, 0));
    angle.setHandlePoint(2, new Point2D.Double(0, 10));
    return angle;
  }

  private static List<Point2D.Double> pathVertices(Shape shape) {
    List<Point2D.Double> out = new ArrayList<>();
    double[] coords = new double[6];
    for (PathIterator it = shape.getPathIterator(null); !it.isDone(); it.next()) {
      int type = it.currentSegment(coords);
      if (type == PathIterator.SEG_MOVETO || type == PathIterator.SEG_LINETO) {
        out.add(new Point2D.Double(coords[0], coords[1]));
      }
    }
    return out;
  }

  private static void assertPoint(double x, double y, Point2D.Double p) {
    assertEquals(x, p.x, 1e-9);
    assertEquals(y, p.y, 1e-9);
  }
}
