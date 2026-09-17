/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.dockable.components.actions.calibrate;

import org.weasis.acquire.explorer.AcquireImageValues;
import org.weasis.acquire.graphics.CalibrationGraphic;
import org.weasis.core.api.image.util.Unit;

/** Applies a known millimetre length on a calibration line onto pending photo-editor values. */
public class CalibrationAction {

  private final CalibrationPanel panel = new CalibrationPanel();

  public CalibrationPanel panel() {
    return panel;
  }

  public void setGraphic(CalibrationGraphic graphic) {
    panel.setGraphic(graphic);
  }

  public CalibrationGraphic graphic() {
    return panel.getGraphic();
  }

  public void setKnownLength(double knownLength, Unit unit) {
    panel.setKnownLength(knownLength, unit);
  }

  public double apply(AcquireImageValues values) {
    return panel.apply(values);
  }

  public double apply(AcquireImageValues values, CalibrationGraphic graphic) {
    setGraphic(graphic);
    return panel.apply(values, graphic);
  }
}
