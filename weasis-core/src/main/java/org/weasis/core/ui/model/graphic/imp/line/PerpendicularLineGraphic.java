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
import org.weasis.core.ui.model.graphic.AbstractDragGraphic;
import org.weasis.core.ui.model.graphic.AbstractGraphic;
import org.weasis.core.ui.model.graphic.GraphicMath;

/** Base AB; third point defines a perpendicular from the foot on AB. */
public class PerpendicularLineGraphic extends AbstractDragGraphic {

  public PerpendicularLineGraphic() {
    super(3);
  }

  public boolean isPerpendicular() {
    Point2D.Double a = getHandlePoint(0);
    Point2D.Double b = getHandlePoint(1);
    Point2D.Double c = getHandlePoint(2);
    Point2D.Double foot = foot(a, b, c);
    return GraphicMath.nearlyPerpendicular(a, b, foot, c, 1.0);
  }

  static Point2D.Double foot(Point2D.Double a, Point2D.Double b, Point2D.Double c) {
    if (a == null || b == null || c == null) {
      return new Point2D.Double();
    }
    double dx = b.x - a.x;
    double dy = b.y - a.y;
    double len2 = dx * dx + dy * dy;
    if (len2 < 1e-18) {
      return new Point2D.Double(a.x, a.y);
    }
    double t = ((c.x - a.x) * dx + (c.y - a.y) * dy) / len2;
    return new Point2D.Double(a.x + t * dx, a.y + t * dy);
  }

  @Override
  public void buildShape() {
    Point2D.Double a = getHandlePoint(0);
    Point2D.Double b = getHandlePoint(1);
    Point2D.Double c = getHandlePoint(2);
    if (a == null || b == null || c == null) {
      setShape(null);
      return;
    }
    Point2D.Double f = foot(a, b, c);
    Path2D path = new Path2D.Double();
    path.moveTo(a.x, a.y);
    path.lineTo(b.x, b.y);
    path.moveTo(f.x, f.y);
    path.lineTo(c.x, c.y);
    setShape(path);
  }

  @Override
  protected AbstractGraphic newInstance() {
    return new PerpendicularLineGraphic();
  }
}
