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

import java.awt.Frame;
import org.weasis.core.ui.util.DefaultAction;
import org.weasis.dicom.explorer.DicomModel;

/** File &gt; Export DICOM action. */
public class DicomExportAction extends DefaultAction {

  public DicomExportAction() {
    this(null, null);
  }

  public DicomExportAction(Frame owner, DicomModel model) {
    super("Export DICOM", () -> DicomExport.open(owner, model));
  }
}
