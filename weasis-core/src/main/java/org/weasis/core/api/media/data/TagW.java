/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.media.data;

import java.util.Objects;

/** Typed DICOM/media tag key. */
public class TagW {

  public static final TagW PatientID = new TagW("PatientID", "Patient ID");
  public static final TagW PatientName = new TagW("PatientName", "Patient Name");
  public static final TagW StudyInstanceUID = new TagW("StudyInstanceUID", "Study Instance UID");
  public static final TagW SeriesInstanceUID = new TagW("SeriesInstanceUID", "Series Instance UID");
  public static final TagW SOPInstanceUID = new TagW("SOPInstanceUID", "SOP Instance UID");
  public static final TagW Modality = new TagW("Modality", "Modality");
  public static final TagW MIME = new TagW("MIME", "MIME type");
  public static final TagW SOPClassUID = new TagW("SOPClassUID", "SOP Class UID");
  public static final TagW SeriesNumber = new TagW("SeriesNumber", "Series Number");
  public static final TagW InstanceNumber = new TagW("InstanceNumber", "Instance Number");
  public static final TagW SeriesDescription = new TagW("SeriesDescription", "Series Description");
  public static final TagW StudyDate = new TagW("StudyDate", "Study Date");
  public static final TagW StudyDescription = new TagW("StudyDescription", "Study Description");

  private final String keyword;
  private final String displayedName;

  public TagW(String keyword, String displayedName) {
    this.keyword = Objects.requireNonNull(keyword);
    this.displayedName = displayedName == null ? keyword : displayedName;
  }

  public String getKeyword() {
    return keyword;
  }

  public String getDisplayedName() {
    return displayedName;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof TagW other && keyword.equals(other.keyword);
  }

  @Override
  public int hashCode() {
    return keyword.hashCode();
  }

  @Override
  public String toString() {
    return keyword;
  }
}
