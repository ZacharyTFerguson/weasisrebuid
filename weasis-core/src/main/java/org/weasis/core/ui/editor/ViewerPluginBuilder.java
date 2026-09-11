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

import java.util.Hashtable;
import org.weasis.core.api.media.data.MediaSeries;
import org.weasis.core.api.service.UICore;
import org.weasis.core.ui.editor.image.ViewerPlugin;

/** Opens a series in a factory-created {@link ViewerPlugin}. */
public final class ViewerPluginBuilder {

  public static final String CMP = "cmp";

  private ViewerPluginBuilder() {}

  public static ViewerPlugin<?> openSequenceInPlugin(
      SeriesViewerFactory factory,
      MediaSeries<?> series,
      Hashtable<String, Object> properties,
      boolean forceNewView,
      boolean select) {
    return openSequenceInPlugin(
        UICore.getInstance(), factory, series, properties, forceNewView, select);
  }

  public static ViewerPlugin<?> openSequenceInPlugin(
      UICore uiCore,
      SeriesViewerFactory factory,
      MediaSeries<?> series,
      Hashtable<String, Object> properties,
      boolean forceNewView,
      boolean select) {
    if (factory == null) {
      throw new IllegalArgumentException("factory");
    }
    UICore core = uiCore == null ? UICore.getInstance() : uiCore;
    Hashtable<String, Object> props = properties == null ? new Hashtable<>() : properties;
    ViewerPlugin<?> plugin = core.openBlankViewer(factory, props);
    if (series != null) {
      @SuppressWarnings("unchecked")
      ViewerPlugin<org.weasis.core.api.media.data.MediaElement> typed =
          (ViewerPlugin<org.weasis.core.api.media.data.MediaElement>) plugin;
      typed.addSeries((MediaSeries<org.weasis.core.api.media.data.MediaElement>) series);
    }
    plugin.setSelected(select || forceNewView);
    return plugin;
  }
}
