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

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Optional;
import org.weasis.core.api.image.measure.ImageSpacing;
import org.weasis.core.ui.model.graphic.AbstractDragGraphicArea;
import org.weasis.core.ui.model.graphic.AbstractGraphic;

public class RectangleGraphic extends AbstractDragGraphicArea {

  public RectangleGraphic() {
    super(2);
  }

  /**
   * Physical area in mm² from the two-corner bbox: pixel {@link #getAreaValue()} times row spacing
   * times column spacing (anisotropic voxels).
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
    if (getHandlePoint(0) == null || getHandlePoint(1) == null) {
      return Optional.empty();
    }
    double pixelArea = getAreaValue();
    if (pixelArea == 0) {
      return Optional.empty();
    }
    return Optional.of(pixelArea * Math.abs(row * col));
  }

  @Override
  public void buildShape() {
    Point2D.Double a = getHandlePoint(0);
    Point2D.Double b = getHandlePoint(1);
    if (a == null || b == null) {
      setShape(null);
      return;
    }
    setShape(
        new Rectangle2D.Double(
            Math.min(a.x, b.x), Math.min(a.y, b.y), Math.abs(b.x - a.x), Math.abs(b.y - a.y)));
  }

  @Override
  protected AbstractGraphic newInstance() {
    return new RectangleGraphic();
  }
}
