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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.service.WProperties;

class SeriesFilterTest {

  @Test
  void modalityModeKeepsOnlyListed() {
    SeriesFilter filter = new SeriesFilter(SeriesFilter.MODE_MODALITY, Set.of("CT"), "");
    ImportedInstance ct = DicomSorterTest.inst(1, "ct", 1);
    ImportedInstance mr =
        new ImportedInstance(
            "SYNTHETIC^A",
            "SYN-1",
            "2.25.1",
            "2.25.9",
            "2.25.91",
            "1.2.840.10008.10.0.2.2.1.4",
            "MR",
            "brain",
            "20260101",
            2,
            1,
            null,
            "image/dicom");
    List<ImportedInstance> out = filter.apply(List.of(ct, mr));
    assertEquals(1, out.size());
    assertEquals("CT", out.get(0).modality());
  }

  @Test
  void defaultPrefModeIsAll() {
    SeriesFilter filter = SeriesFilter.fromPrefs(new WProperties());
    assertEquals(SeriesFilter.MODE_ALL, filter.mode());
    assertTrue(filter.accept(DicomSorterTest.inst(1, "x", 1)));
    assertFalse(filter.accept(null));
  }
}
