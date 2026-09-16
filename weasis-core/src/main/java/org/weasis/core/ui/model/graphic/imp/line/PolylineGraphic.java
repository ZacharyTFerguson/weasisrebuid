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
import java.util.List;
import java.util.Optional;
import org.weasis.core.api.image.measure.ImageSpacing;
import org.weasis.core.ui.model.graphic.AbstractDragGraphic;
import org.weasis.core.ui.model.graphic.AbstractGraphic;

public class PolylineGraphic extends AbstractDragGraphic {

  public PolylineGraphic() {
    super(0);
  }

  /** Pixel path length: sum of Euclidean distances along consecutive vertices. */
  public double getLength() {
    List<Point2D.Double> pts = getPts();
    if (pts.size() < 2) {
      return 0;
    }
    double sum = 0;
    for (int i = 1; i < pts.size(); i++) {
      sum += pts.get(i - 1).distance(pts.get(i));
    }
    return sum;
  }

  /**
   * Physical path length in mm: sum of per-segment lengths using row spacing for vertical delta and
   * column spacing for horizontal delta (same rule as {@link LineGraphic#getLengthMm}).
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
    if (pts.size() < 2) {
      return Optional.empty();
    }
    double sum = 0;
    for (int i = 1; i < pts.size(); i++) {
      Point2D.Double a = pts.get(i - 1);
      Point2D.Double b = pts.get(i);
      double dx = Math.abs(b.x - a.x);
      double dy = Math.abs(b.y - a.y);
      sum += Math.hypot(dx * col, dy * row);
    }
    return Optional.of(sum);
  }

  @Override
  public void buildShape() {
    var pts = getPts();
    if (pts.isEmpty()) {
      setShape(null);
      return;
    }
    Path2D path = new Path2D.Double();
    Point2D.Double first = pts.getFirst();
    path.moveTo(first.x, first.y);
    for (int i = 1; i < pts.size(); i++) {
      Point2D.Double p = pts.get(i);
      path.lineTo(p.x, p.y);
    }
    setShape(path);
  }

  @Override
  protected AbstractGraphic newInstance() {
    return new PolylineGraphic();
  }
}
