/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.mpr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import org.junit.jupiter.api.Test;
import org.weasis.core.ui.model.layer.LayerType;
import org.weasis.dicom.viewer2d.mip.MipView;

class MprShortcutHaveTest {

  @Test
  void altKeysCenterShowAndCycleMipOnTheBoundController() {
    MprController controller = new MprController();
    MprView axial = controller.getAxial();
    MprView coronal = controller.getCoronal();
    BufferedImage image = gray(4);
    axial.setSourceImage(image);
    coronal.setSourceImage(image);
    axial.setCrosshair(0, 0);
    coronal.setCrosshair(0, 0);
    axial.getEventManager().keyPressed(key(axial, KeyEvent.VK_X, InputEvent.ALT_DOWN_MASK));
    assertEquals(2, axial.getCrosshairX());
    assertEquals(2, axial.getCrosshairY());
    assertEquals(0, coronal.getCrosshairX());
    axial
        .getEventManager()
        .keyPressed(
            key(axial, KeyEvent.VK_X, InputEvent.ALT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK));
    assertEquals(2, coronal.getCrosshairX());
    assertTrue(axial.isCrosshairCenterVisible());
    axial.getEventManager().keyPressed(key(axial, KeyEvent.VK_C, InputEvent.ALT_DOWN_MASK));
    assertFalse(axial.isCrosshairCenterVisible());
    assertTrue(coronal.isCrosshairCenterVisible());
    assertFalse(axial.cineListener().isCineRunning());
    axial
        .getEventManager()
        .keyPressed(
            key(axial, KeyEvent.VK_C, InputEvent.ALT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK));
    assertTrue(axial.isCrosshairCenterVisible());
    assertTrue(coronal.isCrosshairCenterVisible());
    axial
        .getEventManager()
        .keyPressed(
            key(axial, KeyEvent.VK_C, InputEvent.ALT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK));
    assertFalse(axial.isCrosshairCenterVisible());
    assertFalse(coronal.isCrosshairCenterVisible());
    assertTrue(axial.isLayerVisible(LayerType.CROSSLINES));
    axial.getEventManager().keyPressed(key(axial, KeyEvent.VK_V, InputEvent.ALT_DOWN_MASK));
    assertFalse(axial.isLayerVisible(LayerType.CROSSLINES));
    assertTrue(coronal.isLayerVisible(LayerType.CROSSLINES));
    axial
        .getEventManager()
        .keyPressed(
            key(axial, KeyEvent.VK_V, InputEvent.ALT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK));
    assertTrue(axial.isLayerVisible(LayerType.CROSSLINES));
    assertTrue(coronal.isLayerVisible(LayerType.CROSSLINES));
    axial
        .getEventManager()
        .keyPressed(
            key(axial, KeyEvent.VK_V, InputEvent.ALT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK));
    assertFalse(axial.isLayerVisible(LayerType.CROSSLINES));
    assertFalse(coronal.isLayerVisible(LayerType.CROSSLINES));
    assertEquals(MipView.Type.NONE, axial.getMip().getType());
    axial
        .getEventManager()
        .keyPressed(
            key(axial, KeyEvent.VK_B, InputEvent.ALT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK));
    assertEquals(MipView.Type.MIN, axial.getMip().getType());
    assertEquals(MipView.Type.MIN, coronal.getMip().getType());
    axial
        .getEventManager()
        .keyPressed(
            key(axial, KeyEvent.VK_B, InputEvent.ALT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK));
    assertEquals(MipView.Type.MEAN, axial.getMip().getType());
    axial
        .getEventManager()
        .keyPressed(
            key(axial, KeyEvent.VK_B, InputEvent.ALT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK));
    assertEquals(MipView.Type.MAX, axial.getMip().getType());
    axial
        .getEventManager()
        .keyPressed(
            key(axial, KeyEvent.VK_B, InputEvent.ALT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK));
    assertEquals(MipView.Type.NONE, axial.getMip().getType());
  }

  @Test
  void altWheelChangesThicknessOnTheSelectedAxisOnly() {
    MprController controller = new MprController();
    MprView coronal = controller.getCoronal();
    coronal.getEventManager().mouseWheelMoved(wheel(coronal, -1));
    assertEquals(2, coronal.getMip().getThickness());
    assertEquals(1, controller.getAxial().getMip().getThickness());
    coronal.getEventManager().mouseWheelMoved(wheel(coronal, 1));
    assertEquals(1, coronal.getMip().getThickness());
  }

  static KeyEvent key(MprView view, int code, int mods) {
    return new KeyEvent(view, KeyEvent.KEY_PRESSED, 0L, mods, code, (char) code);
  }

  static MouseWheelEvent wheel(MprView view, int rotation) {
    return new MouseWheelEvent(
        view,
        MouseWheelEvent.MOUSE_WHEEL,
        0L,
        InputEvent.ALT_DOWN_MASK,
        0,
        0,
        0,
        false,
        MouseWheelEvent.WHEEL_UNIT_SCROLL,
        1,
        rotation);
  }

  static BufferedImage gray(int size) {
    return new BufferedImage(size, size, BufferedImage.TYPE_BYTE_GRAY);
  }
}
