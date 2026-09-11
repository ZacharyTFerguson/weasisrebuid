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

import org.weasis.core.ui.model.graphic.AbstractDragGraphicArea;
import org.weasis.core.ui.model.graphic.AbstractGraphic;

public class ThreePointsCircleGraphic extends AbstractDragGraphicArea {

  public ThreePointsCircleGraphic() {
    super(3);
  }

  @Override
  public void buildShape() {
    setShape(null);
  }

  @Override
  protected AbstractGraphic newInstance() {
    return new ThreePointsCircleGraphic();
  }
}
