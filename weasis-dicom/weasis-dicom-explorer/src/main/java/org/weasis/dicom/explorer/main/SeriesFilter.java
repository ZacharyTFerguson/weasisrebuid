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

import java.util.Locale;
import org.weasis.core.api.service.UICore;
import org.weasis.dicom.explorer.ImportedInstance;

/**
 * Explorer series thumbnail filter. Pref {@code weasis.dicom.explorer.filter.mode} is {@code TEXT}
 * / {@code DATE} / {@code MODALITY} (PREFERENCES.md default TEXT).
 */
public class SeriesFilter {

  public static final String PREF_MODE = "weasis.dicom.explorer.filter.mode";
  public static final String TEXT = "TEXT";
  public static final String DATE = "DATE";
  public static final String MODALITY = "MODALITY";

  private String query = "";
  private String mode;

  public SeriesFilter() {
    this.mode = prefMode();
  }

  public static String prefMode() {
    return normalizeMode(UICore.getInstance().getSystemPreferences().getProperty(PREF_MODE, TEXT));
  }

  public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query == null ? "" : query;
  }

  public String getMode() {
    return mode;
  }

  public void setMode(String mode) {
    this.mode = normalizeMode(mode);
  }

  public boolean accept(ImportedInstance inst) {
    if (inst == null) {
      return false;
    }
    if (query.isBlank()) {
      return true;
    }
    return match(inst);
  }

  boolean match(ImportedInstance inst) {
    String q = query.toLowerCase(Locale.ROOT);
    if (DATE.equals(mode)) {
      return contains(inst.studyDate(), q);
    }
    if (MODALITY.equals(mode)) {
      return contains(inst.modality(), q);
    }
    return matchText(inst, q);
  }

  boolean matchText(ImportedInstance inst, String q) {
    return contains(inst.seriesDescription(), q)
        || contains(inst.patientName(), q)
        || contains(inst.patientId(), q);
  }

  static boolean contains(String value, String q) {
    return value.toLowerCase(Locale.ROOT).contains(q);
  }

  static String normalizeMode(String raw) {
    if (raw == null || raw.isBlank()) {
      return TEXT;
    }
    String u = raw.trim().toUpperCase(Locale.ROOT);
    if (DATE.equals(u) || MODALITY.equals(u)) {
      return u;
    }
    return TEXT;
  }
}
