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

import javax.swing.JButton;
import org.weasis.core.ui.util.WtoolBar;
import org.weasis.dicom.explorer.ImportDicomDialog;

/** Explorer import toolbar. Opens File &gt; Import DICOM. */
public class ImportToolBar extends WtoolBar {

  public ImportToolBar() {
    super("Import DICOM", 5);
    JButton button = new JButton("Import DICOM");
    button.addActionListener(e -> ImportDicomDialog.openFromFactories(null, false));
    add(button);
  }
}
