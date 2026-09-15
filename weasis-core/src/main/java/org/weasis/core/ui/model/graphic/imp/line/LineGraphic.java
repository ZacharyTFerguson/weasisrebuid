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

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Optional;
import org.weasis.core.api.image.measure.ImageSpacing;
import org.weasis.core.ui.model.graphic.AbstractDragGraphic;
import org.weasis.core.ui.model.graphic.AbstractGraphic;

public class LineGraphic extends AbstractDragGraphic {

  public LineGraphic() {
    super(2);
  }

  public double getLength() {
    Point2D.Double a = getHandlePoint(0);
    Point2D.Double b = getHandlePoint(1);
    if (a == null || b == null) {
      return 0;
    }
    return a.distance(b);
  }

  /**
   * Physical length in mm using row spacing for vertical delta and column spacing for horizontal.
   */
  public Optional<Double> getLengthMm(ImageSpacing spacing) {
    if (spacing == null) {
      return Optional.empty();
    }
    double row = spacing.rowMmPerPixel();
    double col = spacing.colMmPerPixel();
    if (row <= 0 || col <= 0 || !Double.isFinite(row) || !Double.isFinite(col)) {
      return Optional.empty();
    }
    Point2D.Double a = getHandlePoint(0);
    Point2D.Double b = getHandlePoint(1);
    if (a == null || b == null) {
      return Optional.empty();
    }
    double dx = Math.abs(b.x - a.x);
    double dy = Math.abs(b.y - a.y);
    return Optional.of(Math.hypot(dx * col, dy * row));
  }

  @Override
  public void buildShape() {
    Point2D.Double a = getHandlePoint(0);
    Point2D.Double b = getHandlePoint(1);
    if (a == null || b == null) {
      setShape(null);
      return;
    }
    setShape(new Line2D.Double(a, b));
  }

  @Override
  protected AbstractGraphic newInstance() {
    return new LineGraphic();
  }
}
