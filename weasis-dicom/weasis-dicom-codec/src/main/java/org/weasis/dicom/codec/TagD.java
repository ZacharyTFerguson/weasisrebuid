/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.codec;

import org.dcm4che3.data.ElementDictionary;
import org.dcm4che3.data.Tag;
import org.weasis.core.api.media.data.TagW;

/** DICOM data-element key. Keyword comes from the standard dictionary. */
public class TagD extends TagW {

  public static final TagD PatientID = get(Tag.PatientID);
  public static final TagD PatientName = get(Tag.PatientName);
  public static final TagD StudyInstanceUID = get(Tag.StudyInstanceUID);
  public static final TagD SeriesInstanceUID = get(Tag.SeriesInstanceUID);
  public static final TagD SOPInstanceUID = get(Tag.SOPInstanceUID);
  public static final TagD SOPClassUID = get(Tag.SOPClassUID);
  public static final TagD Modality = get(Tag.Modality);
  public static final TagD SeriesDescription = get(Tag.SeriesDescription);
  public static final TagD StudyDescription = get(Tag.StudyDescription);
  public static final TagD StudyDate = get(Tag.StudyDate);
  public static final TagD SeriesNumber = get(Tag.SeriesNumber);
  public static final TagD InstanceNumber = get(Tag.InstanceNumber);
  public static final TagD Rows = get(Tag.Rows);
  public static final TagD Columns = get(Tag.Columns);
  public static final TagD PixelSpacing = get(Tag.PixelSpacing);
  public static final TagD WindowWidth = get(Tag.WindowWidth);
  public static final TagD WindowCenter = get(Tag.WindowCenter);
  public static final TagD PhotometricInterpretation = get(Tag.PhotometricInterpretation);
  public static final TagD TransferSyntaxUID = get(Tag.TransferSyntaxUID);
  public static final TagD FrameOfReferenceUID = get(Tag.FrameOfReferenceUID);
  public static final TagD ImageOrientationPatient = get(Tag.ImageOrientationPatient);
  public static final TagD ImagePositionPatient = get(Tag.ImagePositionPatient);
  public static final TagD SliceThickness = get(Tag.SliceThickness);
  public static final TagD RescaleSlope = get(Tag.RescaleSlope);
  public static final TagD RescaleIntercept = get(Tag.RescaleIntercept);

  private final int tag;

  public TagD(int tag) {
    super(keywordOf(tag), keywordOf(tag));
    this.tag = tag;
  }

  public int getId() {
    return tag;
  }

  public static TagD get(int tag) {
    return new TagD(tag);
  }

  public static String keywordOf(int tag) {
    String keyword = ElementDictionary.getStandardElementDictionary().keywordOf(tag);
    return keyword == null || keyword.isBlank() ? String.format("%08X", tag) : keyword;
  }
}
