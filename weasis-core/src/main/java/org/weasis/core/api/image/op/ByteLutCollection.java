/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package org.weasis.core.api.image.op;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ByteLutCollection {
  public static final String GRAY = "Gray";
  public static final String INVERSE = "Inverse";

  private final List<String> luts = new ArrayList<>(List.of(GRAY, INVERSE, "Hot Iron", "PET", "Rainbow"));

  public List<String> getLutCollection() {
    return Collections.unmodifiableList(luts);
  }

  public byte[][] getLut(String name) {
    byte[][] rgb = new byte[3][256];
    for (int i = 0; i < 256; i++) {
      int v = INVERSE.equals(name) ? 255 - i : i;
      rgb[0][i] = rgb[1][i] = rgb[2][i] = (byte) v;
    }
    return rgb;
  }
}
