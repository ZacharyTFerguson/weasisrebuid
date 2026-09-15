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

import org.weasis.dicom.explorer.DicomModel;
import org.weasis.dicom.explorer.ImportDicomPage;
import org.weasis.dicom.explorer.SkipUnsupportedSopNotifier;

/** File &gt; Import DICOM (local files / folder). */
public class LocalImport extends ImportDicomPage {

  public LocalImport(DicomModel model, SkipUnsupportedSopNotifier skip) {
    super(DicomImportFactory.PAGE_LOCAL, 0, model, skip);
  }
}
