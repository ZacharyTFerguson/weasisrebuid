/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.rs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** In-memory QIDO/WADO-RS query outcome (synthetic fixtures in tests). */
public final class RsQueryResult {

  private int httpStatus;
  private final List<String> jsonEntries = new ArrayList<>();

  public int httpStatus() {
    return httpStatus;
  }

  public void setHttpStatus(int httpStatus) {
    this.httpStatus = httpStatus;
  }

  public List<String> jsonEntries() {
    return Collections.unmodifiableList(jsonEntries);
  }

  public void addJsonEntry(String entry) {
    jsonEntries.add(entry);
  }

  public boolean success() {
    return httpStatus >= 200 && httpStatus < 300;
  }
}
