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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.image.util.WindLevelParameters;

class View2dPresetSegHaveTest {

  @Test
  void digitKeysApplyVoiPresetsAndZeroRestoresFile() {
    View2d view = new View2d();
    view.setPresets(List.of(new WindLevelParameters(80, 40), new WindLevelParameters(1500, 300)));
    view.getEventManager().keyPressed(key(view, KeyEvent.VK_1, 0));
    assertEquals(80.0, view.getWindow(), 1e-9);
    assertEquals(40.0, view.getLevel(), 1e-9);
    view.getEventManager().keyPressed(key(view, KeyEvent.VK_2, 0));
    assertEquals(1500.0, view.getWindow(), 1e-9);
    view.getEventManager().keyPressed(key(view, KeyEvent.VK_0, 0));
    assertEquals(400.0, view.getWindow(), 1e-9);
    assertEquals(40.0, view.getLevel(), 1e-9);
  }

  @Test
  void loadedSeriesDigitKeysUseDatasetVoiAndZeroKeepsDefaultWl() {
    View2d view = new View2d();
    view.load(ctWithVoiPresets());
    assertEquals(400.0, view.getWindow(), 1e-9);
    assertEquals(40.0, view.getLevel(), 1e-9);
    assertEquals(2, view.getPresets().size());
    view.getEventManager().keyPressed(key(view, KeyEvent.VK_2, 0));
    assertEquals(1500.0, view.getWindow(), 1e-9);
    assertEquals(300.0, view.getLevel(), 1e-9);
    view.getEventManager().keyPressed(key(view, KeyEvent.VK_0, 0));
    assertEquals(400.0, view.getWindow(), 1e-9);
    assertEquals(40.0, view.getLevel(), 1e-9);
    view.getEventManager().keyPressed(key(view, KeyEvent.VK_1, 0));
    assertEquals(400.0, view.getWindow(), 1e-9);
  }

  @Test
  void dxWithoutVoiKeepsDataRangeOnZeroAndNine() {
    View2d view = new View2d();
    view.load(handDxPattern());
    double window = view.getWindow();
    double level = view.getLevel();
    assertTrue(window < 250, "DX without VOI must use data-range, not CT 400/40");
    view.getEventManager().keyPressed(key(view, KeyEvent.VK_9, 0));
    assertEquals(window, view.getWindow(), 1e-9);
    view.getEventManager().keyPressed(key(view, KeyEvent.VK_0, 0));
    assertEquals(window, view.getWindow(), 1e-9);
    assertEquals(level, view.getLevel(), 1e-9);
  }

  @Test
  void digitKeysApplyOnlyToFocusedLayoutView() {
    View2dContainer container = new View2dContainer();
    container.setLayoutCount(2);
    View2d first = container.getLayoutViews().get(0);
    View2d second = container.getLayoutViews().get(1);
    first.load(ctWithVoiPresets());
    second.load(ctWithVoiPresets());
    container.setLayoutIndex(1);
    second.getEventManager().keyPressed(key(second, KeyEvent.VK_2, 0));
    assertEquals(400.0, first.getWindow(), 1e-9);
    assertEquals(1500.0, second.getWindow(), 1e-9);
    container.setLayoutIndex(0);
    first.getEventManager().keyPressed(key(first, KeyEvent.VK_2, 0));
    assertEquals(1500.0, first.getWindow(), 1e-9);
  }

  @Test
  void pluginApplyPresetUsesFocusedLayoutViewWithoutViewFocus() {
    View2dContainer container = new View2dContainer();
    container.setLayoutCount(2);
    View2d first = container.getLayoutViews().get(0);
    View2d second = container.getLayoutViews().get(1);
    first.load(ctWithVoiPresets());
    second.load(ctWithVoiPresets());
    container.setLayoutIndex(1);
    container.applyPreset(2);
    assertEquals(400.0, first.getWindow(), 1e-9);
    assertEquals(1500.0, second.getWindow(), 1e-9);
  }

  @Test
  void altSTogglesSegVisibilityPolicy() {
    View2d view = new View2d();
    assertTrue(view.isSegmentationsVisible());
    assertTrue(view.getSegVisibility().isVisible());
    view.getEventManager().keyPressed(key(view, KeyEvent.VK_S, InputEvent.ALT_DOWN_MASK));
    assertFalse(view.isSegmentationsVisible());
    assertFalse(view.getSegVisibility().isVisible());
  }

  static KeyEvent key(View2d view, int code, int mods) {
    return new KeyEvent(view, KeyEvent.KEY_PRESSED, 0L, mods, code, (char) code);
  }

  static Attributes ctWithVoiPresets() {
    Attributes dcm = new Attributes();
    dcm.setString(Tag.PhotometricInterpretation, VR.CS, "MONOCHROME2");
    dcm.setInt(Tag.SamplesPerPixel, VR.US, 1);
    dcm.setInt(Tag.Rows, VR.US, 2);
    dcm.setInt(Tag.Columns, VR.US, 2);
    dcm.setInt(Tag.BitsAllocated, VR.US, 16);
    dcm.setInt(Tag.BitsStored, VR.US, 16);
    dcm.setInt(Tag.HighBit, VR.US, 15);
    dcm.setInt(Tag.PixelRepresentation, VR.US, 1);
    dcm.setDouble(Tag.WindowCenter, VR.DS, 40, 300);
    dcm.setDouble(Tag.WindowWidth, VR.DS, 400, 1500);
    dcm.setInt(Tag.PixelData, VR.OW, 0, 0, 0, 0);
    return dcm;
  }

  static Attributes handDxPattern() {
    Attributes dcm = new Attributes();
    dcm.setString(Tag.PhotometricInterpretation, VR.CS, "MONOCHROME2");
    dcm.setString(Tag.Modality, VR.CS, "DX");
    dcm.setInt(Tag.SamplesPerPixel, VR.US, 1);
    dcm.setInt(Tag.Rows, VR.US, 8);
    dcm.setInt(Tag.Columns, VR.US, 8);
    dcm.setInt(Tag.BitsAllocated, VR.US, 8);
    dcm.setInt(Tag.BitsStored, VR.US, 8);
    dcm.setInt(Tag.HighBit, VR.US, 7);
    dcm.setInt(Tag.PixelRepresentation, VR.US, 0);
    byte[] px = new byte[64];
    for (int y = 0; y < 8; y++) {
      for (int x = 0; x < 8; x++) {
        px[y * 8 + x] = (x >= 3 && x <= 5) ? (byte) 220 : 40;
      }
    }
    dcm.setBytes(Tag.PixelData, VR.OB, px);
    return dcm;
  }
}
