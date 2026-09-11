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

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import org.weasis.core.ui.model.graphic.AbstractDragGraphic;
import org.weasis.core.ui.model.graphic.AbstractGraphic;
import org.weasis.core.ui.model.graphic.GraphicMath;

/** Three-point angle (A–vertex–B). */
public class AngleToolGraphic extends AbstractDragGraphic {

  public AngleToolGraphic() {
    super(3);
  }

  public double getAngleDeg() {
    return GraphicMath.angleDeg(getHandlePoint(0), getHandlePoint(1), getHandlePoint(2));
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
    path.lineTo(b.x, b.y);
    setShape(path);
    setLabel(new String[] {Double.toString(getAngleDeg()) + " °"});
  }

  @Override
  protected AbstractGraphic newInstance() {
    return new AngleToolGraphic();
  }
}
