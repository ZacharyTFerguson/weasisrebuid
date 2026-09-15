/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.eclipse.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.dockable.components.actions.rectify;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.weasis.acquire.dockable.components.actions.contrast.ContrastPanel;
import org.weasis.acquire.explorer.AcquireImageValues;
import org.weasis.acquire.operations.impl.RotationActionListener;

class AcquireRectifyChromeHaveTest {

  @Test
  void rotate90ButtonIncrementsAndNotifies() {
    AcquireImageValues values = new AcquireImageValues();
    int[] notified = {Integer.MIN_VALUE};
    RotationActionListener listener = d -> notified[0] = d;
    Rotate90Button button = new Rotate90Button();
    button.apply(values, listener);
    assertEquals(90, values.getRotation());
    assertEquals(90, notified[0]);
    button.apply(values, listener);
    assertEquals(180, values.getRotation());
    assertEquals(180, notified[0]);
  }

  @Test
  void rotate270ButtonDecrementsWrappingAtZero() {
    AcquireImageValues values = new AcquireImageValues();
    new Rotate270Button().apply(values);
    assertEquals(270, values.getRotation());
    new Rotate270Button().apply(values);
    assertEquals(180, values.getRotation());
  }

  @Test
  void orientationSliderSnapsAndWritesValues() {
    AcquireImageValues values = new AcquireImageValues();
    int[] notified = {Integer.MIN_VALUE};
    OrientationSliderComponent slider = new OrientationSliderComponent();
    slider.setValue(95);
    assertEquals(90, slider.degrees());
    slider.applyTo(values, d -> notified[0] = d);
    assertEquals(90, values.getRotation());
    assertEquals(90, notified[0]);
  }

  @Test
  void rectifyPanelRotateButtonsShareSessionValues() {
    AcquireImageValues values = new AcquireImageValues();
    RectifyPanel panel = new RectifyPanel();
    panel.orientation().setValue(0);
    panel.applyOrientation(values);
    assertEquals(0, values.getRotation());
    panel.rotate90(values);
    assertEquals(90, values.getRotation());
    panel.rotate270(values);
    assertEquals(0, values.getRotation());
  }

  @Test
  void contrastPanelWritesBrightnessAndFactor() {
    AcquireImageValues values = new AcquireImageValues();
    ContrastPanel panel = new ContrastPanel();
    panel.brightness().setValue(20);
    panel.contrast().setValue(150);
    panel.applyTo(values);
    assertEquals(20f, values.getBrightness(), 1e-6f);
    assertEquals(1.5f, values.getContrast(), 1e-6f);
  }
}
