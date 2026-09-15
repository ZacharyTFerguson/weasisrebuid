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

import jakarta.xml.bind.annotation.XmlRootElement;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import org.weasis.core.ui.model.graphic.AbstractDragGraphic;
import org.weasis.core.ui.model.graphic.AbstractGraphic;

/** Base segment 0-1; handle 2 is the free end of a perpendicular from the nearest point on 0-1. */
@XmlRootElement(name = "PerpendicularLineGraphic")
public class PerpendicularLineGraphic extends AbstractDragGraphic {

  public PerpendicularLineGraphic() {
    super(3);
  }

  @Override
  public void buildShape() {
    Point2D.Double a = getHandlePoint(0);
    Point2D.Double b = getHandlePoint(1);
    Point2D.Double p = getHandlePoint(2);
    if (a == null || b == null || p == null) {
      setShape(null);
      return;
    }
    double ux = b.x - a.x;
    double uy = b.y - a.y;
    double len2 = ux * ux + uy * uy;
    double t = 0;
    if (len2 > 0) {
      t = ((p.x - a.x) * ux + (p.y - a.y) * uy) / len2;
      t = Math.max(0, Math.min(1, t));
    }
    double fx = a.x + t * ux;
    double fy = a.y + t * uy;
    Path2D path = new Path2D.Double();
    path.moveTo(a.x, a.y);
    path.lineTo(b.x, b.y);
    path.moveTo(fx, fy);
    path.lineTo(p.x, p.y);
    setShape(path);
  }

  @Override
  protected AbstractGraphic newInstance() {
    return new PerpendicularLineGraphic();
  }
}
