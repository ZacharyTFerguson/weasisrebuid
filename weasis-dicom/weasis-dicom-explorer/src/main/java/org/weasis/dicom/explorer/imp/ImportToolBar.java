/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.imp;

import java.awt.Frame;
import javax.swing.JButton;
import javax.swing.JFrame;
import org.weasis.core.api.service.UICore;
import org.weasis.core.ui.util.WtoolBar;

/** Explorer import toolbar. Opens File &gt; Import DICOM. */
public class ImportToolBar extends WtoolBar {

  private final JButton button = new JButton("Import DICOM");

  public ImportToolBar() {
    super("Import DICOM", 5);
    button.setName("import-dicom");
    button.addActionListener(e -> openImport());
    add(button);
  }

  public JButton button() {
    return button;
  }

  void openImport() {
    DicomImport.show(ownerFrame(), false);
  }

  static Frame ownerFrame() {
    JFrame win = UICore.getInstance().getApplicationWindow();
    return win;
  }
}
