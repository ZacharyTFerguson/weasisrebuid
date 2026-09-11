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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class DicomSorterTest {

  @Test
  void seriesByNumberThenDescription() {
    ImportedInstance a = inst(2, "B", 10);
    ImportedInstance b = inst(1, "A", 1);
    ImportedInstance c = inst(1, "C", 2);
    List<ImportedInstance> sorted = DicomSorter.sortSeries(List.of(a, b, c));
    assertEquals(b.sopUid(), sorted.get(0).sopUid());
    assertEquals(c.sopUid(), sorted.get(1).sopUid());
    assertEquals(a.sopUid(), sorted.get(2).sopUid());
  }

  @Test
  void instancesByInstanceNumber() {
    ImportedInstance late = inst(1, "A", 20);
    ImportedInstance early = inst(1, "A", 3);
    List<ImportedInstance> sorted = DicomSorter.sortInstances(List.of(late, early));
    assertEquals(3, sorted.get(0).instanceNumber());
    assertEquals(20, sorted.get(1).instanceNumber());
  }

  static ImportedInstance inst(int series, String desc, int instance) {
    return new ImportedInstance(
        "SYNTHETIC^A",
        "SYN-1",
        "2.25.1",
        "2.25.2." + series + desc,
        "2.25.3." + instance,
        "1.2.840.10008.10.0.2.2.1.2",
        "CT",
        desc,
        "20260101",
        series,
        instance,
        null,
        "image/dicom");
  }
}
