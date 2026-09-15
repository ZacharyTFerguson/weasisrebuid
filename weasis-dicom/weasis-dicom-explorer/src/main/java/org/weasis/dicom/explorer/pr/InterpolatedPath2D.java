/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.pr;

import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Open or closed Catmull-Rom spline through GSPS INTERPOLATED graphic points (C.10.5 Graphic Type).
 */
public class InterpolatedPath2D extends Path2D.Double {

  public static final int STEPS = 16;

  public InterpolatedPath2D() {
    this(List.of(), false);
  }

  public InterpolatedPath2D(float[] graphicData, boolean closed) {
    this(pointsOf(graphicData), closed);
  }

  public InterpolatedPath2D(List<? extends Point2D> points, boolean closed) {
    super();
    build(points, closed);
  }

  public static List<Point2D.Double> pointsOf(float[] graphicData) {
    List<Point2D.Double> pts = new ArrayList<>();
    if (graphicData == null) {
      return pts;
    }
    int n = graphicData.length / 2;
    for (int i = 0; i < n; i++) {
      pts.add(new Point2D.Double(graphicData[i * 2], graphicData[i * 2 + 1]));
    }
    return pts;
  }

  public double distanceTo(double x, double y) {
    PathIterator it = getPathIterator(null, 0.25);
    double[] coords = new double[6];
    double mx = 0;
    double my = 0;
    double px = 0;
    double py = 0;
    boolean started = false;
    double best = java.lang.Double.POSITIVE_INFINITY;
    while (!it.isDone()) {
      int type = it.currentSegment(coords);
      if (type == PathIterator.SEG_MOVETO) {
        mx = px = coords[0];
        my = py = coords[1];
        started = true;
        best = Math.min(best, Point2D.distance(px, py, x, y));
      } else if (started && type == PathIterator.SEG_LINETO) {
        best = Math.min(best, Line2D.ptSegDist(px, py, coords[0], coords[1], x, y));
        px = coords[0];
        py = coords[1];
      } else if (started && type == PathIterator.SEG_CLOSE) {
        best = Math.min(best, Line2D.ptSegDist(px, py, mx, my, x, y));
      }
      it.next();
    }
    return best;
  }

  private void build(List<? extends Point2D> in, boolean closed) {
    reset();
    if (in == null || in.isEmpty()) {
      return;
    }
    List<Point2D> pts = new ArrayList<>();
    for (Point2D p : in) {
      if (p != null) {
        pts.add(p);
      }
    }
    if (pts.isEmpty()) {
      return;
    }
    if (pts.size() == 1) {
      Point2D p = pts.get(0);
      moveTo(p.getX(), p.getY());
      return;
    }
    if (pts.size() == 2) {
      Point2D a = pts.get(0);
      Point2D b = pts.get(1);
      moveTo(a.getX(), a.getY());
      lineTo(b.getX(), b.getY());
      if (closed) {
        closePath();
      }
      return;
    }
    int n = pts.size();
    boolean loop = closed && n >= 3;
    Point2D first = pts.get(0);
    moveTo(first.getX(), first.getY());
    int segments = loop ? n : n - 1;
    for (int i = 0; i < segments; i++) {
      Point2D p0 = point(pts, i - 1, loop);
      Point2D p1 = point(pts, i, loop);
      Point2D p2 = point(pts, i + 1, loop);
      Point2D p3 = point(pts, i + 2, loop);
      for (int s = 1; s <= STEPS; s++) {
        double t = s / (double) STEPS;
        Point2D.Double c = catmull(p0, p1, p2, p3, t);
        lineTo(c.x, c.y);
      }
    }
    if (loop) {
      closePath();
    }
  }

  static Point2D point(List<Point2D> pts, int index, boolean loop) {
    int n = pts.size();
    if (loop) {
      int i = Math.floorMod(index, n);
      return pts.get(i);
    }
    if (index < 0) {
      return pts.get(0);
    }
    if (index >= n) {
      return pts.get(n - 1);
    }
    return pts.get(index);
  }

  static Point2D.Double catmull(Point2D p0, Point2D p1, Point2D p2, Point2D p3, double t) {
    double t2 = t * t;
    double t3 = t2 * t;
    double x =
        0.5
            * ((2 * p1.getX())
                + (-p0.getX() + p2.getX()) * t
                + (2 * p0.getX() - 5 * p1.getX() + 4 * p2.getX() - p3.getX()) * t2
                + (-p0.getX() + 3 * p1.getX() - 3 * p2.getX() + p3.getX()) * t3);
    double y =
        0.5
            * ((2 * p1.getY())
                + (-p0.getY() + p2.getY()) * t
                + (2 * p0.getY() - 5 * p1.getY() + 4 * p2.getY() - p3.getY()) * t2
                + (-p0.getY() + 3 * p1.getY() - 3 * p2.getY() + p3.getY()) * t3);
    return new Point2D.Double(x, y);
  }
}
