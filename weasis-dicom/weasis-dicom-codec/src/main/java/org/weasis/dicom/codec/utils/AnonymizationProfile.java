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

import java.util.List;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;

/** Basic DICOM anonymize (patient identifiers). Not a full PS3.15 profile. */
public final class AnonymizationProfile {

  public static final List<Integer> PATIENT_TAGS =
      List.of(
          Tag.PatientName,
          Tag.PatientID,
          Tag.PatientBirthDate,
          Tag.PatientBirthTime,
          Tag.PatientSex,
          Tag.OtherPatientNames);

  private AnonymizationProfile() {}

  public static Attributes apply(Attributes src) {
    if (src == null) {
      return null;
    }
    Attributes copy = new Attributes(src);
    for (int tag : PATIENT_TAGS) {
      if (copy.contains(tag)) {
        VR vr = copy.getVR(tag);
        copy.setString(tag, vr == null ? VR.LO : vr, "ANONYMIZED");
      }
    }
    return copy;
  }

  public static boolean retainsPixelData() {
    return true;
  }
}
