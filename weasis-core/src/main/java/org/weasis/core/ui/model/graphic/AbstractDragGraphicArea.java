/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.graphic;

import java.awt.geom.Point2D;

public abstract class AbstractDragGraphicArea extends AbstractDragGraphic implements GraphicArea {

  protected AbstractDragGraphicArea(int expectedPts) {
    super(expectedPts);
  }

  @Override
  public double getAreaValue() {
    var pts = getPts();
    if (pts.size() < 3) {
      if (pts.size() == 2) {
        Point2D.Double a = pts.get(0);
        Point2D.Double b = pts.get(1);
        return Math.abs((b.x - a.x) * (b.y - a.y));
      }
      return 0;
    }
    double sum = 0;
    for (int i = 0; i < pts.size(); i++) {
      Point2D.Double p = pts.get(i);
      Point2D.Double q = pts.get((i + 1) % pts.size());
      sum += p.x * q.y - q.x * p.y;
    }
    return Math.abs(sum) / 2.0;
  }
}
