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

/** Cobb measurement: upper endplate handles 0–1, lower endplate handles 2–3. */
public class CobbToolGraphic extends AbstractDragGraphic {

  public CobbToolGraphic() {
    super(4);
  }

  /**
   * Cobb angle in degrees from pixel-space endplate directions (0→1 and 2→3). Dimensionless:
   * anisotropic spacing does not affect this value.
   */
  public Optional<Double> getCobbAngleDegrees() {
    Optional<double[]> d0 = endplateDirection(0);
    Optional<double[]> d1 = endplateDirection(1);
    if (d0.isEmpty() || d1.isEmpty()) {
      return Optional.empty();
    }
    double[] v1 = d0.get();
    double[] v2 = d1.get();
    double len1 = Math.hypot(v1[0], v1[1]);
    double len2 = Math.hypot(v2[0], v2[1]);
    if (len1 <= 0 || len2 <= 0) {
      return Optional.empty();
    }
    double cos = (v1[0] * v2[0] + v1[1] * v2[1]) / (len1 * len2);
    cos = Math.max(-1.0, Math.min(1.0, cos));
    return Optional.of(Math.toDegrees(Math.acos(cos)));
  }

  /** Pixel length of endplate {@code endplateIndex}: 0 = handles 0–1, 1 = handles 2–3. */
  public double getEndplateLengthPx(int endplateIndex) {
    Point2D.Double a = endplateStart(endplateIndex);
    Point2D.Double b = endplateEnd(endplateIndex);
    if (a == null || b == null) {
      return 0;
    }
    return a.distance(b);
  }

  /**
   * Physical endplate length in mm (same row/col rule as {@link LineGraphic#getLengthMm}). Empty
   * when spacing is unusable.
   */
  public Optional<Double> getEndplateLengthMm(int endplateIndex, ImageSpacing spacing) {
    if (endplateIndex != 0 && endplateIndex != 1) {
      return Optional.empty();
    }
    Point2D.Double a = endplateStart(endplateIndex);
    Point2D.Double b = endplateEnd(endplateIndex);
    if (a == null || b == null) {
      return Optional.empty();
    }
    LineGraphic segment = new LineGraphic();
    segment.setHandlePoint(0, a);
    segment.setHandlePoint(1, b);
    return segment.getLengthMm(spacing);
  }

  private Optional<double[]> endplateDirection(int endplateIndex) {
    Point2D.Double a = endplateStart(endplateIndex);
    Point2D.Double b = endplateEnd(endplateIndex);
    if (a == null || b == null) {
      return Optional.empty();
    }
    return Optional.of(new double[] {b.x - a.x, b.y - a.y});
  }

  private Point2D.Double endplateStart(int endplateIndex) {
    return endplateIndex == 0 ? getHandlePoint(0) : getHandlePoint(2);
  }

  private Point2D.Double endplateEnd(int endplateIndex) {
    return endplateIndex == 0 ? getHandlePoint(1) : getHandlePoint(3);
  }

  @Override
  public void buildShape() {
    setShape(null);
  }

  @Override
  protected AbstractGraphic newInstance() {
    return new CobbToolGraphic();
  }
}
