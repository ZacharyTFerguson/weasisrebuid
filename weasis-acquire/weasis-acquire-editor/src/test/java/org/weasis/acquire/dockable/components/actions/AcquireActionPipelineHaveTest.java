/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.dockable.components.actions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import org.junit.jupiter.api.Test;
import org.weasis.acquire.dockable.components.actions.contrast.ContrastAction;
import org.weasis.acquire.dockable.components.actions.rectify.RectifyAction;
import org.weasis.acquire.explorer.AcquireImageValues;
import org.weasis.acquire.graphics.CropRectangleGraphic;

class AcquireActionPipelineHaveTest {

  @Test
  void rectifyActionRotatesClockwiseBySnappedDegrees() {
    BufferedImage src = new BufferedImage(2, 3, BufferedImage.TYPE_INT_RGB);
    src.setRGB(0, 0, 0xFF0000);
    AcquireImageValues values = new AcquireImageValues();
    values.setRotation(90);
    BufferedImage out = new RectifyAction().apply(src, values);
    assertEquals(3, out.getWidth());
    assertEquals(2, out.getHeight());
    assertEquals(0xFF0000, out.getRGB(2, 0) & 0xFFFFFF);
  }

  @Test
  void contrastActionAppliesWindowOffset() {
    BufferedImage src = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
    src.setRGB(0, 0, 0x808080);
    AcquireImageValues values = new AcquireImageValues();
    values.setContrast(1.0f);
    values.setBrightness(20);
    BufferedImage out = new ContrastAction().apply(src, values);
    int gray = out.getRGB(0, 0) & 0xFF;
    assertEquals(148, gray);
  }

  @Test
  void cropGraphicUsesPendingCropRectangle() {
    BufferedImage src = new BufferedImage(4, 4, BufferedImage.TYPE_INT_RGB);
    src.setRGB(1, 1, 0x00FF00);
    CropRectangleGraphic graphic = new CropRectangleGraphic(new Rectangle(1, 1, 1, 1));
    BufferedImage out = graphic.crop(src);
    assertEquals(1, out.getWidth());
    assertEquals(1, out.getHeight());
    assertEquals(0x00FF00, out.getRGB(0, 0) & 0xFFFFFF);
  }

  @Test
  void acquireActionAppliesRotateThenCropThenContrast() {
    BufferedImage src = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
    src.setRGB(0, 0, 0xFF0000);
    AcquireImageValues values = new AcquireImageValues();
    values.setRotation(90);
    values.setCrop(new Rectangle(0, 0, 2, 1));
    values.setContrast(1.0f);
    values.setBrightness(0);
    BufferedImage out = new AcquireAction().apply(src, values);
    assertEquals(2, out.getWidth());
    assertEquals(1, out.getHeight());
    assertEquals(0xFF0000, out.getRGB(1, 0) & 0xFFFFFF);
  }

  @Test
  void abstractAcquireActionAppliesPendingValuesToSessionImage() {
    BufferedImage src = new BufferedImage(1, 2, BufferedImage.TYPE_INT_RGB);
    src.setRGB(0, 0, 0x0000FF);
    AbstractAcquireAction action = new AbstractAcquireAction();
    action.setSource(src);
    action.values().setRotation(90);
    BufferedImage out = action.apply();
    assertNotNull(out);
    assertEquals(2, out.getWidth());
    assertEquals(1, out.getHeight());
    assertEquals(0x0000FF, out.getRGB(1, 0) & 0xFFFFFF);
    assertSame(src, action.getSource());
  }

  @Test
  void abstractAcquireActionWithoutImageReturnsNull() {
    assertNull(new AbstractAcquireAction().apply());
  }
}
