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

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

/** Axis-aligned enclosing rectangle of a point set (via its convex hull). */
public class MinimumEnclosingRectangle {

  public static Rectangle2D of(List<Point2D.Double> pts) {
    return bounds(ConvexHull.hull(pts));
  }

  static Rectangle2D bounds(List<Point2D.Double> pts) {
    if (ConvexHull.missing(pts)) {
      return new Rectangle2D.Double();
    }
    double minX = pts.getFirst().getX();
    double minY = pts.getFirst().getY();
    double maxX = minX;
    double maxY = minY;
    for (Point2D.Double p : pts) {
      minX = Math.min(minX, p.getX());
      minY = Math.min(minY, p.getY());
      maxX = Math.max(maxX, p.getX());
      maxY = Math.max(maxY, p.getY());
    }
    return new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY);
  }
}
