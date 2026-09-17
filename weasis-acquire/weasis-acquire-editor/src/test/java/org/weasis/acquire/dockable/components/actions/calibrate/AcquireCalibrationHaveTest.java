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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.weasis.acquire.explorer.AcquireImageValues;
import org.weasis.acquire.graphics.CalibrationGraphic;
import org.weasis.acquire.utils.GraphicHelper;
import org.weasis.core.api.image.util.Unit;

class AcquireCalibrationHaveTest {

  @Test
  void tenPixelLineWithTwentyMillimetresIsTwoMmPerPixel() {
    CalibrationGraphic line = new CalibrationGraphic(0, 0, 10, 0);
    assertEquals(10.0, line.pixelLength(), 1e-9);
    AcquireImageValues values = new AcquireImageValues();
    CalibrationAction action = new CalibrationAction();
    action.setKnownLength(20.0, Unit.MILLIMETER);
    assertEquals(2.0, action.apply(values, line), 1e-9);
    assertEquals(2.0, values.getCalibrationMmPerPixel(), 1e-9);
    assertTrue(values.isCalibrated());
  }

  @Test
  void centimetreKnownLengthConvertsThroughGraphicHelper() {
    CalibrationGraphic line = new CalibrationGraphic(0, 0, 0, 25);
    CalibrationPanel panel = new CalibrationPanel();
    panel.setGraphic(line);
    panel.setKnownLength(1.0, Unit.CENTIMETER);
    AcquireImageValues values = new AcquireImageValues();
    assertEquals(0.4, panel.apply(values), 1e-9);
    assertEquals(
        GraphicHelper.mmPerPixel(1.0, Unit.CENTIMETER, 25.0),
        values.getCalibrationMmPerPixel(),
        1e-9);
  }

  @Test
  void pixelUnitDoesNotCalibrate() {
    CalibrationGraphic line = new CalibrationGraphic(0, 0, 8, 0);
    AcquireImageValues values = new AcquireImageValues();
    new CalibrationAction().apply(values, line);
    CalibrationPanel panel = new CalibrationPanel();
    panel.setKnownLength(10.0, Unit.PIXEL);
    assertEquals(0.0, panel.apply(values, line), 1e-9);
    assertFalse(values.isCalibrated());
  }

  @Test
  void missingLineLeavesValuesUncalibrated() {
    AcquireImageValues values = new AcquireImageValues();
    CalibrationAction action = new CalibrationAction();
    action.setKnownLength(10.0, Unit.MILLIMETER);
    assertEquals(0.0, action.apply(values), 1e-9);
    assertFalse(values.isCalibrated());
  }
}
