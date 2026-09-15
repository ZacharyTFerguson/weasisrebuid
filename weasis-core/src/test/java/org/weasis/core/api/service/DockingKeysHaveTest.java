/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.ui.editor.image.ViewerPlugin;

class DockingKeysHaveTest {

  @Test
  void ctrlTabCyclesAndCtrlMWEAndNChangeDockingState() {
    UICore core = new UICore();
    ViewerPlugin<?> a = plugin("A");
    ViewerPlugin<?> b = plugin("B");
    ViewerPlugin<?> c = plugin("C");
    core.openViewerPlugin(a);
    core.openViewerPlugin(b);
    core.openViewerPlugin(c);
    assertSame(c, core.getSelectedViewerPlugin());
    assertTrue(core.handleDockingKey(key(c, KeyEvent.VK_TAB, InputEvent.CTRL_DOWN_MASK)));
    assertSame(a, core.getSelectedViewerPlugin());
    assertTrue(
        core.handleDockingKey(
            key(a, KeyEvent.VK_TAB, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK)));
    assertSame(c, core.getSelectedViewerPlugin());
    assertTrue(core.handleDockingKey(key(c, KeyEvent.VK_M, InputEvent.CTRL_DOWN_MASK)));
    assertEquals(ViewerPlugin.DockingState.MAXIMIZED, c.getDockingState());
    assertTrue(core.handleDockingKey(key(c, KeyEvent.VK_M, InputEvent.CTRL_DOWN_MASK)));
    assertEquals(ViewerPlugin.DockingState.NORMAL, c.getDockingState());
    assertTrue(core.handleDockingKey(key(c, KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK)));
    assertEquals(ViewerPlugin.DockingState.EXTERNALIZED, c.getDockingState());
    assertTrue(core.handleDockingKey(key(c, KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK)));
    assertEquals(ViewerPlugin.DockingState.NORMAL, c.getDockingState());
    assertTrue(
        core.handleDockingKey(
            key(c, KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK)));
    assertTrue(core.isDockingListVisible());
    assertEquals(3, core.dockingList().size());
    assertTrue(core.handleDockingKey(key(c, KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK)));
    assertEquals(2, core.getOpenViewerPlugins().size());
    assertSame(b, core.getSelectedViewerPlugin());
  }

  @Test
  void keysWithoutCtrlAreIgnored() {
    UICore core = new UICore();
    ViewerPlugin<?> a = plugin("A");
    core.openViewerPlugin(a);
    assertFalse(core.handleDockingKey(key(a, KeyEvent.VK_M, 0)));
    assertEquals(ViewerPlugin.DockingState.NORMAL, a.getDockingState());
  }

  static ViewerPlugin<?> plugin(String name) {
    return new ViewerPlugin<MediaElement>(name) {};
  }

  static KeyEvent key(ViewerPlugin<?> plugin, int code, int mods) {
    return new KeyEvent(plugin, KeyEvent.KEY_PRESSED, 0L, mods, code, (char) code);
  }
}
