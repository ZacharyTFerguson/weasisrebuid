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
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.weasis.core.api.gui.FactoryEnablement;
import org.weasis.core.api.media.data.MediaSeries;
import org.weasis.core.api.service.UICore;
import org.weasis.core.ui.editor.SeriesViewer;
import org.weasis.core.ui.editor.SeriesViewerFactory;

/** OS hand-off for encapsulated PDF/video. Dicomizer disables this factory by FQCN. */
@Component(service = SeriesViewerFactory.class, immediate = true)
public class MimeSystemAppFactory implements SeriesViewerFactory {

  public static final String NAME = "System application";

  @Activate
  public void activate() {
    if (!FactoryEnablement.isEnabled(MimeSystemAppFactory.class)) {
      return;
    }
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
    if (mimeType == null) {
      return false;
    }
    return mimeType.startsWith("application/pdf")
        || mimeType.startsWith("video/")
        || "encap/dicom".equals(mimeType)
        || "video/dicom".equals(mimeType);
  }

  @Override
  public boolean isViewerCreatedByThisFactory(SeriesViewer<?> viewer) {
    return viewer instanceof MimeSystemAppViewer;
  }

  @Override
  public int getLevel() {
    return 50;
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
    return "Hand off PDF/video to the OS handler";
  }

  @Override
  public String getIconPath() {
    return null;
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
