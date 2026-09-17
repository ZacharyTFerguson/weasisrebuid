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

import jakarta.xml.bind.annotation.XmlRootElement;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.List;
import org.weasis.core.ui.model.graphic.AbstractDragGraphic;
import org.weasis.core.ui.model.graphic.AbstractGraphic;

@XmlRootElement(name = "PolylineGraphic")
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
    double length = pathLength(pts);
    if (length < 0.5) {
      setLabel(new String[0]);
    } else {
      setLabel(new String[] {String.format("%.1f px", length)});
    }
  }

  public static double pathLength(List<Point2D.Double> pts) {
    double len = 0;
    for (int i = 1; i < pts.size(); i++) {
      len += pts.get(i - 1).distance(pts.get(i));
    }
    return len;
  }

  @Override
  protected AbstractGraphic newInstance() {
    return new PolylineGraphic();
  }
}
