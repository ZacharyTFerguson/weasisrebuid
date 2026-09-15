/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.utils.bean;

/** One measured value with its unit, keyed by a {@link Measurement}. */
public class MeasureItem {

  private final Measurement measurement;
  private final Number value;
  private final String unit;

  public MeasureItem(Measurement measurement, Number value, String unit) {
    this.measurement = measurement;
    this.value = value;
    this.unit = unit == null ? "" : unit;
  }

  public Measurement getMeasurement() {
    return measurement;
  }

  public Number getValue() {
    return value;
  }

  public String getUnit() {
    return unit;
  }
}
