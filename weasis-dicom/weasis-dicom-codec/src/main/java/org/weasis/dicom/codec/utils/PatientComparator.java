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

import java.util.Objects;
import org.weasis.core.api.media.data.MediaSeriesGroup;
import org.weasis.core.api.media.data.TagW;

/** Patient join: name AND ID (SRS ingest). */
public final class PatientComparator {
  private PatientComparator() {}

  public static boolean samePatient(MediaSeriesGroup a, MediaSeriesGroup b) {
    if (a == null || b == null) {
      return false;
    }
    return Objects.equals(str(a, TagW.PatientID), str(b, TagW.PatientID))
        && Objects.equals(str(a, TagW.PatientName), str(b, TagW.PatientName));
  }

  public static String key(String patientId, String patientName) {
    return String.valueOf(patientId) + '|' + String.valueOf(patientName);
  }

  private static String str(MediaSeriesGroup g, TagW tag) {
    Object v = g.getTagValue(tag);
    return v == null ? "" : v.toString();
  }
}
