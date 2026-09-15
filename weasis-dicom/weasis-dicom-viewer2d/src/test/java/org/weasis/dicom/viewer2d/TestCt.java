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
import java.io.IOException;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;
import org.dcm4che3.io.DicomOutputStream;
import org.dcm4che3.util.UIDUtils;

/** Synthetic EVR LE MONOCHROME2 CT. No PHI. */
final class TestCt {

  private TestCt() {}

  static void write(File dest, int size, double level, double window) throws IOException {
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
    dcm.setString(Tag.SeriesInstanceUID, VR.UI, UIDUtils.createUID("2.25"));
    dcm.setString(Tag.Modality, VR.CS, "CT");
    dcm.setString(Tag.PhotometricInterpretation, VR.CS, "MONOCHROME2");
    dcm.setInt(Tag.SamplesPerPixel, VR.US, 1);
    dcm.setInt(Tag.Rows, VR.US, size);
    dcm.setInt(Tag.Columns, VR.US, size);
    dcm.setInt(Tag.BitsAllocated, VR.US, 16);
    dcm.setInt(Tag.BitsStored, VR.US, 16);
    dcm.setInt(Tag.HighBit, VR.US, 15);
    dcm.setInt(Tag.PixelRepresentation, VR.US, 1);
    dcm.setDouble(Tag.WindowCenter, VR.DS, level);
    dcm.setDouble(Tag.WindowWidth, VR.DS, window);
    dcm.setDouble(Tag.RescaleSlope, VR.DS, 1.0);
    dcm.setDouble(Tag.RescaleIntercept, VR.DS, 0.0);
    dcm.setString(Tag.PatientName, VR.PN, "SYNTHETIC^CT");
    dcm.setString(Tag.PatientID, VR.LO, "SYN-CT-0001");
    int[] px = new int[size * size];
    for (int y = 0; y < size; y++) {
      for (int x = 0; x < size; x++) {
        px[y * size + x] = (int) Math.round((x / (double) (size - 1)) * 200 - 80);
      }
    }
    dcm.setInt(Tag.PixelData, VR.OW, px);
    try (DicomOutputStream out = new DicomOutputStream(dest)) {
      out.writeDataset(fmi, dcm);
    }
  }

  static void writeDualVoi(File dest) throws IOException {
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
    dcm.setString(Tag.SeriesInstanceUID, VR.UI, UIDUtils.createUID("2.25"));
    dcm.setString(Tag.Modality, VR.CS, "DX");
    dcm.setString(Tag.PhotometricInterpretation, VR.CS, "MONOCHROME2");
    dcm.setInt(Tag.SamplesPerPixel, VR.US, 1);
    dcm.setInt(Tag.Rows, VR.US, 2);
    dcm.setInt(Tag.Columns, VR.US, 2);
    dcm.setInt(Tag.BitsAllocated, VR.US, 16);
    dcm.setInt(Tag.BitsStored, VR.US, 16);
    dcm.setInt(Tag.HighBit, VR.US, 15);
    dcm.setInt(Tag.PixelRepresentation, VR.US, 0);
    dcm.setDouble(Tag.WindowCenter, VR.DS, 40, 200);
    dcm.setDouble(Tag.WindowWidth, VR.DS, 80, 400);
    dcm.setString(Tag.PatientName, VR.PN, "SYNTHETIC^DX");
    dcm.setInt(Tag.PixelData, VR.OW, 0, 50, 100, 200);
    try (DicomOutputStream out = new DicomOutputStream(dest)) {
      out.writeDataset(fmi, dcm);
    }
  }
}
