/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.rt;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;

/** RT Plan label and referenced structure set. */
public class Plan {

  private final String label;

  public Plan(String label) {
    this.label = label == null ? "" : label;
  }

  public static Plan from(Attributes dataset) {
    if (dataset == null) {
      return new Plan("");
    }
    return new Plan(first(dataset.getString(Tag.RTPlanLabel), dataset.getString(Tag.RTPlanName)));
  }

  static String first(String a, String b) {
    if (a != null && !a.isBlank()) {
      return a;
    }
    return b == null ? "" : b;
  }

  public String label() {
    return label;
  }
}
