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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import org.junit.jupiter.api.Test;
import org.weasis.dicom.viewer2d.mpr.VolumeShort;

class FusionLutHaveTest {

  @Test
  void estimatesWindowFromVolumeAndMapsHotIron() {
    VolumeShort pet = new VolumeShort(2, 2, 1);
    pet.setValue(0, 0, 0, 1);
    pet.setValue(1, 0, 0, 2);
    pet.setValue(0, 1, 0, 3);
    pet.setValue(1, 1, 0, 4);
    FusionWindow window = new FusionWindowEstimator().estimate(pet);
    assertEquals(3.0, window.getWindow(), 1e-9);
    assertEquals(2.5, window.getLevel(), 1e-9);
    assertEquals(0, window.indexOf(1));
    assertEquals(255, window.indexOf(4));
    assertEquals(128, window.indexOf(2.5));

    FusionColorScale scale = new FusionColorScale();
    assertTrue(scale.names().contains(FusionColorScale.HOT_IRON));
    assertEquals(Color.BLACK, scale.color(FusionColorScale.HOT_IRON, 0));
    assertEquals(new Color(255, 0, 0), scale.color(FusionColorScale.HOT_IRON, 85));
    assertEquals(Color.WHITE, scale.color(FusionColorScale.HOT_IRON, 255));
    assertEquals(new Color(0, 0, 255), scale.color(FusionColorScale.PET, 0));

    FusionColorBar bar = new FusionColorBar();
    bar.setWindow(window);
    bar.setLut(FusionColorScale.HOT_IRON);
    assertEquals(Color.BLACK, bar.colorFor(1));
    assertEquals(Color.WHITE, bar.colorFor(4));
    assertEquals(256, bar.rgbBar()[0].length);

    FusionState state = new FusionState();
    new FusionAction().applyLut(state, FusionColorScale.PET);
    new FusionAction().applyWindow(state, window);
    new FusionAction().applyOpacity(state, 0.6);
    assertEquals(FusionColorScale.PET, state.getLut());
    assertEquals(3.0, state.getWindow().getWindow(), 1e-9);
    assertEquals(0.6, state.getOpacity(), 1e-9);
  }
}
