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
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;
import org.dcm4che3.io.DicomOutputStream;

/** Writes a GSPS dataset as Part-10. */
public final class DicomPrSerializer {

  private DicomPrSerializer() {}

  public static File write(Attributes pr, File dest) throws IOException {
    if (pr == null || dest == null) {
      throw new IOException("pr dest");
    }
    File parent = dest.getParentFile();
    if (parent != null) {
      parent.mkdirs();
    }
    Attributes fmi = new Attributes();
    fmi.setString(
        Tag.MediaStorageSOPClassUID,
        VR.UI,
        pr.getString(Tag.SOPClassUID, UID.GrayscaleSoftcopyPresentationStateStorage));
    fmi.setString(Tag.MediaStorageSOPInstanceUID, VR.UI, pr.getString(Tag.SOPInstanceUID, ""));
    fmi.setString(Tag.TransferSyntaxUID, VR.UI, UID.ExplicitVRLittleEndian);
    try (DicomOutputStream out = new DicomOutputStream(dest)) {
      out.writeDataset(fmi, pr);
    }
    return dest;
  }
}
