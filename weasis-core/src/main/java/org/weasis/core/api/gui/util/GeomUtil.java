/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package org.weasis.core.api.gui.util;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public final class GeomUtil {
  private GeomUtil() {}

  public static double distance(Point2D a, Point2D b) {
    if (a == null || b == null) {
      return 0;
    }
    return a.distance(b);
  }

  public static double angle(Point2D a, Point2D vertex, Point2D b) {
    if (a == null || vertex == null || b == null) {
      return 0;
    }
    double a1 = Math.atan2(a.getY() - vertex.getY(), a.getX() - vertex.getX());
    double a2 = Math.atan2(b.getY() - vertex.getY(), b.getX() - vertex.getX());
    return Math.toDegrees(a2 - a1);
  }

  public static Point2D.Double mid(Point2D a, Point2D b) {
    if (a == null || b == null) {
      return new Point2D.Double();
    }
    return new Point2D.Double((a.getX() + b.getX()) / 2.0, (a.getY() + b.getY()) / 2.0);
  }

  public static Line2D.Double line(Point2D a, Point2D b) {
    return new Line2D.Double(a, b);
  }
}
