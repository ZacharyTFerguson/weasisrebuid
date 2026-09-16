/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.graphic.imp.line;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.weasis.core.api.image.measure.ImageSpacing;
import org.weasis.core.ui.model.graphic.AbstractDragGraphic;
import org.weasis.core.ui.model.graphic.AbstractGraphic;

/**
 * Closed interpolating curve through image-space handles (distinct from {@link CurveGraphic} and
 * {@link PolylineGraphic}).
 */
public class ClosedCurveGraphic extends AbstractDragGraphic {

  private static final int SAMPLES_PER_SEGMENT = 64;

  public ClosedCurveGraphic() {
    super(0);
  }

  /** Pixel path length along the same sampled closed path as {@link #getLengthMm(ImageSpacing)}. */
  public double getLength() {
    List<Point2D.Double> pts = getPts();
    if (pts.size() < 3) {
      return 0;
    }
    List<Point2D.Double> samples = sampleClosedCatmullRom(pts);
    if (samples.size() < 2) {
      return 0;
    }
    double sum = 0;
    for (int i = 1; i < samples.size(); i++) {
      sum += samples.get(i - 1).distance(samples.get(i));
    }
    return sum;
  }

  /**
   * Physical path length in mm along the closed interpolating curve: samples the smooth closed path
   * (including wrap from last handle to first) and sums per-step length using row spacing for
   * vertical delta and column spacing for horizontal delta (same rule as {@link
   * LineGraphic#getLengthMm}).
   */
  public Optional<Double> getLengthMm(ImageSpacing spacing) {
    if (spacing == null) {
      return Optional.empty();
    }
    double row = spacing.rowMm();
    double col = spacing.colMm();
    if (row <= 0 || col <= 0 || !Double.isFinite(row) || !Double.isFinite(col)) {
      return Optional.empty();
    }
    List<Point2D.Double> pts = getPts();
    if (pts.size() < 3) {
      return Optional.empty();
    }
    List<Point2D.Double> samples = sampleClosedCatmullRom(pts);
    if (samples.size() < 2) {
      return Optional.empty();
    }
    double sum = 0;
    for (int i = 1; i < samples.size(); i++) {
      Point2D.Double a = samples.get(i - 1);
      Point2D.Double b = samples.get(i);
      double dx = Math.abs(b.x - a.x);
      double dy = Math.abs(b.y - a.y);
      sum += Math.hypot(dx * col, dy * row);
    }
    if (!Double.isFinite(sum)) {
      return Optional.empty();
    }
    return Optional.of(sum);
  }

  @Override
  public void buildShape() {
    List<Point2D.Double> pts = getPts();
    if (pts.size() < 3) {
      setShape(null);
      return;
    }
    List<Point2D.Double> samples = sampleClosedCatmullRom(pts);
    if (samples.isEmpty()) {
      setShape(null);
      return;
    }
    Path2D path = new Path2D.Double();
    Point2D.Double first = samples.getFirst();
    path.moveTo(first.x, first.y);
    for (int i = 1; i < samples.size(); i++) {
      Point2D.Double p = samples.get(i);
      path.lineTo(p.x, p.y);
    }
    path.closePath();
    setShape(path);
  }

  @Override
  protected AbstractGraphic newInstance() {
    return new ClosedCurveGraphic();
  }

  static List<Point2D.Double> sampleClosedCatmullRom(List<Point2D.Double> pts) {
    int n = pts.size();
    if (n < 3) {
      return List.of();
    }
    List<Point2D.Double> out = new ArrayList<>();
    for (int seg = 0; seg < n; seg++) {
      Point2D.Double p0 = pts.get((seg - 1 + n) % n);
      Point2D.Double p1 = pts.get(seg);
      Point2D.Double p2 = pts.get((seg + 1) % n);
      Point2D.Double p3 = pts.get((seg + 2) % n);
      int steps = seg == n - 1 ? SAMPLES_PER_SEGMENT + 1 : SAMPLES_PER_SEGMENT;
      for (int i = 0; i < steps; i++) {
        if (seg > 0 && i == 0) {
          continue;
        }
        double t = i / (double) SAMPLES_PER_SEGMENT;
        out.add(catmullRomPoint(p0, p1, p2, p3, t));
      }
    }
    return out;
  }

  private static Point2D.Double catmullRomPoint(
      Point2D.Double p0, Point2D.Double p1, Point2D.Double p2, Point2D.Double p3, double t) {
    double t2 = t * t;
    double t3 = t2 * t;
    double x =
        0.5
            * ((2 * p1.x)
                + (-p0.x + p2.x) * t
                + (2 * p0.x - 5 * p1.x + 4 * p2.x - p3.x) * t2
                + (-p0.x + 3 * p1.x - 3 * p2.x + p3.x) * t3);
    double y =
        0.5
            * ((2 * p1.y)
                + (-p0.y + p2.y) * t
                + (2 * p0.y - 5 * p1.y + 4 * p2.y - p3.y) * t2
                + (-p0.y + 3 * p1.y - 3 * p2.y + p3.y) * t3);
    return new Point2D.Double(x, y);
  }
}
