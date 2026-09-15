/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer3d;

import java.util.Hashtable;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.weasis.core.api.service.UICore;
import org.weasis.core.ui.editor.SeriesViewer;
import org.weasis.core.ui.editor.SeriesViewerFactory;
import org.weasis.dicom.codec.DicomMime;

@Component(service = SeriesViewerFactory.class, immediate = true)
public class View3DFactory implements SeriesViewerFactory {

  public static final String NAME = "DICOM 3D Viewer";
  public static final int START_LEVEL = 120;

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
    OpenGLInfo.Caps caps = capsFrom(properties);
    return new View3DContainer(caps);
  }

  static OpenGLInfo.Caps capsFrom(Hashtable<String, Object> properties) {
    if (properties == null) {
      return OpenGLInfo.describe(null, null);
    }
    Object renderer = properties.get("opengl.renderer");
    Object version = properties.get("opengl.version");
    return OpenGLInfo.describe(
        renderer == null ? null : renderer.toString(), version == null ? null : version.toString());
  }

  @Override
  public boolean canReadMimeType(String mimeType) {
    return DicomMime.VOL_DICOM.equals(mimeType) || DicomMime.IMAGE_DICOM.equals(mimeType);
  }

  @Override
  public boolean isViewerCreatedByThisFactory(SeriesViewer<?> viewer) {
    return viewer instanceof View3DContainer;
  }

  @Override
  public int getLevel() {
    return START_LEVEL;
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
    return "Volume rendering (OpenGL 3.3+)";
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
    return View3DContainer.class.getName();
  }
}
