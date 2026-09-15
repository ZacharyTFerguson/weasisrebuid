/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.graphic.imp.area;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import org.weasis.core.ui.model.graphic.AbstractDragGraphicArea;
import org.weasis.core.ui.model.graphic.AbstractGraphic;

/** Circle through three non-collinear points. */
public class ThreePointsCircleGraphic extends AbstractDragGraphicArea {

  public ThreePointsCircleGraphic() {
    super(3);
  }

  public Point2D.Double getCenter() {
    Point2D.Double a = getHandlePoint(0);
    Point2D.Double b = getHandlePoint(1);
    Point2D.Double c = getHandlePoint(2);
    if (a == null || b == null || c == null) {
      return null;
    }
    double d = 2 * (a.x * (b.y - c.y) + b.x * (c.y - a.y) + c.x * (a.y - b.y));
    if (Math.abs(d) < 1e-12) {
      return null;
    }
    double ux =
        ((a.x * a.x + a.y * a.y) * (b.y - c.y)
                + (b.x * b.x + b.y * b.y) * (c.y - a.y)
                + (c.x * c.x + c.y * c.y) * (a.y - b.y))
            / d;
    double uy =
        ((a.x * a.x + a.y * a.y) * (c.x - b.x)
                + (b.x * b.x + b.y * b.y) * (a.x - c.x)
                + (c.x * c.x + c.y * c.y) * (b.x - a.x))
            / d;
    return new Point2D.Double(ux, uy);
  }

  public double getRadius() {
    Point2D.Double center = getCenter();
    Point2D.Double a = getHandlePoint(0);
    if (center == null || a == null) {
      return 0;
    }
    return center.distance(a);
  }

  @Override
  public void buildShape() {
    Point2D.Double center = getCenter();
    double r = getRadius();
    if (center == null || r <= 0) {
      setShape(null);
      return;
    }
    setShape(new Ellipse2D.Double(center.x - r, center.y - r, r * 2, r * 2));
  }

  @Override
  public double getAreaValue() {
    double r = getRadius();
    return Math.PI * r * r;
  }

  @Override
  protected AbstractGraphic newInstance() {
    return new ThreePointsCircleGraphic();
  }
}
