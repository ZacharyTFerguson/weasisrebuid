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

/** Three-handle parallelogram used as an oblique rectangle. */
public class ObliqueRectangleGraphic extends AbstractDragGraphicArea {

  public ObliqueRectangleGraphic() {
    super(3);
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
    double ux = b.x - a.x;
    double uy = b.y - a.y;
    double vx = c.x - a.x;
    double vy = c.y - a.y;
    Point2D.Double d = new Point2D.Double(a.x + ux + vx, a.y + uy + vy);
    Path2D path = new Path2D.Double();
    path.moveTo(a.x, a.y);
    path.lineTo(b.x, b.y);
    path.lineTo(d.x, d.y);
    path.lineTo(c.x, c.y);
    path.closePath();
    setShape(path);
  }

  @Override
  protected AbstractGraphic newInstance() {
    return new ObliqueRectangleGraphic();
  }
}
