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
import org.weasis.core.ui.util.WtoolBar;
import org.weasis.dicom.viewer2d.mpr.MprContainer;
import org.weasis.dicom.viewer2d.mpr.MprFactory;

/** 2D chrome that opens MPR (MIP lives on the MPR planes). */
public class Basic3DToolBar extends WtoolBar {

  public static final String NAME = "Basic 3D";

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
}
