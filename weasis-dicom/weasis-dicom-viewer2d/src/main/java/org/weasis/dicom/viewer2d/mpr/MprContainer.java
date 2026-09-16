/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.mpr;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JPanel;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.ui.editor.image.ImageViewerPlugin;

/** Three orthogonal MPR planes. */
public class MprContainer extends ImageViewerPlugin<MediaElement> {

  public static final String NAME = "MPR";
  private final MprController controller = new MprController();
  private final JPanel planeGrid = new JPanel(new GridLayout(1, 3));

  public MprContainer() {
    super(NAME);
    planeGrid.add(controller.getAxial());
    planeGrid.add(controller.getCoronal());
    planeGrid.add(controller.getSagittal());
    add(planeGrid, BorderLayout.CENTER);
  }

  public MprController getController() {
    return controller;
  }

  public JPanel getPlaneGrid() {
    return planeGrid;
  }
}
