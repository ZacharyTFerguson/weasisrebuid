/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.codec.utils;

import java.io.File;
import java.io.FileOutputStream;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;
import org.dcm4che3.io.DicomOutputStream;
import org.dcm4che3.util.UIDUtils;

/** Synthetic Part-10 writers for oracle honesty tests. No PHI — UIDs under private 2.25.* roots. */
public final class SyntheticDicomFixtures {

  private SyntheticDicomFixtures() {}

  public static void writeExplicitVrLeMonochrome2Ct(
      File dest, int size, double level, double window) throws Exception {
    SyntheticCtWriter.write(dest, size, level, window);
  }

  public static void writeEncapsulatedPdf(File dest) throws Exception {
    String sop = UIDUtils.createUID("2.25");
    Attributes fmi = fmi(UID.EncapsulatedPDFStorage, sop, UID.ExplicitVRLittleEndian);
    Attributes dcm = new Attributes();
    dcm.setString(Tag.SOPClassUID, VR.UI, UID.EncapsulatedPDFStorage);
    dcm.setString(Tag.SOPInstanceUID, VR.UI, sop);
    dcm.setString(Tag.StudyInstanceUID, VR.UI, UIDUtils.createUID("2.25"));
    dcm.setString(Tag.SeriesInstanceUID, VR.UI, UIDUtils.createUID("2.25"));
    dcm.setString(Tag.Modality, VR.CS, "DOC");
    dcm.setString(Tag.MIMETypeOfEncapsulatedDocument, VR.LO, "application/pdf");
    dcm.setBytes(Tag.EncapsulatedDocument, VR.OB, "%PDF-1.4 SYNTHETIC".getBytes());
    write(dest, fmi, dcm, UID.ExplicitVRLittleEndian);
  }

  public static void writeRgb(File dest) throws Exception {
    String sop = UIDUtils.createUID("2.25");
    Attributes fmi = fmi(UID.SecondaryCaptureImageStorage, sop, UID.ExplicitVRLittleEndian);
    Attributes dcm = new Attributes();
    dcm.setString(Tag.SOPClassUID, VR.UI, UID.SecondaryCaptureImageStorage);
    dcm.setString(Tag.SOPInstanceUID, VR.UI, sop);
    dcm.setString(Tag.StudyInstanceUID, VR.UI, UIDUtils.createUID("2.25"));
    dcm.setString(Tag.SeriesInstanceUID, VR.UI, UIDUtils.createUID("2.25"));
    dcm.setString(Tag.Modality, VR.CS, "OT");
    dcm.setString(Tag.PhotometricInterpretation, VR.CS, "RGB");
    dcm.setInt(Tag.SamplesPerPixel, VR.US, 3);
    dcm.setInt(Tag.PlanarConfiguration, VR.US, 0);
    dcm.setInt(Tag.Rows, VR.US, 4);
    dcm.setInt(Tag.Columns, VR.US, 4);
    dcm.setInt(Tag.BitsAllocated, VR.US, 8);
    dcm.setInt(Tag.BitsStored, VR.US, 8);
    dcm.setInt(Tag.HighBit, VR.US, 7);
    dcm.setInt(Tag.PixelRepresentation, VR.US, 0);
    dcm.setBytes(Tag.PixelData, VR.OB, new byte[4 * 4 * 3]);
    write(dest, fmi, dcm, UID.ExplicitVRLittleEndian);
  }

  public static void writeMonochrome2WithTransferSyntax(File dest, String ts) throws Exception {
    writeMonochrome2Image(dest, ts, "MONOCHROME2");
  }

  public static void writeMonochrome1ExplicitVrLe(File dest) throws Exception {
    writeMonochrome2Image(dest, UID.ExplicitVRLittleEndian, "MONOCHROME1");
  }

  public static void writeMonochrome2ExplicitVrBe(File dest) throws Exception {
    writeMonochrome2Image(dest, UID.ExplicitVRBigEndian, "MONOCHROME2");
  }

  public static void writeMonochrome2Rle(File dest) throws Exception {
    String sop = UIDUtils.createUID("2.25");
    Attributes fmi = fmi(UID.CTImageStorage, sop, UID.RLELossless);
    Attributes dcm = monochrome2Dataset(sop, 4, 4);
    write(dest, fmi, dcm, UID.RLELossless);
  }

  public static void writePresentationState(File dest) throws Exception {
    String sop = UIDUtils.createUID("2.25");
    Attributes fmi =
        fmi(UID.GrayscaleSoftcopyPresentationStateStorage, sop, UID.ExplicitVRLittleEndian);
    Attributes dcm = new Attributes();
    dcm.setString(Tag.SOPClassUID, VR.UI, UID.GrayscaleSoftcopyPresentationStateStorage);
    dcm.setString(Tag.SOPInstanceUID, VR.UI, sop);
    dcm.setString(Tag.StudyInstanceUID, VR.UI, UIDUtils.createUID("2.25"));
    dcm.setString(Tag.SeriesInstanceUID, VR.UI, UIDUtils.createUID("2.25"));
    dcm.setString(Tag.Modality, VR.CS, "PR");
    write(dest, fmi, dcm, UID.ExplicitVRLittleEndian);
  }

  public static void writeSegmentation(File dest) throws Exception {
    String sop = UIDUtils.createUID("2.25");
    Attributes fmi = fmi(UID.SegmentationStorage, sop, UID.ExplicitVRLittleEndian);
    Attributes dcm = new Attributes();
    dcm.setString(Tag.SOPClassUID, VR.UI, UID.SegmentationStorage);
    dcm.setString(Tag.SOPInstanceUID, VR.UI, sop);
    dcm.setString(Tag.StudyInstanceUID, VR.UI, UIDUtils.createUID("2.25"));
    dcm.setString(Tag.SeriesInstanceUID, VR.UI, UIDUtils.createUID("2.25"));
    dcm.setString(Tag.Modality, VR.CS, "SEG");
    write(dest, fmi, dcm, UID.ExplicitVRLittleEndian);
  }

  public static void writeKeyObject(File dest) throws Exception {
    String sop = UIDUtils.createUID("2.25");
    Attributes fmi = fmi(UID.KeyObjectSelectionDocumentStorage, sop, UID.ExplicitVRLittleEndian);
    Attributes dcm = new Attributes();
    dcm.setString(Tag.SOPClassUID, VR.UI, UID.KeyObjectSelectionDocumentStorage);
    dcm.setString(Tag.SOPInstanceUID, VR.UI, sop);
    dcm.setString(Tag.StudyInstanceUID, VR.UI, UIDUtils.createUID("2.25"));
    dcm.setString(Tag.SeriesInstanceUID, VR.UI, UIDUtils.createUID("2.25"));
    dcm.setString(Tag.Modality, VR.CS, "KO");
    write(dest, fmi, dcm, UID.ExplicitVRLittleEndian);
  }

  static void writeMonochrome2Image(File dest, String ts, String photometric) throws Exception {
    String sop = UIDUtils.createUID("2.25");
    Attributes fmi = fmi(UID.CTImageStorage, sop, ts);
    Attributes dcm = monochrome2Dataset(sop, 4, 4);
    dcm.setString(Tag.PhotometricInterpretation, VR.CS, photometric);
    write(dest, fmi, dcm, ts);
  }

  public static void writeCtWithPixelSpacing(File dest, double rowMm, double colMm) throws Exception {
    String sop = UIDUtils.createUID("2.25");
    Attributes fmi = fmi(UID.CTImageStorage, sop, UID.ExplicitVRLittleEndian);
    Attributes dcm = monochrome2Dataset(sop, 8, 8);
    dcm.setDouble(Tag.PixelSpacing, VR.DS, rowMm, colMm);
    write(dest, fmi, dcm, UID.ExplicitVRLittleEndian);
  }

  static Attributes monochrome2Dataset(String sop, int rows, int cols) {
    Attributes dcm = new Attributes();
    dcm.setString(Tag.SOPClassUID, VR.UI, UID.CTImageStorage);
    dcm.setString(Tag.SOPInstanceUID, VR.UI, sop);
    dcm.setString(Tag.StudyInstanceUID, VR.UI, UIDUtils.createUID("2.25"));
    dcm.setString(Tag.SeriesInstanceUID, VR.UI, UIDUtils.createUID("2.25"));
    dcm.setString(Tag.Modality, VR.CS, "CT");
    dcm.setString(Tag.PhotometricInterpretation, VR.CS, "MONOCHROME2");
    dcm.setInt(Tag.SamplesPerPixel, VR.US, 1);
    dcm.setInt(Tag.Rows, VR.US, rows);
    dcm.setInt(Tag.Columns, VR.US, cols);
    dcm.setInt(Tag.BitsAllocated, VR.US, 16);
    dcm.setInt(Tag.BitsStored, VR.US, 16);
    dcm.setInt(Tag.HighBit, VR.US, 15);
    dcm.setInt(Tag.PixelRepresentation, VR.US, 1);
    dcm.setDouble(Tag.WindowCenter, VR.DS, 40);
    dcm.setDouble(Tag.WindowWidth, VR.DS, 400);
    dcm.setInt(Tag.PixelData, VR.OW, new int[rows * cols]);
    return dcm;
  }

  public static Attributes fmi(String sopClass, String sop, String ts) {
    Attributes fmi = new Attributes();
    fmi.setBytes(Tag.FileMetaInformationVersion, VR.OB, new byte[] {0, 1});
    fmi.setString(Tag.MediaStorageSOPClassUID, VR.UI, sopClass);
    fmi.setString(Tag.MediaStorageSOPInstanceUID, VR.UI, sop);
    fmi.setString(Tag.TransferSyntaxUID, VR.UI, ts);
    fmi.setString(Tag.ImplementationClassUID, VR.UI, "2.25.1918");
    fmi.setString(Tag.ImplementationVersionName, VR.SH, "WEASISREBUILD");
    return fmi;
  }

  public static void write(File dest, Attributes fmi, Attributes dcm, String ts) throws Exception {
    try (DicomOutputStream out =
        new DicomOutputStream(new FileOutputStream(dest), UID.ExplicitVRLittleEndian)) {
      out.writeDataset(fmi, dcm);
    }
  }
}
