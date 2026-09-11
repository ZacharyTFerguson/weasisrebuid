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

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Hashtable;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.media.MimeInspector;
import org.weasis.core.ui.editor.SeriesViewer;
import org.weasis.core.ui.editor.image.ViewerPlugin;

class DummySeriesViewerFactoryTest {

  @Test
  void opensBlankViewerPlugin() {
    DummySeriesViewerFactory factory = new DummySeriesViewerFactory();
    SeriesViewer<?> viewer = factory.createSeriesViewer(new Hashtable<>());
    assertInstanceOf(ViewerPlugin.class, viewer);
    assertInstanceOf(DummyViewerPlugin.class, viewer);
    assertTrue(factory.isViewerCreatedByThisFactory(viewer));
    assertTrue(factory.canReadMimeType(MimeInspector.DUMMY_MIME));
    DummyViewerPlugin plugin = (DummyViewerPlugin) viewer;
    assertTrue(plugin.getComponentCount() >= 1);
  }
}
