/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.utils.imp;

import java.awt.geom.Point2D;
import org.weasis.core.ui.model.graphic.AbstractGraphicLabel;
import org.weasis.core.ui.model.utils.Draggable;

/** Moves a graphic label offset in image space. */
public class DragLabelSequence extends Draggable {

  private final AbstractGraphicLabel label;
  private Point2D.Double origin;
  private double startX;
  private double startY;

  public DragLabelSequence(AbstractGraphicLabel label) {
    this.label = label;
  }

  @Override
  public boolean start(Point2D.Double point) {
    if (label == null || point == null) {
      return false;
    }
    origin = copy(point);
    startX = label.getOffsetX();
    startY = label.getOffsetY();
    return true;
  }

  @Override
  public boolean drag(Point2D.Double point) {
    if (origin == null || point == null || label == null) {
      return false;
    }
    label.setOffset(startX + point.getX() - origin.getX(), startY + point.getY() - origin.getY());
    return true;
  }
}
