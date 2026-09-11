/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.image;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

class SimpleOpManagerTest {

  @Test
  void view2dChainOrderMatchesArchitecture() throws Exception {
    SimpleOpManager manager = SimpleOpManager.view2dChain();
    assertEquals(6, manager.getOperations().size());
    assertEquals("op.window.presets", manager.getOperations().get(0).getName());
    assertEquals("op.filter", manager.getOperations().get(1).getName());
    assertEquals("op.pseudocolor", manager.getOperations().get(2).getName());
    assertEquals("op.shutter", manager.getOperations().get(3).getName());
    assertEquals("op.overlay", manager.getOperations().get(4).getName());
    assertEquals("op.affine", manager.getOperations().get(5).getName());
    Object src = new Object();
    manager.setFirstNode(src);
    assertSame(src, manager.process());
    assertNotNull(manager.getNode("op.window.presets"));
  }

  @Test
  void disabledNodeStillForwardsInput() throws Exception {
    SimpleOpManager manager = new SimpleOpManager();
    WindowOp window = new WindowOp();
    window.setEnabled(false);
    manager.addImageOperationAction(window);
    Object src = "img";
    manager.setFirstNode(src);
    assertSame(src, manager.process());
  }
}
