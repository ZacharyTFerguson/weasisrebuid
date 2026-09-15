/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.eclipse.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.mpr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ArcBallHaveTest {

  @Test
  void dragEastRotatesViewDirectionTowardNegativeZ() {
    ArcBallController ball = new ArcBallController();
    ball.begin(0.0, 0.0);
    ball.drag(1.0, 0.0);
    AxisDirection u = ball.u();
    assertTrue(Math.abs(u.x()) < 1e-9, "u.x ~ 0 after 90° about +Y");
    assertTrue(Math.abs(u.y()) < 1e-9, "u.y stays 0");
    assertEquals(-1.0, u.z(), 1e-9);
    AxisDirection v = ball.v();
    assertEquals(0.0, v.x(), 1e-9);
    assertEquals(1.0, v.y(), 1e-9);
    assertEquals(0.0, v.z(), 1e-9);
  }

  @Test
  void axesFeedObliqueMpr() {
    ArcBallController ball = new ArcBallController();
    AxesControl axes = ball.axes();
    assertEquals(1.0, axes.u().x(), 1e-9);
    assertEquals(1.0, axes.v().y(), 1e-9);

    VolumeByte volume = new VolumeByte(2, 2, 2);
    volume.setValue(1, 0, 0, 42);
    double[][] slice = new ObliqueMpr().slice(volume, axes, 2, 1);
    assertEquals(42.0, slice[0][1], 1e-9);
  }

  @Test
  void draggedAxesRotateObliqueSampleAxis() {
    ArcBallController ball = new ArcBallController();
    ball.begin(0.0, 0.0);
    ball.drag(1.0, 0.0);
    VolumeByte volume = new VolumeByte(1, 1, 2);
    volume.setValue(0, 0, 0, 7);
    AxesControl axes = ball.axes();
    axes.setOrigin(0, 0, 0);
    double[][] slice = new ObliqueMpr().slice(volume, axes, 2, 1);
    assertEquals(7.0, slice[0][0], 1e-9);
    assertEquals(0.0, slice[0][1], 1e-9);
  }
}
