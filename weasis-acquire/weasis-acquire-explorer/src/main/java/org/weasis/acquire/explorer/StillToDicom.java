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

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import javax.imageio.ImageIO;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Fragments;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;
import org.dcm4che3.io.DicomOutputStream;
import org.dcm4che3.util.ByteUtils;
import org.dcm4che3.util.UIDUtils;

/** Stills → JPEG-lossy Secondary Capture (TS JPEG Baseline). */
public final class StillToDicom {

  private StillToDicom() {}

  public static Path convert(Path still, Path dest, PatientDemographics demo) throws IOException {
    Objects.requireNonNull(still, "still");
    Objects.requireNonNull(dest, "dest");
    if (!StillFormats.isStill(still)) {
      throw new IOException("not a still: " + still);
    }
    BufferedImage img = ImageIO.read(still.toFile());
    if (img == null) {
      throw new IOException("unreadable still " + still);
    }
    byte[] jpeg = jpegBytes(img);
    PatientDemographics d = demo == null ? PatientDemographics.empty() : demo;
    String sop = UIDUtils.createUID("2.25");
    Attributes fmi = new Attributes();
    fmi.setBytes(Tag.FileMetaInformationVersion, VR.OB, new byte[] {0, 1});
    fmi.setString(Tag.MediaStorageSOPClassUID, VR.UI, UID.SecondaryCaptureImageStorage);
    fmi.setString(Tag.MediaStorageSOPInstanceUID, VR.UI, sop);
    fmi.setString(Tag.TransferSyntaxUID, VR.UI, UID.JPEGBaseline8Bit);
    fmi.setString(Tag.ImplementationClassUID, VR.UI, "2.25.1918");
    fmi.setString(Tag.ImplementationVersionName, VR.SH, "WEASISREBUILD");

    Attributes dcm = new Attributes();
    dcm.setString(Tag.SOPClassUID, VR.UI, UID.SecondaryCaptureImageStorage);
    dcm.setString(Tag.SOPInstanceUID, VR.UI, sop);
    dcm.setString(Tag.StudyInstanceUID, VR.UI, UIDUtils.createUID("2.25"));
    dcm.setString(Tag.SeriesInstanceUID, VR.UI, UIDUtils.createUID("2.25"));
    dcm.setString(Tag.Modality, VR.CS, "OT");
    dcm.setString(Tag.PhotometricInterpretation, VR.CS, "YBR_FULL_422");
    dcm.setInt(Tag.SamplesPerPixel, VR.US, 3);
    dcm.setInt(Tag.PlanarConfiguration, VR.US, 0);
    dcm.setInt(Tag.Rows, VR.US, img.getHeight());
    dcm.setInt(Tag.Columns, VR.US, img.getWidth());
    dcm.setInt(Tag.BitsAllocated, VR.US, 8);
    dcm.setInt(Tag.BitsStored, VR.US, 8);
    dcm.setInt(Tag.HighBit, VR.US, 7);
    dcm.setInt(Tag.PixelRepresentation, VR.US, 0);
    dcm.setString(Tag.LossyImageCompression, VR.CS, "01");
    dcm.setString(Tag.LossyImageCompressionMethod, VR.CS, "ISO_10918_1");
    dcm.setString(
        Tag.PatientName,
        VR.PN,
        d.patientName() == null || d.patientName().isBlank() ? "SYNTHETIC^STILL" : d.patientName());
    dcm.setString(
        Tag.PatientID,
        VR.LO,
        d.patientId() == null || d.patientId().isBlank() ? "SYN-STILL-1" : d.patientId());
    Instant now = Instant.now();
    dcm.setString(
        Tag.ContentDate,
        VR.DA,
        DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneOffset.UTC).format(now));
    dcm.setString(
        Tag.ContentTime,
        VR.TM,
        DateTimeFormatter.ofPattern("HHmmss").withZone(ZoneOffset.UTC).format(now));
    Fragments frags = new Fragments(VR.OB, false, 2);
    frags.add(ByteUtils.EMPTY_BYTES);
    frags.add(jpeg);
    dcm.setValue(Tag.PixelData, VR.OB, frags);
    try (DicomOutputStream out = new DicomOutputStream(dest.toFile())) {
      out.writeDataset(fmi, dcm);
    }
    return dest;
  }

  static byte[] jpegBytes(BufferedImage img) throws IOException {
    BufferedImage rgb =
        new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
    java.awt.Graphics g = rgb.getGraphics();
    try {
      g.drawImage(img, 0, 0, null);
    } finally {
      g.dispose();
    }
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    if (!ImageIO.write(rgb, "jpeg", baos)) {
      throw new IOException("JPEG writer missing");
    }
    return baos.toByteArray();
  }
}
