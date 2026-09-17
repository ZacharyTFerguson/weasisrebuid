/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer;

import java.util.Hashtable;
import java.util.Locale;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.weasis.core.api.media.data.MediaSeries;
import org.weasis.core.api.service.UICore;
import org.weasis.core.ui.editor.MimeSystemAppViewer;
import org.weasis.core.ui.editor.SeriesViewer;
import org.weasis.core.ui.editor.SeriesViewerFactory;
import org.weasis.dicom.codec.DicomMime;

/**
 * PDF and video series open with the OS handler. Must not claim {@code image/dicom} (2D viewer owns
 * that MIME).
 */
@Component(service = SeriesViewerFactory.class, immediate = true)
public class MimeSystemAppFactory implements SeriesViewerFactory {

  public static final String NAME = "System application";

  @Activate
  public void activate() {
    UICore.getInstance().registerSeriesViewerFactory(this);
  }

  @Deactivate
  public void deactivate() {
    UICore.getInstance().unregisterSeriesViewerFactory(this);
  }

  @Override
  public SeriesViewer<?> createSeriesViewer(Hashtable<String, Object> properties) {
    return new MimeSystemAppViewer();
  }

  @Override
  public boolean canReadMimeType(String mimeType) {
    if (mimeType == null || mimeType.isBlank()) {
      return false;
    }
    String mime = mimeType.toLowerCase(Locale.ROOT).trim();
    if (DicomMime.IMAGE_DICOM.equals(mime)
        || DicomMime.SERIES_DICOM.equals(mime)
        || DicomMime.APPLICATION_DICOM.equals(mime)) {
      return false;
    }
    if (DicomMime.ENCAP_DICOM.equals(mime) || DicomMime.VIDEO_DICOM.equals(mime)) {
      return true;
    }
    return "application/pdf".equals(mime)
        || mime.startsWith("application/pdf;")
        || mime.startsWith("video/");
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
    return NAME;
  }

  @Override
  public String getDescription() {
    return "Open encapsulated PDF and video with the system application";
  }

  @Override
  public String getIconPath() {
    return "";
  }

  @Override
  public String getSeriesViewerName() {
    return NAME;
  }

  @Override
  public String getClassName() {
    return MimeSystemAppViewer.class.getName();
  }

  @Override
  public boolean canReadSeries(MediaSeries<?> series) {
    return series != null && canReadMimeType(series.getMimeType());
  }
}
