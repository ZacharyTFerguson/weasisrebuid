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
import org.weasis.dicom.explorer.ImportedInstance;

/** Explorer series thumbnail filter (modality or series description substring). */
public class SeriesFilter {

  private String query = "";

  public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query == null ? "" : query;
  }

  public boolean accept(ImportedInstance inst) {
    if (inst == null) {
      return false;
    }
    if (query.isBlank()) {
      return true;
    }
    String q = query.toLowerCase(Locale.ROOT);
    return inst.modality().toLowerCase(Locale.ROOT).contains(q)
        || inst.seriesDescription().toLowerCase(Locale.ROOT).contains(q);
  }
}
