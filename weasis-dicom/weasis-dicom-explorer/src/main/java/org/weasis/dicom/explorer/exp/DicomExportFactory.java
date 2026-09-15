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

import java.util.Hashtable;
import org.osgi.service.component.annotations.Component;
import org.weasis.dicom.explorer.DicomModel;

/** Weasis path: File &gt; Export DICOM pages (local files, ZIP, DICOMDIR). */
@Component(service = DicomExportFactory.class, immediate = true)
public class DicomExportFactory {

  public static final String PAGE_LOCAL = "DICOM";
  public static final String PAGE_ZIP = "ZIP";
  public static final String PAGE_DIR = "DICOMDIR";
  public static final String PREF_EXPORT = "weasis.export.dicom";

  public ExportDicom createDicomExportPage(Hashtable<String, Object> properties) {
    String title = PAGE_LOCAL;
    if (properties != null && properties.get("title") instanceof String s) {
      title = s;
    }
    DicomModel model = new DicomModel();
    if (properties != null && properties.get("model") instanceof DicomModel m) {
      model = m;
    }
    return new LocalExport(title, model);
  }
}
