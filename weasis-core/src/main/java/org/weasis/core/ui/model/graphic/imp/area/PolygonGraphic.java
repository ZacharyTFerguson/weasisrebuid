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
import java.util.Optional;
import org.weasis.core.api.image.measure.ImageSpacing;
import org.weasis.core.ui.model.graphic.AbstractDragGraphicArea;
import org.weasis.core.ui.model.graphic.AbstractGraphic;

public class PolygonGraphic extends AbstractDragGraphicArea {

  public PolygonGraphic() {
    super(0);
  }

  /**
   * Physical area in mm² from the closed vertex path: pixel shoelace area times row spacing times
   * column spacing (anisotropic voxels).
   */
  public Optional<Double> getAreaMm(ImageSpacing spacing) {
    if (spacing == null) {
      return Optional.empty();
    }
    double row = spacing.rowMm();
    double col = spacing.colMm();
    if (row <= 0 || col <= 0 || !Double.isFinite(row) || !Double.isFinite(col)) {
      return Optional.empty();
    }
    if (getPts().size() < 3) {
      return Optional.empty();
    }
    return Optional.of(getAreaValue() * Math.abs(row * col));
  }

  @Override
  public void buildShape() {
    var pts = getPts();
    if (pts.size() < 3) {
      setShape(null);
      return;
    }
    Path2D path = new Path2D.Double();
    Point2D.Double first = pts.getFirst();
    path.moveTo(first.x, first.y);
    for (int i = 1; i < pts.size(); i++) {
      Point2D.Double p = pts.get(i);
      path.lineTo(p.x, p.y);
    }
    path.closePath();
    setShape(path);
  }

  @Override
  protected AbstractGraphic newInstance() {
    return new PolygonGraphic();
  }
}
