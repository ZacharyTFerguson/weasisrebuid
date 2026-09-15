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

import java.awt.geom.Point2D;
import java.util.Optional;
import org.weasis.core.api.image.measure.ImageSpacing;
import org.weasis.core.ui.model.graphic.AbstractDragGraphic;
import org.weasis.core.ui.model.graphic.AbstractGraphic;
import org.weasis.core.ui.model.graphic.imp.line.LineGraphic;

public class AngleToolGraphic extends AbstractDragGraphic {

  /** Vertex handle index; handles 0 and 2 are the arm endpoints. */
  private static final int VERTEX = 1;

  public AngleToolGraphic() {
    super(3);
  }

  /**
   * Included angle in degrees from pixel-space arm directions (handle 1 = vertex). Dimensionless:
   * anisotropic spacing does not affect this value.
   */
  public Optional<Double> getAngleDegrees() {
    Point2D.Double vertex = getHandlePoint(VERTEX);
    Point2D.Double armA = getHandlePoint(0);
    Point2D.Double armB = getHandlePoint(2);
    if (vertex == null || armA == null || armB == null) {
      return Optional.empty();
    }
    double v1x = armA.x - vertex.x;
    double v1y = armA.y - vertex.y;
    double v2x = armB.x - vertex.x;
    double v2y = armB.y - vertex.y;
    double len1 = Math.hypot(v1x, v1y);
    double len2 = Math.hypot(v2x, v2y);
    if (len1 <= 0 || len2 <= 0) {
      return Optional.empty();
    }
    double cos = (v1x * v2x + v1y * v2y) / (len1 * len2);
    cos = Math.max(-1.0, Math.min(1.0, cos));
    return Optional.of(Math.toDegrees(Math.acos(cos)));
  }

  /** Pixel length of arm {@code armIndex}: 0 = vertex→handle 0, 1 = vertex→handle 2. */
  public double getArmLengthPx(int armIndex) {
    Point2D.Double vertex = getHandlePoint(VERTEX);
    if (vertex == null) {
      return 0;
    }
    Point2D.Double end = armIndex == 0 ? getHandlePoint(0) : getHandlePoint(2);
    if (end == null) {
      return 0;
    }
    return vertex.distance(end);
  }

  /**
   * Physical arm length in mm (same row/col rule as {@link LineGraphic#getLengthMm}). Empty when
   * spacing is unusable.
   */
  public Optional<Double> getArmLengthMm(int armIndex, ImageSpacing spacing) {
    if (armIndex != 0 && armIndex != 1) {
      return Optional.empty();
    }
    Point2D.Double vertex = getHandlePoint(VERTEX);
    Point2D.Double end = armIndex == 0 ? getHandlePoint(0) : getHandlePoint(2);
    if (vertex == null || end == null) {
      return Optional.empty();
    }
    LineGraphic segment = new LineGraphic();
    segment.setHandlePoint(0, vertex);
    segment.setHandlePoint(1, end);
    return segment.getLengthMm(spacing);
  }

  @Override
  public void buildShape() {
    setShape(null);
  }

  @Override
  protected AbstractGraphic newInstance() {
    return new AngleToolGraphic();
  }
}
