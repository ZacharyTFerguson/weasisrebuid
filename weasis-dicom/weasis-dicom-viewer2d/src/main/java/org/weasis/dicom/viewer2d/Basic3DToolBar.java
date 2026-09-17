/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d;

import java.awt.event.ActionEvent;
import java.util.Hashtable;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import org.weasis.core.api.service.UICore;
import org.weasis.core.ui.editor.SeriesViewer;
import org.weasis.core.ui.editor.SeriesViewerFactory;
import org.weasis.core.ui.editor.image.ViewerPlugin;
import org.weasis.core.ui.util.WtoolBar;
import org.weasis.dicom.viewer2d.mpr.MprContainer;
import org.weasis.dicom.viewer2d.mpr.MprFactory;

/** 2D chrome that opens MPR (MIP lives on the MPR planes) and the DICOM 3D viewer. */
public class Basic3DToolBar extends WtoolBar {

  public static final String NAME = "Basic 3D";
  public static final String VOLUME_VIEWER = "DICOM 3D Viewer";

  public Basic3DToolBar() {
    super(NAME, 25);
    JButton mpr =
        new JButton(
            new AbstractAction("MPR") {
              @Override
              public void actionPerformed(ActionEvent e) {
                openMpr();
              }
            });
    mpr.setName("mpr");
    add(mpr);
    JButton volume =
        new JButton(
            new AbstractAction("3D") {
              @Override
              public void actionPerformed(ActionEvent e) {
                open3d();
              }
            });
    volume.setName("3d");
    add(volume);
  }

  public MprContainer openMpr() {
    return openMpr(UICore.getInstance());
  }

  public MprContainer openMpr(UICore core) {
    MprContainer container = (MprContainer) new MprFactory().createSeriesViewer(new Hashtable<>());
    if (core != null) {
      core.openViewerPlugin(container);
    }
    return container;
  }

  public ViewerPlugin<?> open3d() {
    return open3d(UICore.getInstance());
  }

  public ViewerPlugin<?> open3d(UICore core) {
    SeriesViewerFactory factory = volumeFactory(core);
    if (factory == null) {
      return null;
    }
    SeriesViewer<?> created = factory.createSeriesViewer(new Hashtable<>());
    if (created instanceof ViewerPlugin<?> plugin) {
      core.openViewerPlugin(plugin);
      return plugin;
    }
    return null;
  }

  static SeriesViewerFactory volumeFactory(UICore core) {
    if (core == null) {
      return null;
    }
    for (SeriesViewerFactory factory : core.getSeriesViewerFactories()) {
      if (VOLUME_VIEWER.equals(factory.getUIName())) {
        return factory;
      }
    }
    return null;
  }
}
