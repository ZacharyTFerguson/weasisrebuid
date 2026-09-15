/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.utils.bean;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import org.weasis.core.ui.model.utils.algo.ConvexHull;
import org.weasis.core.ui.model.utils.algo.MinimumEnclosingRectangle;

/** Drawn shape plus its convex hull / enclosing rectangle. */
public class AdvancedShape {

  private final Shape shape;
  private final List<Point2D.Double> hull;

  public AdvancedShape(Shape shape, List<Point2D.Double> pts) {
    this.shape = shape;
    this.hull = ConvexHull.hull(pts);
  }

  public Shape getShape() {
    return shape;
  }

  public List<Point2D.Double> getHull() {
    return hull;
  }

  public Rectangle2D getBounds() {
    return MinimumEnclosingRectangle.of(hull);
  }
}
