/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.graphic.imp.area;

import java.awt.geom.Ellipse2D;
import org.weasis.core.ui.model.graphic.AbstractDragGraphicArea;
import org.weasis.core.ui.model.graphic.AbstractGraphic;
import org.weasis.core.ui.model.graphic.GraphicMath;

public class ThreePointsCircleGraphic extends AbstractDragGraphicArea {

  public ThreePointsCircleGraphic() {
    super(3);
  }

  public Ellipse2D.Double circumcircle() {
    return GraphicMath.circumcircle(getHandlePoint(0), getHandlePoint(1), getHandlePoint(2));
  }

  @Override
  public double getAreaValue() {
    Ellipse2D.Double e = circumcircle();
    if (e == null) {
      return 0;
    }
    double r = e.width / 2.0;
    return Math.PI * r * r;
  }

  @Override
  public void buildShape() {
    Ellipse2D.Double e = circumcircle();
    setShape(e);
  }

  @Override
  protected AbstractGraphic newInstance() {
    return new ThreePointsCircleGraphic();
  }
}
