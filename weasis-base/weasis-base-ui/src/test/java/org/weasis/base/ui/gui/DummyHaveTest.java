/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.base.ui.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.GraphicsEnvironment;
import java.util.Hashtable;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.media.MimeInspector;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.api.media.data.Series;
import org.weasis.core.api.service.UICore;
import org.weasis.core.ui.editor.SeriesViewer;
import org.weasis.core.ui.editor.image.ViewerPlugin;

class DummyHaveTest {

  @Test
  void activatingFactoryDoesNotAttachDummyPlugin() {
    UICore core = UICore.getInstance();
    WeasisWinChromeHaveTest.closeOpen(core);
    DummySeriesViewerFactory factory = new DummySeriesViewerFactory();
    factory.activate();
    try {
      assertTrue(core.getViewerFactory(MimeInspector.DUMMY_MIME).isPresent());
      assertEquals(0, dummyCount(core));
    } finally {
      factory.deactivate();
      WeasisWinChromeHaveTest.closeOpen(core);
    }
  }

  @Test
  void factoryReadsOnlyDummyMimeNotDicom() {
    DummySeriesViewerFactory factory = new DummySeriesViewerFactory();
    assertTrue(factory.canReadMimeType(MimeInspector.DUMMY_MIME));
    assertFalse(factory.canReadMimeType(MimeInspector.DICOM_MIME));
    assertFalse(factory.canReadMimeType("image/dicom"));
    assertEquals(1000, factory.getLevel());
    assertTrue(factory.canAddSeries());
    assertFalse(factory.canExternalizeSeries());
    SeriesViewer<?> viewer = factory.createSeriesViewer(new Hashtable<>());
    assertInstanceOf(DummyViewerPlugin.class, viewer);
    assertTrue(factory.isViewerCreatedByThisFactory(viewer));
  }

  @Test
  void dummyHoldsSeriesAndReturnsToEmptyStatusOnClose() {
    DummyViewerPlugin plugin = new DummyViewerPlugin();
    assertEquals(DummyViewerPlugin.EMPTY_STATUS, plugin.statusText());
    Series<MediaElement> series = new Series<>("2.25.dummy");
    series.setMimeType(MimeInspector.DUMMY_MIME);
    plugin.addSeries(series);
    assertEquals(1, plugin.getOpenSeries().size());
    assertEquals("1 series", plugin.statusText());
    plugin.removeSeries(series);
    assertEquals(DummyViewerPlugin.EMPTY_STATUS, plugin.statusText());
    plugin.addSeries(series);
    plugin.close();
    assertTrue(plugin.getOpenSeries().isEmpty());
    assertEquals(DummyViewerPlugin.EMPTY_STATUS, plugin.statusText());
  }

  @Test
  void weasisWinStartsWithEmptyViewerTabsAndNoDummy() {
    Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());
    UICore core = UICore.getInstance();
    WeasisWinChromeHaveTest.closeOpen(core);
    WeasisWin win = new WeasisWin();
    try {
      assertEquals(0, win.getViewerTabs().getTabCount());
      assertEquals(0, dummyCount(core));
    } finally {
      WeasisWinChromeHaveTest.closeOpen(core);
      win.dispose();
    }
  }

  static int dummyCount(UICore core) {
    int n = 0;
    for (ViewerPlugin<?> plugin : core.getOpenViewerPlugins()) {
      if (plugin instanceof DummyViewerPlugin) {
        n++;
      }
    }
    return n;
  }
}
