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

public class DefaultMimeAppFactory implements SeriesViewerFactory {
  @Override
  public SeriesViewer<?> createSeriesViewer(Hashtable<String, Object> properties) {
    return new MimeSystemAppViewer();
  }

  @Override
  public boolean canReadMimeType(String mimeType) {
    return mimeType != null && (mimeType.startsWith("application/pdf") || mimeType.startsWith("video/"));
  }

  @Override
  public boolean isViewerCreatedByThisFactory(SeriesViewer<?> viewer) {
    return viewer instanceof MimeSystemAppViewer;
  }

  @Override
  public int getLevel() {
    return 100;
  }

  @Override
  public boolean canAddSeries() {
    return false;
  }

  @Override
  public boolean canExternalizeSeries() {
    return true;
  }

  @Override
  public String getUIName() {
    return "System application";
  }

  @Override
  public String getDescription() {
    return "Open with the system application";
  }

  @Override
  public String getIconPath() {
    return "";
  }

  @Override
  public String getSeriesViewerName() {
    return getUIName();
  }

  @Override
  public String getClassName() {
    return getClass().getName();
  }

  @Override
  public boolean canReadSeries(MediaSeries<?> series) {
    return series != null && canReadMimeType(series.getMimeType());
  }
}
