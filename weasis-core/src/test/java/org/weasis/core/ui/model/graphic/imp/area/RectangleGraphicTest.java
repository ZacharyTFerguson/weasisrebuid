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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.awt.geom.Point2D;
import org.junit.jupiter.api.Test;
import org.weasis.core.ui.model.graphic.GraphicArea;

class RectangleGraphicTest {

  @Test
  void areaFromTwoCorners() {
    RectangleGraphic r = new RectangleGraphic();
    assertInstanceOf(GraphicArea.class, r);
    r.setHandlePoint(0, new Point2D.Double(0, 0));
    r.setHandlePoint(1, new Point2D.Double(10, 5));
    assertEquals(50.0, r.getAreaValue(), 1e-9);
  }
}
