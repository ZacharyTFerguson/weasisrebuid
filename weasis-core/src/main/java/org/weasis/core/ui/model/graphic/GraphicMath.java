/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.graphic;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/** Geometry helpers for ARCHITECTURE §5.2 / SRS §4.4 graphics (clean-room). */
public final class GraphicMath {

  private GraphicMath() {}

  public static double length(Point2D a, Point2D b) {
    if (a == null || b == null) {
      return 0;
    }
    return a.distance(b);
  }

  public static double angleDeg(Point2D a, Point2D vertex, Point2D b) {
    if (a == null || vertex == null || b == null) {
      return 0;
    }
    double v1x = a.getX() - vertex.getX();
    double v1y = a.getY() - vertex.getY();
    double v2x = b.getX() - vertex.getX();
    double v2y = b.getY() - vertex.getY();
    double mag = Math.hypot(v1x, v1y) * Math.hypot(v2x, v2y);
    if (mag == 0) {
      return 0;
    }
    double cos = Math.max(-1, Math.min(1, (v1x * v2x + v1y * v2y) / mag));
    return Math.toDegrees(Math.acos(cos));
  }

  /** Angle between directed segments ab and cd, 0–180. */
  public static double lineAngleDeg(Point2D a, Point2D b, Point2D c, Point2D d) {
    if (a == null || b == null || c == null || d == null) {
      return 0;
    }
    double v1x = b.getX() - a.getX();
    double v1y = b.getY() - a.getY();
    double v2x = d.getX() - c.getX();
    double v2y = d.getY() - c.getY();
    double mag = Math.hypot(v1x, v1y) * Math.hypot(v2x, v2y);
    if (mag == 0) {
      return 0;
    }
    double cos = Math.max(-1, Math.min(1, (v1x * v2x + v1y * v2y) / mag));
    return Math.toDegrees(Math.acos(cos));
  }

  /** Cobb: smaller angle between two lines (0–90). */
  public static double cobbDeg(Point2D a, Point2D b, Point2D c, Point2D d) {
    double ang = lineAngleDeg(a, b, c, d);
    if (ang > 90) {
      ang = 180 - ang;
    }
    return ang;
  }

  public static boolean nearlyParallel(Point2D a, Point2D b, Point2D c, Point2D d, double tolDeg) {
    double ang = cobbDeg(a, b, c, d);
    return ang <= tolDeg;
  }

  public static boolean nearlyPerpendicular(
      Point2D a, Point2D b, Point2D c, Point2D d, double tolDeg) {
    return Math.abs(cobbDeg(a, b, c, d) - 90) <= tolDeg;
  }

  public static Ellipse2D.Double circumcircle(Point2D a, Point2D b, Point2D c) {
    if (a == null || b == null || c == null) {
      return null;
    }
    double ax = a.getX();
    double ay = a.getY();
    double bx = b.getX();
    double by = b.getY();
    double cx = c.getX();
    double cy = c.getY();
    double d = 2 * (ax * (by - cy) + bx * (cy - ay) + cx * (ay - by));
    if (Math.abs(d) < 1e-12) {
      return null;
    }
    double ux =
        ((ax * ax + ay * ay) * (by - cy)
                + (bx * bx + by * by) * (cy - ay)
                + (cx * cx + cy * cy) * (ay - by))
            / d;
    double uy =
        ((ax * ax + ay * ay) * (cx - bx)
                + (bx * bx + by * by) * (ax - cx)
                + (cx * cx + cy * cy) * (bx - ax))
            / d;
    double r = Math.hypot(ux - ax, uy - ay);
    return new Ellipse2D.Double(ux - r, uy - r, 2 * r, 2 * r);
  }

  public static double polygonPerimeter(List<Point2D.Double> pts, boolean closed) {
    if (pts == null || pts.size() < 2) {
      return 0;
    }
    double sum = 0;
    for (int i = 1; i < pts.size(); i++) {
      sum += pts.get(i - 1).distance(pts.get(i));
    }
    if (closed && pts.size() >= 3) {
      sum += pts.get(pts.size() - 1).distance(pts.get(0));
    }
    return sum;
  }

  /**
   * Oriented minimum bounding box area via rotating edges of the convex hull. Degenerate polygons
   * fall back to axis-aligned area.
   */
  public static double ombbArea(List<Point2D.Double> pts) {
    List<Point2D.Double> hull = convexHull(pts);
    if (hull.size() < 3) {
      if (hull.size() == 2) {
        return 0;
      }
      return 0;
    }
    double best = Double.POSITIVE_INFINITY;
    int n = hull.size();
    for (int i = 0; i < n; i++) {
      Point2D.Double p = hull.get(i);
      Point2D.Double q = hull.get((i + 1) % n);
      double dx = q.x - p.x;
      double dy = q.y - p.y;
      double len = Math.hypot(dx, dy);
      if (len < 1e-12) {
        continue;
      }
      double ux = dx / len;
      double uy = dy / len;
      double vx = -uy;
      double vy = ux;
      double minU = Double.POSITIVE_INFINITY;
      double maxU = Double.NEGATIVE_INFINITY;
      double minV = Double.POSITIVE_INFINITY;
      double maxV = Double.NEGATIVE_INFINITY;
      for (Point2D.Double h : hull) {
        double pu = h.x * ux + h.y * uy;
        double pv = h.x * vx + h.y * vy;
        minU = Math.min(minU, pu);
        maxU = Math.max(maxU, pu);
        minV = Math.min(minV, pv);
        maxV = Math.max(maxV, pv);
      }
      double area = (maxU - minU) * (maxV - minV);
      if (area < best) {
        best = area;
      }
    }
    return Double.isInfinite(best) ? 0 : best;
  }

  static List<Point2D.Double> convexHull(List<Point2D.Double> pts) {
    List<Point2D.Double> src = new ArrayList<>();
    if (pts != null) {
      for (Point2D.Double p : pts) {
        if (p != null) {
          src.add(p);
        }
      }
    }
    src.sort(
        (a, b) -> {
          int cx = Double.compare(a.x, b.x);
          return cx != 0 ? cx : Double.compare(a.y, b.y);
        });
    if (src.size() <= 1) {
      return src;
    }
    List<Point2D.Double> lower = new ArrayList<>();
    for (Point2D.Double p : src) {
      while (lower.size() >= 2
          && cross(lower.get(lower.size() - 2), lower.get(lower.size() - 1), p) <= 0) {
        lower.remove(lower.size() - 1);
      }
      lower.add(p);
    }
    List<Point2D.Double> upper = new ArrayList<>();
    for (int i = src.size() - 1; i >= 0; i--) {
      Point2D.Double p = src.get(i);
      while (upper.size() >= 2
          && cross(upper.get(upper.size() - 2), upper.get(upper.size() - 1), p) <= 0) {
        upper.remove(upper.size() - 1);
      }
      upper.add(p);
    }
    lower.remove(lower.size() - 1);
    upper.remove(upper.size() - 1);
    lower.addAll(upper);
    return lower;
  }

  static double cross(Point2D.Double o, Point2D.Double a, Point2D.Double b) {
    return (a.x - o.x) * (b.y - o.y) - (a.y - o.y) * (b.x - o.x);
  }
}
