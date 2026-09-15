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
import org.junit.jupiter.api.Test;
import org.weasis.core.api.service.WProperties;

class SeriesFilterTest {

  @Test
  void modalityModeKeepsOnlyMatchingModality() {
    SeriesFilter filter = new SeriesFilter(SeriesFilter.MODALITY, "CT");
    ImportedInstance ct = DicomSorterTest.inst(1, "chest", 1);
    ImportedInstance mr = inst("MR", "brain", "20260101");
    List<ImportedInstance> out = filter.apply(List.of(ct, mr));
    assertEquals(1, out.size());
    assertEquals("CT", out.get(0).modality());
  }

  @Test
  void defaultPrefModeIsTextAndBlankQueryKeepsAll() {
    SeriesFilter filter = SeriesFilter.fromPrefs(new WProperties());
    assertEquals(SeriesFilter.TEXT, filter.mode());
    assertTrue(filter.accept(DicomSorterTest.inst(1, "x", 1)));
    assertFalse(filter.accept(null));
  }

  @Test
  void textModeMatchesDescriptionNotModality() {
    SeriesFilter filter = new SeriesFilter(SeriesFilter.TEXT, "chest");
    assertTrue(filter.accept(inst("CT", "chest", "20260101")));
    assertFalse(filter.accept(inst("CT", "knee", "20260101")));
    SeriesFilter byModalityToken = new SeriesFilter(SeriesFilter.TEXT, "ct");
    assertFalse(byModalityToken.accept(inst("CT", "knee", "20260101")));
  }

  @Test
  void dateModeMatchesStudyDate() {
    SeriesFilter filter = new SeriesFilter(SeriesFilter.DATE, "20260101");
    assertTrue(filter.accept(inst("CT", "chest", "20260101")));
    assertFalse(filter.accept(inst("CT", "chest", "20251231")));
  }

  @Test
  void fromPrefsReadsDocumentedModes() {
    WProperties prefs = new WProperties();
    prefs.setProperty(SeriesFilter.PREF_MODE, "DATE");
    assertEquals(SeriesFilter.DATE, SeriesFilter.fromPrefs(prefs).mode());
    prefs.setProperty(SeriesFilter.PREF_MODE, "modality");
    assertEquals(SeriesFilter.MODALITY, SeriesFilter.fromPrefs(prefs).mode());
    prefs.setProperty(SeriesFilter.PREF_MODE, "all");
    assertEquals(SeriesFilter.TEXT, SeriesFilter.fromPrefs(prefs).mode());
    assertEquals(SeriesFilter.TEXT, SeriesFilter.fromPrefs(null).mode());
  }

  static ImportedInstance inst(String modality, String desc, String date) {
    return new ImportedInstance(
        "SYNTHETIC^A",
        "SYN-1",
        "2.25.1",
        "2.25." + modality + "." + desc,
        "2.25.i." + desc,
        "1.2.840.10008.10.0.2.2.1.2",
        modality,
        desc,
        date,
        1,
        1,
        null,
        "image/dicom");
  }
}
