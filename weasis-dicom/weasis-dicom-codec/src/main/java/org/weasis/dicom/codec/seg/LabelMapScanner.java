/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.codec.seg;

import java.util.LinkedHashSet;
import java.util.Set;

public class LabelMapScanner {
  public Set<Integer> scan(byte[] pixels) {
    Set<Integer> labels = new LinkedHashSet<>();
    if (pixels == null) {
      return labels;
    }
    for (byte b : pixels) {
      int v = b & 0xFF;
      if (v != 0) {
        labels.add(v);
      }
    }
    return labels;
  }
}
