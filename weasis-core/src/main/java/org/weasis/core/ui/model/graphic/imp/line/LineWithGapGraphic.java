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
import org.weasis.core.ui.model.graphic.AbstractGraphic;
import org.weasis.core.ui.model.graphic.GraphicMath;

public class LineWithGapGraphic extends LineGraphic {

  private float gapSize = 10f;

  public float getGapSize() {
    return gapSize;
  }

  public void setGapSize(float gapSize) {
    this.gapSize = Math.max(0, gapSize);
    buildShape();
  }

  @Override
  public void buildShape() {
    Point2D.Double a = getHandlePoint(0);
    Point2D.Double b = getHandlePoint(1);
    if (a == null || b == null) {
      setShape(null);
      return;
    }
    double len = GraphicMath.length(a, b);
    if (len < 1e-9 || gapSize <= 0 || gapSize >= len) {
      super.buildShape();
      return;
    }
    double ux = (b.x - a.x) / len;
    double uy = (b.y - a.y) / len;
    double mid = len / 2.0;
    double half = gapSize / 2.0;
    Point2D.Double g0 = new Point2D.Double(a.x + ux * (mid - half), a.y + uy * (mid - half));
    Point2D.Double g1 = new Point2D.Double(a.x + ux * (mid + half), a.y + uy * (mid + half));
    Path2D path = new Path2D.Double();
    path.moveTo(a.x, a.y);
    path.lineTo(g0.x, g0.y);
    path.moveTo(g1.x, g1.y);
    path.lineTo(b.x, b.y);
    setShape(path);
  }

  @Override
  protected AbstractGraphic newInstance() {
    LineWithGapGraphic copy = new LineWithGapGraphic();
    copy.gapSize = this.gapSize;
    return copy;
  }
}
