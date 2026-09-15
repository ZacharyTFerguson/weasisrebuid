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
import org.weasis.core.api.service.UICore;
import org.weasis.dicom.explorer.DicomModel;

/** File &gt; Export DICOM entry. Honors {@code weasis.export.dicom}. */
public final class DicomExport {

  private DicomExport() {}

  public static boolean isExportEnabled() {
    String sys = System.getProperty(DicomExportFactory.PREF_EXPORT);
    if (sys != null && !sys.isBlank()) {
      return !"false".equalsIgnoreCase(sys.trim());
    }
    return UICore.getInstance()
        .getSystemPreferences()
        .getBooleanProperty(DicomExportFactory.PREF_EXPORT, true);
  }

  public static ExportDicomView open(Frame owner, DicomModel model) {
    if (!isExportEnabled()) {
      return null;
    }
    return new ExportDicomView(owner, model);
  }
}
