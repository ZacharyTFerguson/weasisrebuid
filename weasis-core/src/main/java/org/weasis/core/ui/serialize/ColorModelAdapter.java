/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.serialize;

import java.awt.Color;

public class ColorModelAdapter {
  public String marshal(Color c) {
    if (c == null) {
      return "";
    }
    return c.getRed() + "," + c.getGreen() + "," + c.getBlue() + "," + c.getAlpha();
  }

  public Color unmarshal(String v) {
    if (v == null || v.isBlank()) {
      return Color.BLACK;
    }
    String[] p = v.split(",");
    try {
      int r = Integer.parseInt(p[0].trim());
      int g = p.length > 1 ? Integer.parseInt(p[1].trim()) : 0;
      int b = p.length > 2 ? Integer.parseInt(p[2].trim()) : 0;
      int a = p.length > 3 ? Integer.parseInt(p[3].trim()) : 255;
      return new Color(r, g, b, a);
    } catch (NumberFormatException e) {
      return Color.BLACK;
    }
  }
}
