/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.sr;

import java.util.Hashtable;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.weasis.core.api.service.UICore;
import org.weasis.core.ui.editor.SeriesViewer;
import org.weasis.core.ui.editor.SeriesViewerFactory;
import org.weasis.dicom.codec.DicomMime;
import org.weasis.dicom.codec.DicomSpecialElementFactory;

@Component(
    service = {SeriesViewerFactory.class, DicomSpecialElementFactory.class},
    immediate = true)
public class SrFactory implements SeriesViewerFactory, DicomSpecialElementFactory {

  public static final String NAME = "DICOM SR";

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
    return new SrViewer();
  }

  @Override
  public boolean canReadMimeType(String mimeType) {
    return DicomMime.SR_DICOM.equals(mimeType);
  }

  @Override
  public boolean isViewerCreatedByThisFactory(SeriesViewer<?> viewer) {
    return viewer instanceof SrViewer;
  }

  @Override
  public int getLevel() {
    return 20;
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
    return NAME;
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
    return SrViewer.class.getName();
  }

  @Override
  public String getSeriesMimeType() {
    return DicomMime.SR_DICOM;
  }

  @Override
  public String getModality() {
    return "SR";
  }
}
