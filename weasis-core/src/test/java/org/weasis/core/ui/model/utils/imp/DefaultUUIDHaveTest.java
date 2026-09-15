/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.utils.imp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.weasis.core.ui.model.graphic.imp.line.LineGraphic;
import org.weasis.core.ui.model.utils.UUIDable;

class DefaultUUIDHaveTest {

  @Test
  void graphicsAndExplicitIdsStayStable() {
    LineGraphic line = new LineGraphic();
    assertInstanceOf(DefaultUUID.class, line);
    assertInstanceOf(UUIDable.class, line);
    assertTrue(line.getUuid().length() > 8);
    line.setUuid("line-1");
    assertEquals("line-1", line.getUuid());
    line.setUuid(null);
    assertEquals("line-1", line.getUuid());
    DefaultUUID other = new DefaultUUID("layer-9");
    assertEquals("layer-9", other.getUuid());
    assertNotEquals(new DefaultUUID().getUuid(), new DefaultUUID().getUuid());
  }
}
