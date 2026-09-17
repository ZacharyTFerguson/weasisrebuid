/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.utils;

import java.awt.geom.Point2D;
import org.weasis.core.api.image.util.Unit;

/** Pixel length and mm/pixel helpers for dicomizer photo calibration. */
public class GraphicHelper {

  private GraphicHelper() {}

  public static double pixelLength(Point2D a, Point2D b) {
    if (a == null || b == null) {
      return 0.0;
    }
    return a.distance(b);
  }

  public static double pixelLength(double x1, double y1, double x2, double y2) {
    return pixelLength(new Point2D.Double(x1, y1), new Point2D.Double(x2, y2));
  }

  /**
   * Converts a known real-world length along a pixel-measured line into millimetres per pixel.
   * Pixel units and non-positive lengths leave the image uncalibrated (0).
   */
  public static double mmPerPixel(double knownLength, Unit unit, double pixelLength) {
    if (pixelLength <= 0.0 || unit == null || unit == Unit.PIXEL || knownLength <= 0.0) {
      return 0.0;
    }
    return (knownLength * unit.getConvMm()) / pixelLength;
  }
}
