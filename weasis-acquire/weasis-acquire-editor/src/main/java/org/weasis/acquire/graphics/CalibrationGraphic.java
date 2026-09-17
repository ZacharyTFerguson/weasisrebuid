/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.graphics;

import java.awt.geom.Point2D;
import org.weasis.acquire.utils.GraphicHelper;
import org.weasis.core.ui.model.graphic.AbstractGraphic;
import org.weasis.core.ui.model.graphic.imp.line.LineGraphic;

/** Distance line used to set millimetres-per-pixel on a dicomizer photo. */
public class CalibrationGraphic extends LineGraphic {

  public CalibrationGraphic() {}

  public CalibrationGraphic(double x1, double y1, double x2, double y2) {
    setHandlePoint(0, new Point2D.Double(x1, y1));
    setHandlePoint(1, new Point2D.Double(x2, y2));
  }

  public double pixelLength() {
    return GraphicHelper.pixelLength(getHandlePoint(0), getHandlePoint(1));
  }

  @Override
  protected AbstractGraphic newInstance() {
    return new CalibrationGraphic();
  }
}
