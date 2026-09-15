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

import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.api.media.data.MediaSeries;

public class SeriesViewerEvent {
  public enum EVENT {
    SELECT,
    ADD,
    REMOVE,
    LAYOUT,
    PRESET
  }

  private final SeriesViewer<?> viewer;
  private final MediaSeries<?> series;
  private final MediaElement media;
  private final EVENT eventType;

  public SeriesViewerEvent(
      SeriesViewer<?> viewer, MediaSeries<?> series, MediaElement media, EVENT eventType) {
    this.viewer = viewer;
    this.series = series;
    this.media = media;
    this.eventType = eventType;
  }

  public SeriesViewer<?> getViewer() {
    return viewer;
  }

  public MediaSeries<?> getSeries() {
    return series;
  }

  public MediaElement getMediaElement() {
    return media;
  }

  public EVENT getEventType() {
    return eventType;
  }
}
