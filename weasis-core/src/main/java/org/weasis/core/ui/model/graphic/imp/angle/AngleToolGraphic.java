/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.graphic.imp.angle;

import jakarta.xml.bind.annotation.XmlRootElement;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import org.weasis.core.ui.model.graphic.AbstractDragGraphic;
import org.weasis.core.ui.model.graphic.AbstractGraphic;

/** Three-point angle: handle 1 is the vertex. Shortcut A. */
@XmlRootElement(name = "AngleToolGraphic")
public class AngleToolGraphic extends AbstractDragGraphic {

  public AngleToolGraphic() {
    super(3);
  }

  public double getAngleDegrees() {
    Point2D.Double a = getHandlePoint(0);
    Point2D.Double v = getHandlePoint(1);
    Point2D.Double b = getHandlePoint(2);
    if (a == null || v == null || b == null) {
      return 0;
    }
    double ux = a.x - v.x;
    double uy = a.y - v.y;
    double vx = b.x - v.x;
    double vy = b.y - v.y;
    double nu = Math.hypot(ux, uy);
    double nv = Math.hypot(vx, vy);
    if (nu == 0 || nv == 0) {
      return 0;
    }
    double cos = Math.max(-1, Math.min(1, (ux * vx + uy * vy) / (nu * nv)));
    return Math.toDegrees(Math.acos(cos));
  }

  @Override
  public void buildShape() {
    Point2D.Double a = getHandlePoint(0);
    Point2D.Double v = getHandlePoint(1);
    Point2D.Double b = getHandlePoint(2);
    if (a == null || v == null || b == null) {
      setShape(null);
      return;
    }
    Path2D path = new Path2D.Double();
    path.moveTo(a.x, a.y);
    path.lineTo(v.x, v.y);
    double degrees = getAngleDegrees();
    if (degrees >= 0.5) {
      path.lineTo(b.x, b.y);
      setLabel(new String[] {String.format("%.1f°", degrees)});
    } else {
      setLabel(new String[0]);
    }
    setShape(path);
  }

  /** After click-drag-release, place a right-angle ray so degrees paint before the second click. */
  public void setRightRay() {
    Point2D.Double a = getHandlePoint(0);
    Point2D.Double v = getHandlePoint(1);
    if (a == null || v == null) {
      return;
    }
    double dx = a.x - v.x;
    double dy = a.y - v.y;
    if (dx == 0 && dy == 0) {
      return;
    }
    setHandlePoint(2, new Point2D.Double(v.x - dy, v.y + dx));
  }

  @Override
  protected AbstractGraphic newInstance() {
    return new AngleToolGraphic();
  }
}
