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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Monotone-chain convex hull of image-space points. */
public class ConvexHull {

  public static List<Point2D.Double> hull(List<Point2D.Double> pts) {
    List<Point2D.Double> unique = uniqueSorted(pts);
    if (unique.size() <= 2) {
      return List.copyOf(unique);
    }
    return build(unique);
  }

  static List<Point2D.Double> uniqueSorted(List<Point2D.Double> pts) {
    if (missing(pts)) {
      return List.of();
    }
    List<Point2D.Double> copy = new ArrayList<>();
    for (Point2D.Double p : pts) {
      addCopy(copy, p);
    }
    copy.sort(
        Comparator.comparingDouble(Point2D.Double::getX)
            .thenComparingDouble(Point2D.Double::getY));
    return dedupe(copy);
  }

  static boolean missing(List<Point2D.Double> pts) {
    return pts == null || pts.isEmpty();
  }

  static void addCopy(List<Point2D.Double> out, Point2D.Double p) {
    if (p != null) {
      out.add(new Point2D.Double(p.getX(), p.getY()));
    }
  }

  static List<Point2D.Double> dedupe(List<Point2D.Double> sorted) {
    List<Point2D.Double> out = new ArrayList<>();
    for (Point2D.Double p : sorted) {
      addUnique(out, p);
    }
    return out;
  }

  static void addUnique(List<Point2D.Double> out, Point2D.Double p) {
    if (out.isEmpty() || !same(out.getLast(), p)) {
      out.add(p);
    }
  }

  static boolean same(Point2D.Double a, Point2D.Double b) {
    return a.getX() == b.getX() && a.getY() == b.getY();
  }

  static List<Point2D.Double> build(List<Point2D.Double> pts) {
    List<Point2D.Double> lower = chain(pts, false);
    List<Point2D.Double> upper = chain(pts, true);
    dropLast(lower);
    dropLast(upper);
    lower.addAll(upper);
    return List.copyOf(lower);
  }

  static void dropLast(List<Point2D.Double> pts) {
    if (!pts.isEmpty()) {
      pts.removeLast();
    }
  }

  static List<Point2D.Double> chain(List<Point2D.Double> pts, boolean upper) {
    return upper ? reverseChain(pts) : forwardChain(pts);
  }

  static List<Point2D.Double> forwardChain(List<Point2D.Double> pts) {
    List<Point2D.Double> out = new ArrayList<>();
    for (Point2D.Double p : pts) {
      push(out, p);
    }
    return out;
  }

  static List<Point2D.Double> reverseChain(List<Point2D.Double> pts) {
    List<Point2D.Double> out = new ArrayList<>();
    for (int i = pts.size() - 1; i >= 0; i--) {
      push(out, pts.get(i));
    }
    return out;
  }

  static void push(List<Point2D.Double> out, Point2D.Double p) {
    while (out.size() >= 2 && !leftTurn(out.get(out.size() - 2), out.getLast(), p)) {
      out.removeLast();
    }
    out.add(p);
  }

  static boolean leftTurn(Point2D.Double o, Point2D.Double a, Point2D.Double b) {
    return cross(o, a, b) > 0;
  }

  static double cross(Point2D.Double o, Point2D.Double a, Point2D.Double b) {
    return (a.getX() - o.getX()) * (b.getY() - o.getY())
        - (a.getY() - o.getY()) * (b.getX() - o.getX());
  }
}
