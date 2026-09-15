/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.base.viewer2d;

import java.util.Hashtable;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.weasis.core.api.service.UICore;
import org.weasis.core.ui.editor.SeriesViewer;
import org.weasis.core.ui.editor.SeriesViewerFactory;

@Component(service = SeriesViewerFactory.class, immediate = true)
public class ViewerFactory implements SeriesViewerFactory {

  public static final String NAME = "Image Viewer";

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
    return new View2dContainer();
  }

  @Override
  public boolean canReadMimeType(String mimeType) {
    return mimeType != null && mimeType.startsWith("image/") && !mimeType.contains("dicom");
  }

  @Override
  public boolean isViewerCreatedByThisFactory(SeriesViewer<?> viewer) {
    return viewer instanceof View2dContainer;
  }

  @Override
  public int getLevel() {
    return 100;
  }

  @Override
  public boolean canAddSeries() {
    return true;
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
    return View2dContainer.class.getName();
  }
}
