/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.qr;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;

/** DIMSE C-FIND identifier dataset and charset defaults (ISO_IR 192). */
public final class SearchParameters {

  public static final String FIND_CHARSET = "ISO_IR 192";

  public enum QueryRetrieveLevel {
    PATIENT,
    STUDY,
    SERIES,
    IMAGE
  }

  private QueryRetrieveLevel level = QueryRetrieveLevel.STUDY;
  private String patientId;
  private String patientName;
  private String studyInstanceUid;
  private String modality;

  public String charset() {
    return FIND_CHARSET;
  }

  public QueryRetrieveLevel level() {
    return level;
  }

  public void setLevel(QueryRetrieveLevel level) {
    this.level = level == null ? QueryRetrieveLevel.STUDY : level;
  }

  public String patientId() {
    return patientId;
  }

  public void setPatientId(String patientId) {
    this.patientId = patientId;
  }

  public String patientName() {
    return patientName;
  }

  public void setPatientName(String patientName) {
    this.patientName = patientName;
  }

  public String studyInstanceUid() {
    return studyInstanceUid;
  }

  public void setStudyInstanceUid(String studyInstanceUid) {
    this.studyInstanceUid = studyInstanceUid;
  }

  public String modality() {
    return modality;
  }

  public void setModality(String modality) {
    this.modality = modality;
  }

  /** Study-root C-FIND identifier with return keys and matching keys. */
  public Attributes buildCFindIdentifier() {
    Attributes keys = new Attributes();
    keys.setString(Tag.SpecificCharacterSet, VR.CS, FIND_CHARSET);
    keys.setString(Tag.QueryRetrieveLevel, VR.CS, level.name());
    if (patientId != null) {
      keys.setString(Tag.PatientID, VR.LO, patientId);
    }
    if (patientName != null) {
      keys.setString(Tag.PatientName, VR.PN, patientName);
    }
    if (studyInstanceUid != null) {
      keys.setString(Tag.StudyInstanceUID, VR.UI, studyInstanceUid);
    }
    if (modality != null) {
      keys.setString(Tag.ModalitiesInStudy, VR.CS, modality);
    }
    keys.setNull(Tag.StudyDate, VR.DA);
    keys.setNull(Tag.StudyTime, VR.TM);
    keys.setNull(Tag.AccessionNumber, VR.SH);
    keys.setNull(Tag.StudyDescription, VR.LO);
    keys.setNull(Tag.NumberOfStudyRelatedSeries, VR.IS);
    keys.setNull(Tag.NumberOfStudyRelatedInstances, VR.IS);
    return keys;
  }

  public String findSopClassUid() {
    return UID.StudyRootQueryRetrieveInformationModelFind;
  }
}
