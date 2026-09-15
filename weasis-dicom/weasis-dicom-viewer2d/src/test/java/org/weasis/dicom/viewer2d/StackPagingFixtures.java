/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d;

import java.io.File;
import java.io.FileOutputStream;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;
import org.dcm4che3.io.DicomOutputStream;
import org.dcm4che3.util.UIDUtils;

/** Part-10 writers for stack paging tests only. No PHI. */
final class StackPagingFixtures {

  private StackPagingFixtures() {}

  static File writeCtInstance(
      File dest,
      String seriesUid,
      int instanceNumber,
      double rowSpacing,
      double colSpacing,
      double ippZ,
      int pixelFill)
      throws Exception {
    String sop = UIDUtils.createUID("2.25");
    Attributes fmi = new Attributes();
    fmi.setBytes(Tag.FileMetaInformationVersion, VR.OB, new byte[] {0, 1});
    fmi.setString(Tag.MediaStorageSOPClassUID, VR.UI, UID.CTImageStorage);
    fmi.setString(Tag.MediaStorageSOPInstanceUID, VR.UI, sop);
    fmi.setString(Tag.TransferSyntaxUID, VR.UI, UID.ExplicitVRLittleEndian);
    fmi.setString(Tag.ImplementationClassUID, VR.UI, "2.25.1918");
    Attributes dcm = new Attributes();
    dcm.setString(Tag.SOPClassUID, VR.UI, UID.CTImageStorage);
    dcm.setString(Tag.SOPInstanceUID, VR.UI, sop);
    dcm.setString(Tag.StudyInstanceUID, VR.UI, UIDUtils.createUID("2.25"));
    dcm.setString(Tag.SeriesInstanceUID, VR.UI, seriesUid);
    dcm.setInt(Tag.InstanceNumber, VR.IS, instanceNumber);
    dcm.setString(Tag.Modality, VR.CS, "CT");
    dcm.setString(Tag.PhotometricInterpretation, VR.CS, "MONOCHROME2");
    dcm.setInt(Tag.SamplesPerPixel, VR.US, 1);
    int size = 8;
    dcm.setInt(Tag.Rows, VR.US, size);
    dcm.setInt(Tag.Columns, VR.US, size);
    dcm.setInt(Tag.BitsAllocated, VR.US, 16);
    dcm.setInt(Tag.BitsStored, VR.US, 16);
    dcm.setInt(Tag.HighBit, VR.US, 15);
    dcm.setInt(Tag.PixelRepresentation, VR.US, 1);
    dcm.setDouble(Tag.WindowCenter, VR.DS, 40);
    dcm.setDouble(Tag.WindowWidth, VR.DS, 400);
    dcm.setDouble(Tag.RescaleSlope, VR.DS, 1.0);
    dcm.setDouble(Tag.RescaleIntercept, VR.DS, 0.0);
    dcm.setDouble(Tag.PixelSpacing, VR.DS, rowSpacing, colSpacing);
    dcm.setDouble(Tag.ImagePositionPatient, VR.DS, 0, 0, ippZ);
    int[] px = new int[size * size];
    for (int i = 0; i < px.length; i++) {
      px[i] = pixelFill + i;
    }
    dcm.setInt(Tag.PixelData, VR.OW, px);
    try (DicomOutputStream out =
        new DicomOutputStream(new FileOutputStream(dest), UID.ExplicitVRLittleEndian)) {
      out.writeDataset(fmi, dcm);
    }
    return dest;
  }

  static File writeOtherSeriesCt(File dest) throws Exception {
    return writeCtInstance(
        dest, UIDUtils.createUID("2.25"), 1, 0.80, 0.80, 10.0, 500);
  }

  static File writeMultiframeCt(File dest) throws Exception {
    String seriesUid = UIDUtils.createUID("2.25");
    File single =
        writeCtInstance(dest, seriesUid, 1, 0.80, 0.80, 0.0, 100);
    Attributes dcm;
    try (org.dcm4che3.io.DicomInputStream in =
        new org.dcm4che3.io.DicomInputStream(single)) {
      dcm = in.readDataset();
    }
    int size = 8;
    int[] frame0 = new int[size * size];
    int[] frame1 = new int[size * size];
    int[] frame2 = new int[size * size];
    java.util.Arrays.fill(frame0, 10);
    java.util.Arrays.fill(frame1, 20);
    java.util.Arrays.fill(frame2, 30);
    int[] all = new int[frame0.length * 3];
    System.arraycopy(frame0, 0, all, 0, frame0.length);
    System.arraycopy(frame1, 0, all, frame0.length, frame1.length);
    System.arraycopy(frame2, 0, all, frame0.length + frame1.length, frame2.length);
    dcm.setInt(Tag.NumberOfFrames, VR.IS, 3);
    dcm.setInt(Tag.PixelData, VR.OW, all);
    Attributes fmi = new Attributes();
    fmi.setBytes(Tag.FileMetaInformationVersion, VR.OB, new byte[] {0, 1});
    fmi.setString(Tag.MediaStorageSOPClassUID, VR.UI, UID.CTImageStorage);
    fmi.setString(
        Tag.MediaStorageSOPInstanceUID, VR.UI, dcm.getString(Tag.SOPInstanceUID));
    fmi.setString(Tag.TransferSyntaxUID, VR.UI, UID.ExplicitVRLittleEndian);
    try (DicomOutputStream out =
        new DicomOutputStream(new FileOutputStream(dest), UID.ExplicitVRLittleEndian)) {
      out.writeDataset(fmi, dcm);
    }
    return dest;
  }
}
