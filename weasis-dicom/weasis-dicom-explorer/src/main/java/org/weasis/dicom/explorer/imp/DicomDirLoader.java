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

import java.io.File;
import java.io.IOException;
import org.weasis.dicom.explorer.DicomModel;
import org.weasis.dicom.explorer.LoadLocalDicom;
import org.weasis.dicom.explorer.SkipUnsupportedSopNotifier;

/** Walks DICOMDIR records into the explorer model. */
public class DicomDirLoader {

  public LoadLocalDicom.ImportResult load(
      File dicomdir, DicomModel model, SkipUnsupportedSopNotifier skip) throws IOException {
    return DicomDirImport.read(dicomdir, model, skip);
  }
}
