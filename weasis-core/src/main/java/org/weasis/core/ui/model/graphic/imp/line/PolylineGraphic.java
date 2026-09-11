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

public class PolylineGraphic extends AbstractDragGraphic {

  public PolylineGraphic() {
    super(0);
  }

  @Override
  public void buildShape() {
    var pts = getPts();
    if (pts.isEmpty()) {
      setShape(null);
      return;
    }
    Path2D path = new Path2D.Double();
    Point2D.Double first = pts.getFirst();
    path.moveTo(first.x, first.y);
    for (int i = 1; i < pts.size(); i++) {
      Point2D.Double p = pts.get(i);
      path.lineTo(p.x, p.y);
    }
    setShape(path);
  }

  @Override
  protected AbstractGraphic newInstance() {
    return new PolylineGraphic();
  }
}
