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

/** Synthetic Part-10 writers for measurement label tests. No PHI. */
final class MeasureLabelFixtures {

  private MeasureLabelFixtures() {}

  static File writeCtIso050(File dest) throws Exception {
    return writeCt(dest, 0.50, 0.50, true);
  }

  static File writeCtNoSpacing(File dest) throws Exception {
    return writeCt(dest, 0, 0, false);
  }

  static File writeCtRoiAir(File dest) throws Exception {
    File base = writeCt(dest, 0.50, 0.50, true);
    Attributes dcm;
    try (org.dcm4che3.io.DicomInputStream in = new org.dcm4che3.io.DicomInputStream(base)) {
      dcm = in.readDataset();
    }
    dcm.setDouble(Tag.RescaleIntercept, VR.DS, -1024);
    dcm.setString(Tag.RescaleType, VR.LO, "HU");
    int rows = dcm.getInt(Tag.Rows, 0);
    int cols = dcm.getInt(Tag.Columns, 0);
    int[] px = dcm.getInts(Tag.PixelData);
    java.util.Arrays.fill(px, 1064);
    int cx = cols / 2;
    int cy = rows / 2;
    int r = 2;
    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        double dx = col - cx;
        double dy = row - cy;
        if (dx * dx + dy * dy <= r * r) {
          px[row * cols + col] = 24;
        }
      }
    }
    dcm.setInt(Tag.PixelData, VR.OW, px);
    writePart10(dest, dcm);
    return dest;
  }

  static File writeDxImager020(File dest) throws Exception {
    return writeDx(dest, 0.20, 0.20, Double.NaN);
  }

  static File writeDxErmf12(File dest) throws Exception {
    return writeDx(dest, 0.15, 0.15, 1.2);
  }

  private static File writeCt(File dest, double rowSpacing, double colSpacing, boolean withSpacing)
      throws Exception {
    String sop = UIDUtils.createUID("2.25");
    Attributes fmi = fmi(UID.CTImageStorage, sop);
    Attributes dcm = new Attributes();
    dcm.setString(Tag.SOPClassUID, VR.UI, UID.CTImageStorage);
    dcm.setString(Tag.SOPInstanceUID, VR.UI, sop);
    dcm.setString(Tag.StudyInstanceUID, VR.UI, UIDUtils.createUID("2.25"));
    dcm.setString(Tag.SeriesInstanceUID, VR.UI, UIDUtils.createUID("2.25"));
    dcm.setInt(Tag.InstanceNumber, VR.IS, 1);
    dcm.setString(Tag.Modality, VR.CS, "CT");
    dcm.setString(Tag.PhotometricInterpretation, VR.CS, "MONOCHROME2");
    dcm.setInt(Tag.SamplesPerPixel, VR.US, 1);
    int size = 16;
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
    if (withSpacing) {
      dcm.setDouble(Tag.PixelSpacing, VR.DS, rowSpacing, colSpacing);
    }
    dcm.setInt(Tag.PixelData, VR.OW, new int[size * size]);
    writePart10(dest, fmi, dcm);
    return dest;
  }

  private static File writeDx(File dest, double rowImager, double colImager, double ermf)
      throws Exception {
    String sop = UIDUtils.createUID("2.25");
    Attributes fmi = fmi(UID.DigitalXRayImageStorageForPresentation, sop);
    Attributes dcm = new Attributes();
    dcm.setString(Tag.SOPClassUID, VR.UI, UID.DigitalXRayImageStorageForPresentation);
    dcm.setString(Tag.SOPInstanceUID, VR.UI, sop);
    dcm.setString(Tag.StudyInstanceUID, VR.UI, UIDUtils.createUID("2.25"));
    dcm.setString(Tag.SeriesInstanceUID, VR.UI, UIDUtils.createUID("2.25"));
    dcm.setInt(Tag.InstanceNumber, VR.IS, 1);
    dcm.setString(Tag.Modality, VR.CS, "DX");
    dcm.setString(Tag.PhotometricInterpretation, VR.CS, "MONOCHROME2");
    dcm.setInt(Tag.SamplesPerPixel, VR.US, 1);
    int size = 8;
    dcm.setInt(Tag.Rows, VR.US, size);
    dcm.setInt(Tag.Columns, VR.US, size);
    dcm.setInt(Tag.BitsAllocated, VR.US, 16);
    dcm.setInt(Tag.BitsStored, VR.US, 12);
    dcm.setInt(Tag.HighBit, VR.US, 11);
    dcm.setInt(Tag.PixelRepresentation, VR.US, 0);
    dcm.setDouble(Tag.WindowCenter, VR.DS, 2048);
    dcm.setDouble(Tag.WindowWidth, VR.DS, 4096);
    dcm.setString(Tag.RescaleType, VR.LO, "US");
    dcm.setDouble(Tag.ImagerPixelSpacing, VR.DS, rowImager, colImager);
    if (Double.isFinite(ermf)) {
      dcm.setDouble(Tag.EstimatedRadiographicMagnificationFactor, VR.DS, ermf);
    }
    dcm.setInt(Tag.PixelData, VR.OW, new int[size * size]);
    writePart10(dest, fmi, dcm);
    return dest;
  }

  private static Attributes fmi(String sopClass, String sop) {
    Attributes fmi = new Attributes();
    fmi.setBytes(Tag.FileMetaInformationVersion, VR.OB, new byte[] {0, 1});
    fmi.setString(Tag.MediaStorageSOPClassUID, VR.UI, sopClass);
    fmi.setString(Tag.MediaStorageSOPInstanceUID, VR.UI, sop);
    fmi.setString(Tag.TransferSyntaxUID, VR.UI, UID.ExplicitVRLittleEndian);
    fmi.setString(Tag.ImplementationClassUID, VR.UI, "2.25.1918");
    return fmi;
  }

  private static void writePart10(File dest, Attributes fmi, Attributes dcm) throws Exception {
    try (DicomOutputStream out =
        new DicomOutputStream(new FileOutputStream(dest), UID.ExplicitVRLittleEndian)) {
      out.writeDataset(fmi, dcm);
    }
  }

  private static void writePart10(File dest, Attributes dcm) throws Exception {
    Attributes fmi = fmi(dcm.getString(Tag.SOPClassUID), dcm.getString(Tag.SOPInstanceUID));
    writePart10(dest, fmi, dcm);
  }
}
