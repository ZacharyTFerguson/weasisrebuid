/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.pr;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import org.weasis.dicom.codec.DicomMime;
import org.weasis.dicom.explorer.DicomModel;
import org.weasis.dicom.explorer.ImportedInstance;

/** Copies GSPS / PR instances from the explorer model to a destination folder. */
public final class DicomExportPR {

  private DicomExportPR() {}

  public static List<File> export(DicomModel model, File destDir) throws IOException {
    if (destDir == null) {
      throw new IOException("pr dest");
    }
    Files.createDirectories(destDir.toPath());
    List<File> written = new ArrayList<>();
    if (model == null) {
      return written;
    }
    for (ImportedInstance inst : model.getInstances()) {
      if (!isPresentationState(inst) || inst.file() == null || !inst.file().isFile()) {
        continue;
      }
      File dest = new File(destDir, inst.sopUid() + ".dcm");
      Files.copy(inst.file().toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
      written.add(dest);
    }
    return written;
  }

  static boolean isPresentationState(ImportedInstance inst) {
    if (inst == null) {
      return false;
    }
    if ("PR".equalsIgnoreCase(inst.modality())) {
      return true;
    }
    return DicomMime.PR_DICOM.equals(inst.mime())
        || DicomMime.PR_DICOM.equals(DicomMime.fromSopClass(inst.sopClassUid()));
  }
}
