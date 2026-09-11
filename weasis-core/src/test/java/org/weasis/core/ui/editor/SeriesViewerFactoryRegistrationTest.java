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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Hashtable;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.media.MimeInspector;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.api.media.data.Series;
import org.weasis.core.api.service.UICore;
import org.weasis.core.ui.editor.image.ViewerPlugin;

class SeriesViewerFactoryRegistrationTest {

  @Test
  void mimeSelectsFactoryAndOpensBlankViewerPlugin() {
    UICore core = new UICore();
    SeriesViewerFactory factory = new BlankFactory();
    core.registerSeriesViewerFactory(factory);
    assertTrue(core.getViewerFactory(MimeInspector.DUMMY_MIME).isPresent());
    ViewerPlugin<?> plugin = core.openBlankViewer(factory, new Hashtable<>());
    assertInstanceOf(ViewerPlugin.class, plugin);
    assertTrue(factory.isViewerCreatedByThisFactory(plugin));
    assertEquals("Blank", plugin.getPluginName());
    Series<MediaElement> series = new Series<>();
    series.setMimeType(MimeInspector.DUMMY_MIME);
    ViewerPluginBuilder.openSequenceInPlugin(core, factory, series, new Hashtable<>(), true, true);
    assertTrue(core.getOpenViewerPlugins().size() >= 2);
  }

  static final class BlankFactory implements SeriesViewerFactory {
    @Override
    public SeriesViewer<?> createSeriesViewer(Hashtable<String, Object> properties) {
      return new ViewerPlugin<MediaElement>("Blank") {};
    }

    @Override
    public boolean canReadMimeType(String mimeType) {
      return MimeInspector.DUMMY_MIME.equals(mimeType);
    }

    @Override
    public boolean isViewerCreatedByThisFactory(SeriesViewer<?> viewer) {
      return viewer instanceof ViewerPlugin<?> p && "Blank".equals(p.getPluginName());
    }

    @Override
    public int getLevel() {
      return 1;
    }

    @Override
    public boolean canAddSeries() {
      return true;
    }

    @Override
    public boolean canExternalizeSeries() {
      return false;
    }

    @Override
    public String getUIName() {
      return "Blank";
    }

    @Override
    public String getDescription() {
      return "test";
    }

    @Override
    public String getIconPath() {
      return null;
    }

    @Override
    public String getSeriesViewerName() {
      return "Blank";
    }

    @Override
    public String getClassName() {
      return "Blank";
    }
  }
}
