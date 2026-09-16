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

import java.awt.event.ActionEvent;
import java.util.Hashtable;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import org.weasis.core.api.service.UICore;
import org.weasis.core.ui.editor.SeriesViewer;
import org.weasis.core.ui.util.WtoolBar;

/** 2D-side chrome that opens the current series in the DICOM 3D viewer. */
public class ExternalView3DToolbar extends WtoolBar {

  public static final String NAME = "3D External";
  private final View3DFactory factory = new View3DFactory();
  private View3DContainer lastOpened;

  public ExternalView3DToolbar() {
    super(NAME, 121);
    JButton open =
        new JButton(
            new AbstractAction("3D") {
              @Override
              public void actionPerformed(ActionEvent e) {
                open3d(new Hashtable<>());
              }
            });
    open.setName("3d");
    add(open);
  }

  public View3DContainer open3d(Hashtable<String, Object> properties) {
    return open3d(properties, UICore.getInstance());
  }

  public View3DContainer open3d(Hashtable<String, Object> properties, UICore core) {
    SeriesViewer<?> viewer = factory.createSeriesViewer(properties);
    if (!(viewer instanceof View3DContainer container)) {
      return null;
    }
    lastOpened = container;
    EventManager.getInstance().setAction(ActionVol.RENDERING_TYPE);
    if (core != null) {
      core.openViewerPlugin(container);
    }
    return container;
  }

  public View3DContainer getLastOpened() {
    return lastOpened;
  }
}
