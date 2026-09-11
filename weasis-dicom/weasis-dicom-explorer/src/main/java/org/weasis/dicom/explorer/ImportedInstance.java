/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer;

import java.io.File;
import java.util.Objects;

/** One imported SOP instance. No PHI required in tests (synthetic UIDs). */
public final class ImportedInstance {

  private final String patientName;
  private final String patientId;
  private final String studyUid;
  private final String seriesUid;
  private final String sopUid;
  private final String sopClassUid;
  private final String modality;
  private final String seriesDescription;
  private final String studyDate;
  private final int seriesNumber;
  private final int instanceNumber;
  private final File file;
  private final String mime;

  public ImportedInstance(
      String patientName,
      String patientId,
      String studyUid,
      String seriesUid,
      String sopUid,
      String sopClassUid,
      String modality,
      String seriesDescription,
      String studyDate,
      int seriesNumber,
      int instanceNumber,
      File file,
      String mime) {
    this.patientName = patientName == null ? "" : patientName;
    this.patientId = patientId == null ? "" : patientId;
    this.studyUid = studyUid == null ? "" : studyUid;
    this.seriesUid = seriesUid == null ? "" : seriesUid;
    this.sopUid = sopUid == null ? "" : sopUid;
    this.sopClassUid = sopClassUid == null ? "" : sopClassUid;
    this.modality = modality == null ? "" : modality;
    this.seriesDescription = seriesDescription == null ? "" : seriesDescription;
    this.studyDate = studyDate == null ? "" : studyDate;
    this.seriesNumber = seriesNumber;
    this.instanceNumber = instanceNumber;
    this.file = file;
    this.mime = mime == null ? "application/dicom" : mime;
  }

  public String patientName() {
    return patientName;
  }

  public String patientId() {
    return patientId;
  }

  public String studyUid() {
    return studyUid;
  }

  public String seriesUid() {
    return seriesUid;
  }

  public String sopUid() {
    return sopUid;
  }

  public String sopClassUid() {
    return sopClassUid;
  }

  public String modality() {
    return modality;
  }

  public String seriesDescription() {
    return seriesDescription;
  }

  public String studyDate() {
    return studyDate;
  }

  public int seriesNumber() {
    return seriesNumber;
  }

  public int instanceNumber() {
    return instanceNumber;
  }

  public File file() {
    return file;
  }

  public String mime() {
    return mime;
  }

  public String patientKey() {
    return patientName + "\t" + patientId;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof ImportedInstance o && Objects.equals(sopUid, o.sopUid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sopUid);
  }
}
