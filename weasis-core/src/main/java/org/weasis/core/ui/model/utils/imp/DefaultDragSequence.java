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
import org.weasis.core.ui.model.graphic.DragGraphic;
import org.weasis.core.ui.model.utils.Draggable;

/** Drags one handle of a {@link DragGraphic}. */
public class DefaultDragSequence extends Draggable {

  private final DragGraphic graphic;
  private final int handle;
  private boolean active;

  public DefaultDragSequence(DragGraphic graphic, int handle) {
    this.graphic = graphic;
    this.handle = handle;
  }

  public DragGraphic graphic() {
    return graphic;
  }

  @Override
  public boolean start(Point2D.Double point) {
    if (graphic == null || point == null) {
      return false;
    }
    graphic.setHandlePoint(handle, copy(point));
    active = true;
    return true;
  }

  @Override
  public boolean drag(Point2D.Double point) {
    if (!active || graphic == null || point == null) {
      return false;
    }
    graphic.setHandlePoint(handle, copy(point));
    return true;
  }

  @Override
  public boolean complete() {
    active = false;
    return true;
  }
}
