/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.util;

import java.awt.Component;
import java.awt.event.MouseEvent;

public class MouseEventDouble extends MouseEvent {
  private final double imageX;
  private final double imageY;

  public MouseEventDouble(MouseEvent e, double imageX, double imageY) {
    super(
        (Component) e.getSource(),
        e.getID(),
        e.getWhen(),
        e.getModifiersEx(),
        e.getX(),
        e.getY(),
        e.getClickCount(),
        e.isPopupTrigger(),
        e.getButton());
    this.imageX = imageX;
    this.imageY = imageY;
  }

  public double getImageX() {
    return imageX;
  }

  public double getImageY() {
    return imageY;
  }
}
