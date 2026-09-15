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

import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import org.weasis.core.ui.model.graphic.AbstractDragGraphic;
import org.weasis.core.ui.model.graphic.AbstractGraphic;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "PixelInfoGraphic")
public class PixelInfoGraphic extends AbstractDragGraphic {

  public PixelInfoGraphic() {
    super(1);
  }

  @Override
  public void buildShape() {
    Point2D.Double p = getHandlePoint(0);
    if (p == null) {
      setShape(null);
      return;
    }
    setShape(new Ellipse2D.Double(p.x - 2, p.y - 2, 4, 4));
  }

  @Override
  protected AbstractGraphic newInstance() {
    return new PixelInfoGraphic();
  }
}
