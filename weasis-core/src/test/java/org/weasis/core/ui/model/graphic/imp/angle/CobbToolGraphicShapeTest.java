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
 * org.weasis.core.ui.model.graphic.Graphic#getShape()} for landed Cobb graphics. Degree and mm
 * labels already come from {@link CobbToolGraphic#getCobbAngleDegrees()} and endplate length
 * helpers; without a path, the viewer can show formatted Cobb text but not the two endplate
 * segments the measurement refers to.
 *
 * <p>Why four handles in fixed order: handles 0 and 1 span the upper endplate, handles 2 and 3 the
 * lower. {@code buildShape()} draws each endplate as its own move/line pair in image pixel space so
 * paint lines up with the same handle geometry used for Cobb angle and endplate length.
 *
 * <p>Why fail-closed null shape: if any of the four handle positions is unavailable, painting a
 * partial or inferred endplate would misrepresent where the Cobb was taken. Clearing the shape
 * matches {@link org.weasis.core.ui.model.graphic.imp.line.LineGraphic#buildShape()} when
 * endpoints are missing.
 *
 * <p>Why not copy upstream {@code MeasureTool}: that stack mixes calibration, arc decoration, and
 * tool chrome. This rebuild keeps Cobb as four image-space handles on {@link CobbToolGraphic} with
 * plain segment paths — no measure-tool import, no interactive draw changes in this slice.
 */
class CobbToolGraphicShapeTest {

  @Test
  void fourHandlesBuildTwoEndplateSegments() {
    CobbToolGraphic cobb = parallelEndplates();
    Shape shape = cobb.getShape();
    assertNotNull(shape);
    assertEquals(
        List.of(
            new Point2D.Double(0, 0),
            new Point2D.Double(10, 0),
            new Point2D.Double(0, 20),
            new Point2D.Double(10, 20)),
        pathVertices(shape));
  }

  @Test
  void missingFourthHandleClearsShape() {
    CobbToolGraphic cobb = new CobbToolGraphic();
    cobb.setPts(
        List.of(
            new Point2D.Double(0, 0),
            new Point2D.Double(10, 0),
            new Point2D.Double(0, 20)));
    assertNull(cobb.getShape());
  }

  private static CobbToolGraphic parallelEndplates() {
    CobbToolGraphic cobb = new CobbToolGraphic();
    cobb.setHandlePoint(0, new Point2D.Double(0, 0));
    cobb.setHandlePoint(1, new Point2D.Double(10, 0));
    cobb.setHandlePoint(2, new Point2D.Double(0, 20));
    cobb.setHandlePoint(3, new Point2D.Double(10, 20));
    return cobb;
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
