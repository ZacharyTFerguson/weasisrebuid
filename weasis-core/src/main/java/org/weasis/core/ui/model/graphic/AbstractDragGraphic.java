/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.graphic;

import java.awt.geom.Point2D;
import java.util.List;
import org.weasis.core.ui.model.utils.imp.DefaultDragSequence;

public abstract class AbstractDragGraphic extends AbstractGraphic implements DragGraphic {

  protected AbstractDragGraphic(int expectedPts) {
    super(expectedPts);
  }

  @Override
  public Point2D.Double getHandlePoint(int index) {
    List<Point2D.Double> pts = ptsMutable();
    if (index < 0 || index >= pts.size()) {
      return null;
    }
    return pts.get(index);
  }

  @Override
  public void setHandlePoint(int index, Point2D.Double point) {
    List<Point2D.Double> pts = ptsMutable();
    if (index < 0) {
      return;
    }
    while (pts.size() <= index) {
      pts.add(new Point2D.Double());
    }
    pts.set(index, point == null ? new Point2D.Double() : point);
    buildShape();
  }

  @Override
  public int getHandlePointTotalNumber() {
    return getPtsNumber();
  }

  public DefaultDragSequence dragHandle(int handle) {
    return new DefaultDragSequence(this, handle);
  }
}
