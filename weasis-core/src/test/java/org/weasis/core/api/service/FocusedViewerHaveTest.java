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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.ui.editor.image.ImageViewerPlugin;
import org.weasis.core.ui.editor.image.ViewerPlugin;

class FocusedViewerHaveTest {

  @Test
  void digitFromDockHostAppliesPresetOnSelectedImagePlugin() {
    UICore core = new UICore();
    RecordingPlugin plugin = new RecordingPlugin();
    core.openViewerPlugin(plugin);
    JPanel dockHost = new JPanel();
    assertTrue(core.handleViewerKey(key(dockHost, KeyEvent.VK_3, 0)));
    assertEquals(3, plugin.lastPreset);
    assertTrue(core.handleViewerKey(key(dockHost, KeyEvent.VK_NUMPAD9, 0)));
    assertEquals(9, plugin.lastPreset);
    assertFalse(core.handleViewerKey(key(dockHost, KeyEvent.VK_3, InputEvent.CTRL_DOWN_MASK)));
    assertEquals(9, plugin.lastPreset);
  }

  @Test
  void selectRegistersMissingPluginAndTabWinsOverNonImageSelection() {
    JFrame win = new JFrame();
    JTabbedPane tabs = new JTabbedPane();
    tabs.setName("viewer-tabs");
    win.setLayout(new BorderLayout());
    win.add(tabs, BorderLayout.CENTER);
    UICore core = new UICore();
    core.setApplicationWindow(win);
    ViewerPlugin<?> other = plugin("other");
    core.openViewerPlugin(other);
    RecordingPlugin image = new RecordingPlugin();
    tabs.addTab("2D", image);
    core.setSelectedViewerPlugin(image);
    assertSame(image, core.getSelectedViewerPlugin());
    assertTrue(core.getOpenViewerPlugins().contains(image));
    assertSame(image, core.getFocusedImagePlugin());
    assertTrue(core.handleViewerKey(key(tabs, KeyEvent.VK_1, 0)));
    assertEquals(1, image.lastPreset);
  }

  static ViewerPlugin<?> plugin(String name) {
    return new ViewerPlugin<MediaElement>(name) {};
  }

  static KeyEvent key(Component src, int code, int mods) {
    return new KeyEvent(src, KeyEvent.KEY_PRESSED, 0L, mods, code, (char) code);
  }

  static final class RecordingPlugin extends ImageViewerPlugin<MediaElement> {
    int lastPreset = -1;
    int layout = 1;

    RecordingPlugin() {
      super("rec");
    }

    @Override
    public void applyPreset(int index) {
      lastPreset = index;
    }

    @Override
    public void setLayoutCount(int n) {
      layout = n;
    }

    @Override
    public int getLayoutCount() {
      return layout;
    }
  }
}
