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
}
