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
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "LineWithGapGraphic")
public class LineWithGapGraphic extends LineGraphic {

  private float gapSize = 10f;

  public float getGapSize() {
    return gapSize;
  }

  public void setGapSize(float gapSize) {
    this.gapSize = Math.max(0f, gapSize);
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
    double dx = b.x - a.x;
    double dy = b.y - a.y;
    double len = Math.hypot(dx, dy);
    if (len == 0 || gapSize <= 0 || gapSize >= len) {
      super.buildShape();
      return;
    }
    double mx = (a.x + b.x) / 2;
    double my = (a.y + b.y) / 2;
    double ux = dx / len;
    double uy = dy / len;
    double h = gapSize / 2.0;
    Path2D path = new Path2D.Double();
    path.moveTo(a.x, a.y);
    path.lineTo(mx - ux * h, my - uy * h);
    path.moveTo(mx + ux * h, my + uy * h);
    path.lineTo(b.x, b.y);
    setShape(path);
  }
}
