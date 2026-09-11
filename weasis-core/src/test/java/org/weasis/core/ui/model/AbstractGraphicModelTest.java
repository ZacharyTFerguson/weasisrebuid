/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.weasis.core.ui.model.graphic.imp.PointGraphic;

class AbstractGraphicModelTest {

  @Test
  void addRemoveAndSelected() {
    AbstractGraphicModel model = new AbstractGraphicModel();
    PointGraphic a = new PointGraphic();
    PointGraphic b = new PointGraphic();
    b.setSelected(true);
    model.addGraphic(a);
    model.addGraphic(b);
    assertEquals(2, model.getModels().size());
    assertEquals(1, model.getSelectedGraphics().size());
    model.removeGraphic(a);
    assertEquals(1, model.getModels().size());
    model.clear();
    assertTrue(model.getModels().isEmpty());
  }
}
