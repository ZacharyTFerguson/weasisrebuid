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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Series by number then description; instances by instance number then SOP UID. */
public final class DicomSorter {

  public static final Comparator<ImportedInstance> SERIES =
      Comparator.comparingInt(ImportedInstance::seriesNumber)
          .thenComparing(ImportedInstance::seriesDescription, String.CASE_INSENSITIVE_ORDER)
          .thenComparing(ImportedInstance::seriesUid);

  public static final Comparator<ImportedInstance> INSTANCE =
      Comparator.comparingInt(ImportedInstance::instanceNumber)
          .thenComparing(ImportedInstance::sopUid);

  public static final Comparator<ImportedInstance> STUDY_DATE_DESC =
      Comparator.comparing(ImportedInstance::studyDate, Comparator.reverseOrder())
          .thenComparing(ImportedInstance::seriesDescription, String.CASE_INSENSITIVE_ORDER);

  private DicomSorter() {}

  public static List<ImportedInstance> sortSeries(List<ImportedInstance> in) {
    List<ImportedInstance> copy = new ArrayList<>(in);
    copy.sort(SERIES);
    return copy;
  }

  public static List<ImportedInstance> sortInstances(List<ImportedInstance> in) {
    List<ImportedInstance> copy = new ArrayList<>(in);
    copy.sort(INSTANCE);
    return copy;
  }
}
