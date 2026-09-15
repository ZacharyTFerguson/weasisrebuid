/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.mpr.cmpr;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/** Uniform arc-length sampling of a polyline drawn on an axial plane. */
public class CurveSampler {

  public List<Point2D.Double> resample(List<Point2D.Double> polyline, double spacing) {
    if (polyline == null || polyline.isEmpty()) {
      return List.of();
    }
    if (spacing <= 0) {
      return copyAll(polyline);
    }
    double[] cum = cumulative(polyline);
    double total = cum[cum.length - 1];
    if (total <= 1e-12) {
      return List.of(copy(polyline.get(0)));
    }
    int n = Math.max(2, (int) Math.round(total / spacing) + 1);
    List<Point2D.Double> out = new ArrayList<>(n);
    for (int i = 0; i < n; i++) {
      out.add(atLength(polyline, cum, total * i / (n - 1.0)));
    }
    return out;
  }

  public double length(List<Point2D.Double> polyline) {
    if (polyline == null || polyline.isEmpty()) {
      return 0;
    }
    double[] cum = cumulative(polyline);
    return cum[cum.length - 1];
  }

  public Point2D.Double atParameter(List<Point2D.Double> polyline, double t) {
    if (polyline == null || polyline.isEmpty()) {
      return new Point2D.Double();
    }
    double[] cum = cumulative(polyline);
    double total = cum[cum.length - 1];
    double clamped = Math.max(0, Math.min(1, t));
    if (total <= 1e-12) {
      return copy(polyline.get(0));
    }
    return atLength(polyline, cum, clamped * total);
  }

  public Point2D.Double tangent(List<Point2D.Double> polyline, double t) {
    if (polyline == null || polyline.size() < 2) {
      return new Point2D.Double(1, 0);
    }
    double clamped = Math.max(0, Math.min(1, t));
    double dt = 1e-3;
    Point2D.Double a = atParameter(polyline, Math.max(0, clamped - dt));
    Point2D.Double b = atParameter(polyline, Math.min(1, clamped + dt));
    double tx = b.x - a.x;
    double ty = b.y - a.y;
    double n = Math.hypot(tx, ty);
    if (n <= 1e-12) {
      return new Point2D.Double(1, 0);
    }
    return new Point2D.Double(tx / n, ty / n);
  }

  static Point2D.Double atLength(List<Point2D.Double> pts, double[] cum, double s) {
    if (s <= 0) {
      return copy(pts.get(0));
    }
    double total = cum[cum.length - 1];
    if (s >= total) {
      return copy(pts.get(pts.size() - 1));
    }
    int i = 1;
    while (i < cum.length && cum[i] < s) {
      i++;
    }
    double seg = cum[i] - cum[i - 1];
    double f = seg <= 1e-12 ? 0 : (s - cum[i - 1]) / seg;
    Point2D.Double a = pts.get(i - 1);
    Point2D.Double b = pts.get(i);
    return new Point2D.Double(a.x + f * (b.x - a.x), a.y + f * (b.y - a.y));
  }

  static double[] cumulative(List<Point2D.Double> pts) {
    double[] c = new double[pts.size()];
    for (int i = 1; i < pts.size(); i++) {
      c[i] = c[i - 1] + pts.get(i - 1).distance(pts.get(i));
    }
    return c;
  }

  private static List<Point2D.Double> copyAll(List<Point2D.Double> polyline) {
    List<Point2D.Double> out = new ArrayList<>(polyline.size());
    for (Point2D.Double p : polyline) {
      out.add(copy(p));
    }
    return out;
  }

  private static Point2D.Double copy(Point2D.Double p) {
    return new Point2D.Double(p.x, p.y);
  }
}
