/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.explorer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;
import org.dcm4che3.io.DicomOutputStream;
import org.dcm4che3.util.UIDUtils;

/** PDF → Encapsulated PDF; STL → Encapsulated STL. */
public final class EncapToDicom {

  private EncapToDicom() {}

  public static Path convert(Path src, Path dest, PatientDemographics demo) throws IOException {
    Objects.requireNonNull(src, "src");
    Objects.requireNonNull(dest, "dest");
    String name = src.getFileName().toString().toLowerCase(Locale.ROOT);
    boolean pdf = name.endsWith(".pdf");
    boolean stl = name.endsWith(".stl");
    if (!pdf && !stl) {
      throw new IOException("not PDF or STL: " + src);
    }
    byte[] bytes = Files.readAllBytes(src);
    PatientDemographics d = demo == null ? PatientDemographics.empty() : demo;
    String sopClass = pdf ? UID.EncapsulatedPDFStorage : UID.EncapsulatedSTLStorage;
    String mime = pdf ? "application/pdf" : "model/stl";
    String sop = UIDUtils.createUID("2.25");
    Attributes fmi = new Attributes();
    fmi.setBytes(Tag.FileMetaInformationVersion, VR.OB, new byte[] {0, 1});
    fmi.setString(Tag.MediaStorageSOPClassUID, VR.UI, sopClass);
    fmi.setString(Tag.MediaStorageSOPInstanceUID, VR.UI, sop);
    fmi.setString(Tag.TransferSyntaxUID, VR.UI, UID.ExplicitVRLittleEndian);
    fmi.setString(Tag.ImplementationClassUID, VR.UI, "2.25.1918");

    Attributes dcm = new Attributes();
    dcm.setString(Tag.SOPClassUID, VR.UI, sopClass);
    dcm.setString(Tag.SOPInstanceUID, VR.UI, sop);
    dcm.setString(Tag.StudyInstanceUID, VR.UI, UIDUtils.createUID("2.25"));
    dcm.setString(Tag.SeriesInstanceUID, VR.UI, UIDUtils.createUID("2.25"));
    dcm.setString(Tag.Modality, VR.CS, "DOC");
    dcm.setString(Tag.MIMETypeOfEncapsulatedDocument, VR.LO, mime);
    dcm.setBytes(Tag.EncapsulatedDocument, VR.OB, bytes);
    dcm.setString(
        Tag.PatientName,
        VR.PN,
        d.patientName() == null || d.patientName().isBlank() ? "SYNTHETIC^ENCAP" : d.patientName());
    dcm.setString(
        Tag.PatientID,
        VR.LO,
        d.patientId() == null || d.patientId().isBlank() ? "SYN-ENCAP-1" : d.patientId());
    try (DicomOutputStream out = new DicomOutputStream(dest.toFile())) {
      out.writeDataset(fmi, dcm);
    }
    return dest;
  }
}
