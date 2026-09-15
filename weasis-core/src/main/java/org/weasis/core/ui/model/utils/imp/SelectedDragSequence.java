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
import java.util.List;
import org.weasis.core.ui.model.graphic.DragGraphic;
import org.weasis.core.ui.model.graphic.Graphic;
import org.weasis.core.ui.model.utils.Draggable;

/** Translates every handle of the selected graphics by the same delta. */
public class SelectedDragSequence extends Draggable {

  private final List<Graphic> graphics;
  private Point2D.Double origin;

  public SelectedDragSequence(List<Graphic> graphics) {
    this.graphics = graphics == null ? List.of() : List.copyOf(graphics);
  }

  @Override
  public boolean start(Point2D.Double point) {
    origin = copy(point);
    return origin != null && !graphics.isEmpty();
  }

  @Override
  public boolean drag(Point2D.Double point) {
    if (origin == null || point == null) {
      return false;
    }
    translateAll(point.getX() - origin.getX(), point.getY() - origin.getY());
    origin = copy(point);
    return true;
  }

  void translateAll(double dx, double dy) {
    for (Graphic graphic : graphics) {
      translate(graphic, dx, dy);
    }
  }

  static void translate(Graphic graphic, double dx, double dy) {
    if (graphic instanceof DragGraphic drag) {
      translateHandles(drag, dx, dy);
    }
  }

  static void translateHandles(DragGraphic drag, double dx, double dy) {
    for (int i = 0; i < drag.getHandlePointTotalNumber(); i++) {
      shiftHandle(drag, i, dx, dy);
    }
  }

  static void shiftHandle(DragGraphic drag, int index, double dx, double dy) {
    Point2D.Double handle = drag.getHandlePoint(index);
    if (handle != null) {
      drag.setHandlePoint(index, new Point2D.Double(handle.getX() + dx, handle.getY() + dy));
    }
  }
}
