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
import java.util.List;
import org.weasis.core.api.service.WProperties;

/**
 * Clone-only facade over fixture {@code main/SeriesFilter}. Same documented pref {@code
 * weasis.dicom.explorer.filter.mode}: TEXT / DATE / MODALITY (default TEXT).
 */
public final class SeriesFilter {

  public static final String PREF_MODE = org.weasis.dicom.explorer.main.SeriesFilter.PREF_MODE;
  public static final String TEXT = org.weasis.dicom.explorer.main.SeriesFilter.TEXT;
  public static final String DATE = org.weasis.dicom.explorer.main.SeriesFilter.DATE;
  public static final String MODALITY = org.weasis.dicom.explorer.main.SeriesFilter.MODALITY;

  private final org.weasis.dicom.explorer.main.SeriesFilter inner;

  public SeriesFilter(String mode, String query) {
    inner = new org.weasis.dicom.explorer.main.SeriesFilter();
    inner.setMode(mode);
    inner.setQuery(query);
  }

  public static SeriesFilter fromPrefs(WProperties prefs) {
    if (prefs == null) {
      return new SeriesFilter(TEXT, "");
    }
    return new SeriesFilter(prefs.getProperty(PREF_MODE, TEXT), "");
  }

  public boolean accept(ImportedInstance inst) {
    return inner.accept(inst);
  }

  public List<ImportedInstance> apply(List<ImportedInstance> in) {
    List<ImportedInstance> out = new ArrayList<>();
    if (in == null) {
      return out;
    }
    for (ImportedInstance inst : in) {
      addIfAccepted(out, inst);
    }
    return out;
  }

  void addIfAccepted(List<ImportedInstance> out, ImportedInstance inst) {
    if (accept(inst)) {
      out.add(inst);
    }
  }

  public String mode() {
    return inner.getMode();
  }

  public String query() {
    return inner.getQuery();
  }
}
