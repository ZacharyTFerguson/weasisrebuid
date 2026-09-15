/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.operations.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import org.junit.jupiter.api.Test;
import org.weasis.acquire.AcquireObject;
import org.weasis.acquire.dockable.components.actions.contrast.ContrastAction;
import org.weasis.acquire.explorer.AcquireImageInfo;
import org.weasis.acquire.explorer.AcquireImageValues;
import org.weasis.acquire.operations.OpValueChanged;

class AutoLevelListenerHaveTest {

  @Test
  void histogramStretchMapsMinMaxThroughContrastAction() {
    BufferedImage src = ramp(64, 192);
    AcquireImageValues values = new AcquireImageValues();
    AutoLevelListener listener = new AutoLevelListener();
    listener.stretch(src, values);

    float contrast = 255.0f / (192 - 64);
    assertEquals(contrast, values.getContrast(), 1e-5);
    assertEquals(-64 * contrast, values.getBrightness(), 1e-5);

    BufferedImage out = new ContrastAction().apply(src, values);
    assertEquals(0, gray(out, 0));
    assertEquals(255, gray(out, 1));
  }

  @Test
  void applyUsesBoundAcquireObjectSession() {
    BufferedImage src = ramp(32, 224);
    AcquireImageInfo info = new AcquireImageInfo();
    AcquireImageValues values = new AcquireImageValues();
    AutoLevelListener listener = new AutoLevelListener();
    listener.setImage(src);
    listener.setImageInfo(info);
    listener.setImageValues(values);
    assertInstanceOf(OpValueChanged.class, listener);
    assertInstanceOf(AcquireObject.class, listener);
    listener.apply();
    assertSame(info, listener.getImageInfo());
    assertSame(src, listener.getImage());
    BufferedImage out = new ContrastAction().apply(src, values);
    assertTrue(gray(out, 0) <= 2);
    assertTrue(gray(out, 1) >= 253);
  }

  @Test
  void flatHistogramLeavesNeutralWindow() {
    BufferedImage src = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
    src.setRGB(0, 0, 0x808080);
    AcquireImageValues values = new AcquireImageValues();
    values.setContrast(1.4f);
    values.setBrightness(12f);
    new AutoLevelListener().stretch(src, values);
    assertEquals(1.0f, values.getContrast(), 1e-5);
    assertEquals(0f, values.getBrightness(), 1e-5);
  }

  static BufferedImage ramp(int dark, int bright) {
    BufferedImage src = new BufferedImage(2, 1, BufferedImage.TYPE_INT_RGB);
    int lo = dark * 0x010101;
    int hi = bright * 0x010101;
    src.setRGB(0, 0, lo);
    src.setRGB(1, 0, hi);
    return src;
  }

  static int gray(BufferedImage image, int x) {
    return image.getRGB(x, 0) & 0xFF;
  }
}
