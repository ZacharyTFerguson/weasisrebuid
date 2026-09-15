/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.graphic.imp.line;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import org.weasis.core.ui.model.graphic.AbstractDragGraphic;
import org.weasis.core.ui.model.graphic.AbstractGraphic;

public class ParallelLineGraphic extends AbstractDragGraphic {

  public ParallelLineGraphic() {
    super(4);
  }

  @Override
  public void buildShape() {
    Point2D.Double a = getHandlePoint(0);
    Point2D.Double b = getHandlePoint(1);
    Point2D.Double c = getHandlePoint(2);
    Point2D.Double d = getHandlePoint(3);
    if (a == null || b == null || c == null || d == null) {
      setShape(null);
      return;
    }
    Path2D path = new Path2D.Double();
    path.moveTo(a.x, a.y);
    path.lineTo(b.x, b.y);
    path.moveTo(c.x, c.y);
    path.lineTo(d.x, d.y);
    setShape(path);
  }

  @Override
  protected AbstractGraphic newInstance() {
    return new ParallelLineGraphic();
  }
}
