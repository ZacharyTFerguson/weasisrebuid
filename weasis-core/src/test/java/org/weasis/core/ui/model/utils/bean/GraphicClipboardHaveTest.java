/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.utils.bean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Point2D;
import org.junit.jupiter.api.Test;
import org.weasis.core.ui.model.AbstractGraphicModel;
import org.weasis.core.ui.model.graphic.imp.line.LineGraphic;

class GraphicClipboardHaveTest {

  @Test
  void copySelectedPastesIndependentCopy() {
    LineGraphic line = new LineGraphic();
    line.setHandlePoint(0, new Point2D.Double(0, 0));
    line.setHandlePoint(1, new Point2D.Double(3, 4));
    line.setSelected(true);
    AbstractGraphicModel model = new AbstractGraphicModel();
    model.addGraphic(line);
    model.copySelected();
    assertTrue(model.clipboard().hasGraphics());
    assertEquals(1, model.paste());
    assertEquals(2, model.getModels().size());
    LineGraphic pasted = (LineGraphic) model.getModels().get(1);
    assertNotEquals(line.getUuid(), pasted.getUuid());
    assertEquals(5.0, pasted.getLength(), 1e-9);
    line.setHandlePoint(1, new Point2D.Double(10, 0));
    assertEquals(5.0, pasted.getLength(), 1e-9);
  }
}
