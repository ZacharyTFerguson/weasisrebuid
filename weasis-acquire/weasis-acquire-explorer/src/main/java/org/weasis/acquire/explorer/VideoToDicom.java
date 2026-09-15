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
import java.util.Objects;
import java.util.Properties;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Fragments;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;
import org.dcm4che3.io.DicomOutputStream;
import org.dcm4che3.util.ByteUtils;
import org.dcm4che3.util.UIDUtils;

/** Video → Video Photographic Image Storage when the profile/size gate passes. */
public final class VideoToDicom {

  private VideoToDicom() {}

  public static Path convert(
      Path src,
      Path dest,
      String mime,
      String profile,
      String level,
      Properties prefs,
      PatientDemographics demo)
      throws IOException {
    Objects.requireNonNull(src, "src");
    Objects.requireNonNull(dest, "dest");
    if (!VideoProfileGate.accept(mime, profile, level)) {
      throw new IOException("rejected video profile mime=" + mime + " profile=" + profile);
    }
    byte[] bytes = Files.readAllBytes(src);
    int max = VideoSizeLimit.maxMegabytes(prefs);
    if (!VideoSizeLimit.allowed(bytes.length, max)) {
      throw new IOException("video exceeds weasis.acquire.video.max.size=" + max);
    }
    String ts = transferSyntax(mime, profile);
    PatientDemographics d = demo == null ? PatientDemographics.empty() : demo;
    String sop = UIDUtils.createUID("2.25");
    Attributes fmi = new Attributes();
    fmi.setBytes(Tag.FileMetaInformationVersion, VR.OB, new byte[] {0, 1});
    fmi.setString(Tag.MediaStorageSOPClassUID, VR.UI, UID.VideoPhotographicImageStorage);
    fmi.setString(Tag.MediaStorageSOPInstanceUID, VR.UI, sop);
    fmi.setString(Tag.TransferSyntaxUID, VR.UI, ts);
    fmi.setString(Tag.ImplementationClassUID, VR.UI, "2.25.1918");

    Attributes dcm = new Attributes();
    dcm.setString(Tag.SOPClassUID, VR.UI, UID.VideoPhotographicImageStorage);
    dcm.setString(Tag.SOPInstanceUID, VR.UI, sop);
    dcm.setString(Tag.StudyInstanceUID, VR.UI, UIDUtils.createUID("2.25"));
    dcm.setString(Tag.SeriesInstanceUID, VR.UI, UIDUtils.createUID("2.25"));
    dcm.setString(Tag.Modality, VR.CS, "XC");
    dcm.setInt(Tag.Rows, VR.US, 16);
    dcm.setInt(Tag.Columns, VR.US, 16);
    dcm.setInt(Tag.SamplesPerPixel, VR.US, 3);
    dcm.setString(Tag.PhotometricInterpretation, VR.CS, "YBR_PARTIAL_420");
    dcm.setInt(Tag.BitsAllocated, VR.US, 8);
    dcm.setInt(Tag.BitsStored, VR.US, 8);
    dcm.setInt(Tag.HighBit, VR.US, 7);
    dcm.setInt(Tag.PixelRepresentation, VR.US, 0);
    dcm.setString(Tag.LossyImageCompression, VR.CS, "01");
    dcm.setString(
        Tag.PatientName,
        VR.PN,
        d.patientName() == null || d.patientName().isBlank() ? "SYNTHETIC^VIDEO" : d.patientName());
    dcm.setString(
        Tag.PatientID,
        VR.LO,
        d.patientId() == null || d.patientId().isBlank() ? "SYN-VID-1" : d.patientId());
    Fragments frags = new Fragments(VR.OB, false, 2);
    frags.add(ByteUtils.EMPTY_BYTES);
    frags.add(bytes);
    dcm.setValue(Tag.PixelData, VR.OB, frags);
    try (DicomOutputStream out = new DicomOutputStream(dest.toFile())) {
      out.writeDataset(fmi, dcm);
    }
    return dest;
  }

  static String transferSyntax(String mime, String profile) {
    if (mime != null
        && mime.toLowerCase().contains("mpeg")
        && !mime.toLowerCase().contains("mp4")) {
      return UID.MPEG2MPML;
    }
    if (profile != null && profile.toLowerCase().contains("main")) {
      return UID.HEVCMP51;
    }
    return UID.MPEG4HP41;
  }
}
