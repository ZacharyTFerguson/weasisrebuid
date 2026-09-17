/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.utils;

import java.awt.geom.Point2D;

/** Start / drag / complete sequence for a graphic handle, selection, or label. */
public class Draggable {

  public boolean start(Point2D.Double point) {
    return false;
  }

  public boolean drag(Point2D.Double point) {
    return false;
  }

  public boolean complete() {
    return true;
  }

  protected static Point2D.Double copy(Point2D.Double point) {
    return point == null ? null : new Point2D.Double(point.getX(), point.getY());
  }
}
