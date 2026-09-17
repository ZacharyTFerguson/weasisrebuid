/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.graphic.imp;

import jakarta.xml.bind.annotation.XmlRootElement;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import org.weasis.core.ui.model.graphic.AbstractDragGraphic;
import org.weasis.core.ui.model.graphic.AbstractGraphic;

@XmlRootElement(name = "PointGraphic")
public class PointGraphic extends AbstractDragGraphic {

  public PointGraphic() {
    super(1);
  }

  @Override
  public void buildShape() {
    Point2D.Double p = getHandlePoint(0);
    if (p == null) {
      setShape(null);
      return;
    }
    double r = 3;
    setShape(new Ellipse2D.Double(p.x - r, p.y - r, r * 2, r * 2));
  }

  @Override
  protected AbstractGraphic newInstance() {
    return new PointGraphic();
  }
}
