/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.explorer.core.bean;

import org.weasis.acquire.explorer.PatientDemographics;
import org.weasis.core.api.media.data.TagW;

/** Patient-level tags shared by every series in the dicomizer album. */
public class Global extends DefaultTaggable {

  public void init(PatientDemographics demographics) {
    PatientDemographics demo = demographics == null ? PatientDemographics.empty() : demographics;
    putPatient(TagW.PatientName, demo.patientName());
    putPatient(TagW.PatientID, demo.patientId());
    putPatient(TagW.PatientBirthDate, demo.birthDate());
    putPatient(TagW.PatientSex, demo.sex());
    putPatient(TagW.AccessionNumber, demo.accessionNumber());
    putPatient(TagW.OperatorsName, demo.operatorsName());
    putPatient(TagW.StudyID, demo.studyId());
    putPatient(TagW.LocalNamespaceEntityID, demo.issuerOfAccessionNumber());
  }

  public boolean hasPatientTags() {
    return nonBlank(TagW.PatientName) || nonBlank(TagW.PatientID);
  }

  private void putPatient(TagW tag, String value) {
    if (value == null || value.isBlank()) {
      setTag(tag, null);
    } else {
      setTag(tag, value);
    }
  }

  private boolean nonBlank(TagW tag) {
    Object value = getTagValue(tag);
    return value != null && !value.toString().isBlank();
  }
}
