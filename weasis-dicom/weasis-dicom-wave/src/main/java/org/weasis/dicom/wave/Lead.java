/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.wave;

/** Standard ECG lead labels (CID 3001 / common Channel Label values). */
public enum Lead {
  I("I"),
  II("II"),
  III("III"),
  AVR("aVR"),
  AVL("aVL"),
  AVF("aVF"),
  V1("V1"),
  V2("V2"),
  V3("V3"),
  V4("V4"),
  V5("V5"),
  V6("V6"),
  UNKNOWN("?");

  private final String label;

  Lead(String label) {
    this.label = label;
  }

  public String label() {
    return label;
  }

  public static Lead fromLabel(String raw) {
    if (raw == null || raw.isBlank()) {
      return UNKNOWN;
    }
    String t = raw.trim();
    String compact = t.replace("Lead ", "").replace("LEAD ", "").replace("_", "").replace(" ", "");
    for (Lead lead : values()) {
      if (lead == UNKNOWN) {
        continue;
      }
      if (lead.label.equalsIgnoreCase(t)
          || lead.label.equalsIgnoreCase(compact)
          || lead.name().equalsIgnoreCase(compact)
          || lead.name().equalsIgnoreCase(t)) {
        return lead;
      }
    }
    return UNKNOWN;
  }
}
