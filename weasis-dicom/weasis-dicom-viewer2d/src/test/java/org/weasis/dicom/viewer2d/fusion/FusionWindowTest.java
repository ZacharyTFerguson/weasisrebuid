/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.fusion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.weasis.core.ui.model.graphic.imp.seg.ByteLutAlpha;

class FusionWindowTest {

  @Test
  void suvWindowClampedFourToThirty() {
    FusionWindow.Range small = FusionWindow.suv(2.1);
    assertEquals(1.0, small.min(), 1e-9);
    assertEquals(4.0, small.max(), 1e-9);
    assertEquals("SUVbw", small.unit());
    FusionWindow.Range mid = FusionWindow.suv(12.2);
    assertEquals(13.0, mid.max(), 1e-9);
    FusionWindow.Range huge = FusionWindow.suv(50);
    assertEquals(30.0, huge.max(), 1e-9);
    FusionWindow.Range stored = FusionWindow.stored(800, "BQML");
    assertEquals(0.0, stored.min(), 1e-9);
    assertEquals(800.0, stored.max(), 1e-9);
  }

  @Test
  void overlayAlphaReachesOpacityInLowerTenth() {
    FusionWindow.Range win = new FusionWindow.Range(1, 11, "SUVbw");
    assertEquals(0.0, FusionWindow.overlayAlpha(1.05, win, 0.8), 1e-9);
    assertEquals(0.8, FusionWindow.overlayAlpha(2.0, win, 0.8), 1e-9);
    assertEquals(0.8, FusionWindow.overlayAlpha(6.0, win, 0.8), 1e-9);
  }

  @Test
  void compositeBaseZeroShowsOverlayOnBlack() {
    assertEquals(5.0, FusionWindow.composite(100, 5, 1.0, 0.0), 1e-9);
    assertEquals(50.0, FusionWindow.composite(100, 0, 0.5, 1.0), 1e-9);
  }

  @Test
  void colorScaleHiddenUnder350px() {
    assertFalse(FusionWindow.drawColorScale(349));
    assertTrue(FusionWindow.drawColorScale(350));
  }

  @Test
  void byteLutAlphaFusionRamp() {
    ByteLutAlpha ramp = ByteLutAlpha.fusionRamp();
    assertEquals(0, ramp.alphaAt(0));
    assertTrue(ramp.alphaAt(30) > 200);
    assertEquals(255, ramp.alphaAt(255));
  }
}
