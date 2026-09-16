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
 * Why a stored shape: the 2D overlay paints {@link
 * org.weasis.core.ui.model.graphic.Graphic#getShape()} for landed graphics. Angle labels already
 * come from {@link AngleToolGraphic#getAngleDegrees()} and arm helpers; without a path, viewers
 * show numbers but no caliper arms.
 *
 * <p>Why three handles in fixed order: handle 1 is the vertex; handles 0 and 2 are the arm
 * endpoints the user drags. {@code buildShape()} connects 0 → vertex → 2 in image pixel space so
 * paint matches the same geometry used for measurement.
 *
 * <p>Why fail-closed null shape: if any of those three handle positions is unavailable, painting a
 * partial or guessed path would lie about where the angle was taken. Clearing the shape matches how
 * {@link org.weasis.core.ui.model.graphic.imp.line.LineGraphic#buildShape()} behaves when endpoints
 * are missing.
 *
 * <p>Why not copy upstream {@code MeasureTool}: that stack mixes calibration, arc decoration, and
 * tool chrome. This rebuild keeps arms as two plain segments on {@link AngleToolGraphic} — no arc,
 * no Cobb geometry, no measure-tool import.
 */
class AngleToolGraphicShapeTest {

  @Test
  void threeHandlesBuildTwoSegmentArmPath() {
    AngleToolGraphic angle = rightAngleAtOrigin();
    Shape shape = angle.getShape();
    assertNotNull(shape);
    assertEquals(
        List.of(new Point2D.Double(10, 0), new Point2D.Double(0, 0), new Point2D.Double(0, 10)),
        pathVertices(shape));
  }

  @Test
  void missingVertexHandleClearsShape() {
    AngleToolGraphic angle = new AngleToolGraphic();
    angle.setPts(List.of(new Point2D.Double(10, 0), new Point2D.Double(5, 5)));
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
    List<Point2D.Double> vertices = new ArrayList<>();
    double[] coords = new double[6];
    PathIterator iterator = shape.getPathIterator(null);
    while (!iterator.isDone()) {
      int segment = iterator.currentSegment(coords);
      if (segment == PathIterator.SEG_MOVETO || segment == PathIterator.SEG_LINETO) {
        vertices.add(new Point2D.Double(coords[0], coords[1]));
      }
      iterator.next();
    }
    return vertices;
  }
}
