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
import static org.junit.jupiter.api.Assertions.assertSame;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.ui.editor.image.ViewerPlugin;

class UICoreViewerTabsHaveTest {

  @Test
  void openTwoPluginsAddsTwoTabs() {
    JFrame win = new JFrame();
    JTabbedPane tabs = new JTabbedPane();
    win.setLayout(new BorderLayout());
    win.add(tabs, BorderLayout.CENTER);
    UICore core = new UICore();
    core.setApplicationWindow(win);
    ViewerPlugin<?> a = plugin("A");
    ViewerPlugin<?> b = plugin("B");
    core.openViewerPlugin(a);
    core.openViewerPlugin(b);
    assertEquals(2, tabs.getTabCount());
    assertSame(b, tabs.getSelectedComponent());
    core.closeViewerPlugin(a);
    assertEquals(1, tabs.getTabCount());
    assertSame(b, tabs.getSelectedComponent());
  }

  static ViewerPlugin<?> plugin(String name) {
    return new ViewerPlugin<MediaElement>(name) {};
  }
}
