/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.dockable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.weasis.acquire.dockable.components.actions.AbstractAcquireActionPanel;
import org.weasis.acquire.explorer.AcquireImageInfo;
import org.weasis.acquire.explorer.AcquireImageValues;
import org.weasis.core.api.gui.Insertable;

class EditionToolFactoryHaveTest {

  @Test
  void factoryProducesEditionToolNamedEdition() {
    EditionToolFactory factory = new EditionToolFactory();
    Insertable tool = factory.createInstance(null);
    assertInstanceOf(EditionTool.class, tool);
    assertEquals("Edition", tool.getComponentName());
    assertEquals(Insertable.Type.TOOL, factory.getType());
    assertTrue(factory.isComponentCreatedByThisFactory(tool));
    assertFalse(factory.isComponentCreatedByThisFactory(null));
  }

  @Test
  void actionPanelInitValuesBindImageSession() {
    AbstractAcquireActionPanel panel = new AbstractAcquireActionPanel();
    AcquireImageInfo info = new AcquireImageInfo();
    AcquireImageValues values = new AcquireImageValues();
    values.setRotation(90);
    panel.initValues(info, values);
    assertSame(info, panel.getImageInfo());
    assertSame(values, panel.getValues());
    assertFalse(panel.needValidationPanel());
  }
}
