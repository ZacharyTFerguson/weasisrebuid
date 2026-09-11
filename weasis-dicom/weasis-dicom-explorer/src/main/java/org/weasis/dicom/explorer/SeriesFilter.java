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
import java.util.Locale;
import java.util.Set;
import org.weasis.core.api.service.WProperties;

/**
 * Explorer series filter. Pref {@code weasis.dicom.explorer.filter.mode}: {@code all} / {@code
 * modality} / {@code description}.
 */
public final class SeriesFilter {

  public static final String PREF_MODE = "weasis.dicom.explorer.filter.mode";
  public static final String MODE_ALL = "all";
  public static final String MODE_MODALITY = "modality";
  public static final String MODE_DESCRIPTION = "description";

  private final String mode;
  private final Set<String> modalities;
  private final String descriptionContains;

  public SeriesFilter(String mode, Set<String> modalities, String descriptionContains) {
    this.mode = mode == null || mode.isBlank() ? MODE_ALL : mode.toLowerCase(Locale.ROOT);
    this.modalities = modalities == null ? Set.of() : Set.copyOf(modalities);
    this.descriptionContains = descriptionContains == null ? "" : descriptionContains;
  }

  public static SeriesFilter fromPrefs(WProperties prefs) {
    String mode = prefs == null ? MODE_ALL : prefs.getProperty(PREF_MODE, MODE_ALL);
    return new SeriesFilter(mode, Set.of(), "");
  }

  public boolean accept(ImportedInstance inst) {
    if (inst == null) {
      return false;
    }
    if (MODE_MODALITY.equals(mode) && !modalities.isEmpty()) {
      return modalities.contains(inst.modality().toUpperCase(Locale.ROOT));
    }
    if (MODE_DESCRIPTION.equals(mode) && !descriptionContains.isBlank()) {
      return inst.seriesDescription()
          .toLowerCase(Locale.ROOT)
          .contains(descriptionContains.toLowerCase(Locale.ROOT));
    }
    return true;
  }

  public List<ImportedInstance> apply(List<ImportedInstance> in) {
    List<ImportedInstance> out = new ArrayList<>();
    for (ImportedInstance inst : in) {
      if (accept(inst)) {
        out.add(inst);
      }
    }
    return out;
  }

  public String mode() {
    return mode;
  }
}
