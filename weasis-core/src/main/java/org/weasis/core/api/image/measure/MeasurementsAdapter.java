/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.image.measure;

import org.weasis.core.api.image.util.Unit;

public class MeasurementsAdapter {
  private final double calibRatio;
  private final Unit unit;
  private final double offsetX;
  private final double offsetY;

  public MeasurementsAdapter(double calibRatio, Unit unit) {
    this(calibRatio, unit, 0, 0);
  }

  public MeasurementsAdapter(double calibRatio, Unit unit, double offsetX, double offsetY) {
    this.calibRatio = calibRatio <= 0 ? 1.0 : calibRatio;
    this.unit = unit == null ? Unit.PIXEL : unit;
    this.offsetX = offsetX;
    this.offsetY = offsetY;
  }

  public double getCalibRatio() {
    return calibRatio;
  }

  public Unit getUnit() {
    return unit;
  }

  public double getOffsetX() {
    return offsetX;
  }

  public double getOffsetY() {
    return offsetY;
  }

  public double getLength(double pixelDistance) {
    return pixelDistance * calibRatio;
  }
}
