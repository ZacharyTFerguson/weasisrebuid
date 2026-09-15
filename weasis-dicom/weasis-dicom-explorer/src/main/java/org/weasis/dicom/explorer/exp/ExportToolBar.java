/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.exp;

import javax.swing.JButton;
import org.weasis.core.ui.util.WtoolBar;

/** Explorer export toolbar. Opens File &gt; Export DICOM when {@code weasis.export.dicom}. */
public class ExportToolBar extends WtoolBar {

  public ExportToolBar() {
    super("Export DICOM", 6);
    JButton button = new JButton(new DicomExportAction());
    button.setEnabled(DicomExport.isExportEnabled());
    add(button);
  }
}
