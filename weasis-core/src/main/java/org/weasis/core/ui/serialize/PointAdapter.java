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

import java.awt.geom.Point2D;

public class PointAdapter {
  public String marshal(Point2D p) {
    if (p == null) {
      return "";
    }
    return p.getX() + "," + p.getY();
  }

  public Point2D.Double unmarshal(String v) {
    if (v == null || v.isBlank()) {
      return new Point2D.Double();
    }
    String[] p = v.split(",");
    try {
      return new Point2D.Double(Double.parseDouble(p[0]), p.length > 1 ? Double.parseDouble(p[1]) : 0);
    } catch (NumberFormatException e) {
      return new Point2D.Double();
    }
  }
}
