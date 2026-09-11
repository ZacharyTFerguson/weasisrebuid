/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.awt.geom.Point2D;
import org.junit.jupiter.api.Test;
import org.weasis.core.ui.model.graphic.imp.line.LineGraphic;

class View2dGraphicTest {

  @Test
  void lineGraphicLengthOnView() {
    View2d view = new View2d();
    LineGraphic line = new LineGraphic();
    line.setHandlePoint(0, new Point2D.Double(0, 0));
    line.setHandlePoint(1, new Point2D.Double(3, 4));
    view.addGraphic(line);
    assertEquals(1, view.getGraphicList().size());
    assertEquals(5.0, line.getLength(), 1e-9);
  }

  @Test
  void monitorCalibrationIsNotSessionManual() {
    View2d view = new View2d();
    view.setMonitorCalibrationMmPerPixel(0.2);
    view.setSessionManualCalibrationMmPerPixel(0.5);
    assertNotEquals(
        view.getMonitorCalibrationMmPerPixel(), view.getSessionManualCalibrationMmPerPixel());
    view.setFreezeParameters(true);
    view.setFreezeImage(false);
    assertNotEquals(view.isFreezeParameters(), view.isFreezeImage());
  }
}
