/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.editor;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Component;
import javax.swing.JButton;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.gui.Insertable;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.ui.editor.image.ViewerPlugin;
import org.weasis.core.ui.util.ToolBarContainer;
import org.weasis.core.ui.util.WtoolBar;

class SeriesViewerUiHaveTest {

  @Test
  void toolbarsFollowFocusedPluginSeriesViewerUi() {
    ToolBarContainer bars = new ToolBarContainer();
    JButton importBtn = new JButton("Import DICOM");
    importBtn.setName("import-dicom");
    bars.add(importBtn);
    bars.registerToolBar(new WtoolBar("Viewer", 0));
    ViewerPlugin<?> twoD = pluginWithBar("2d", "LUT");
    ViewerPlugin<?> audio = pluginWithBar("au", "Audio");
    assertSame(twoD.getSeriesViewerUI(), twoD.getSeriesViewerUI());

    bars.replaceViewerBars(twoD.getSeriesViewerUI().getToolBar(), twoD);
    assertTrue(hasBar(bars, "LUT"));
    assertFalse(hasBar(bars, "Audio"));
    assertFalse(hasBar(bars, "Viewer"));
    assertTrue(hasNamed(bars, "import-dicom"));

    bars.replaceViewerBars(audio.getSeriesViewerUI().getToolBar(), audio);
    assertFalse(hasBar(bars, "LUT"));
    assertTrue(hasBar(bars, "Audio"));
    assertTrue(hasNamed(bars, "import-dicom"));
  }

  static ViewerPlugin<?> pluginWithBar(String name, String bar) {
    ViewerPlugin<?> plugin = new ViewerPlugin<MediaElement>(name) {};
    plugin.getSeriesViewerUI().getToolBar().add(new WtoolBar(bar, 10));
    return plugin;
  }

  static boolean hasBar(ToolBarContainer bars, String name) {
    for (Component c : bars.getComponents()) {
      if (c instanceof Insertable ins && name.equals(ins.getComponentName())) {
        return true;
      }
    }
    return false;
  }

  static boolean hasNamed(ToolBarContainer bars, String name) {
    for (Component c : bars.getComponents()) {
      if (name.equals(c.getName())) {
        return true;
      }
    }
    return false;
  }
}
