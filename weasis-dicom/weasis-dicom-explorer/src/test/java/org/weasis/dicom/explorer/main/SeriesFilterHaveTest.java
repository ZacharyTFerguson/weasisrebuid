/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.service.UICore;
import org.weasis.core.api.service.WProperties;
import org.weasis.dicom.explorer.ImportedInstance;

class SeriesFilterHaveTest {

  @AfterEach
  void restorePref() {
    UICore.getInstance().getSystemPreferences().remove(SeriesFilter.PREF_MODE);
  }

  @Test
  void defaultPrefModeIsTextAndBlankQueryKeepsAll() {
    WProperties prefs = UICore.getInstance().getSystemPreferences();
    prefs.remove(SeriesFilter.PREF_MODE);
    SeriesFilter filter = new SeriesFilter();
    assertEquals(SeriesFilter.TEXT, filter.getMode());
    assertEquals(SeriesFilter.TEXT, SeriesFilter.prefMode());
    ImportedInstance ct = inst("CT", "chest", "20260101");
    assertTrue(filter.accept(ct));
    assertFalse(filter.accept(null));
  }

  @Test
  void textModeMatchesDescriptionNotModality() {
    SeriesFilter filter = new SeriesFilter();
    filter.setMode(SeriesFilter.TEXT);
    filter.setQuery("chest");
    assertTrue(filter.accept(inst("CT", "chest", "20260101")));
    assertFalse(filter.accept(inst("CT", "knee", "20260101")));
    filter.setQuery("ct");
    assertFalse(filter.accept(inst("CT", "knee", "20260101")));
  }

  @Test
  void modalityModeMatchesModalityOnly() {
    SeriesFilter filter = new SeriesFilter();
    filter.setMode(SeriesFilter.MODALITY);
    filter.setQuery("ct");
    assertTrue(filter.accept(inst("CT", "knee", "20260101")));
    assertFalse(filter.accept(inst("MR", "chest", "20260101")));
  }

  @Test
  void dateModeMatchesStudyDate() {
    SeriesFilter filter = new SeriesFilter();
    filter.setMode(SeriesFilter.DATE);
    filter.setQuery("20260101");
    assertTrue(filter.accept(inst("CT", "chest", "20260101")));
    assertFalse(filter.accept(inst("CT", "chest", "20251231")));
  }

  @Test
  void prefModeReadsDocumentedKey() {
    WProperties prefs = UICore.getInstance().getSystemPreferences();
    prefs.setProperty(SeriesFilter.PREF_MODE, "DATE");
    assertEquals(SeriesFilter.DATE, SeriesFilter.prefMode());
    prefs.setProperty(SeriesFilter.PREF_MODE, "modality");
    assertEquals(SeriesFilter.MODALITY, SeriesFilter.prefMode());
    prefs.setProperty(SeriesFilter.PREF_MODE, "nope");
    assertEquals(SeriesFilter.TEXT, SeriesFilter.prefMode());
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
