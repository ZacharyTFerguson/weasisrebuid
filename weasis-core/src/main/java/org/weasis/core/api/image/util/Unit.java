/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.image.util;

/** Spatial calibration units. Uncalibrated graphics fall back to PIXEL. */
public enum Unit {
  PIXEL("px", 1.0),
  MILLIMETER("mm", 1.0),
  CENTIMETER("cm", 10.0),
  METER("m", 1000.0),
  INCH("in", 25.4);

  private final String symbol;
  private final double convMm;

  Unit(String symbol, double convMm) {
    this.symbol = symbol;
    this.convMm = convMm;
  }

  public String getSymbol() {
    return symbol;
  }

  public double getConvMm() {
    return convMm;
  }
}
