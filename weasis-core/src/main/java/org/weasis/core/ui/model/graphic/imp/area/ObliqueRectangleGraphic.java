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

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import org.weasis.core.ui.model.graphic.AbstractDragGraphicArea;
import org.weasis.core.ui.model.graphic.AbstractGraphic;

/**
 * Rotated rectangle: p0 origin, p1 adjacent along width, p2 adjacent along height. Rect/ellipse at
 * any angle (CHECKLIST §4.4).
 */
public class ObliqueRectangleGraphic extends AbstractDragGraphicArea {

  public ObliqueRectangleGraphic() {
    super(3);
  }

  @Override
  public void buildShape() {
    Point2D.Double o = getHandlePoint(0);
    Point2D.Double w = getHandlePoint(1);
    Point2D.Double h = getHandlePoint(2);
    if (o == null || w == null || h == null) {
      setShape(null);
      return;
    }
    double cx = w.x + (h.x - o.x);
    double cy = w.y + (h.y - o.y);
    Path2D path = new Path2D.Double();
    path.moveTo(o.x, o.y);
    path.lineTo(w.x, w.y);
    path.lineTo(cx, cy);
    path.lineTo(h.x, h.y);
    path.closePath();
    setShape(path);
  }

  @Override
  public double getAreaValue() {
    Point2D.Double o = getHandlePoint(0);
    Point2D.Double w = getHandlePoint(1);
    Point2D.Double h = getHandlePoint(2);
    if (o == null || w == null || h == null) {
      return 0;
    }
    return Math.abs((w.x - o.x) * (h.y - o.y) - (w.y - o.y) * (h.x - o.x));
  }

  @Override
  protected AbstractGraphic newInstance() {
    return new ObliqueRectangleGraphic();
  }
}
