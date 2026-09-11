/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.editor.image.dock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class DockingLayoutTest {

  @Test
  void splitCloseMaximizeAndNotPersisted() {
    DockingLayout layout = new DockingLayout();
    layout.openTab("a");
    layout.openTab("b");
    layout.split("a", DockingLayout.Split.RIGHT);
    assertTrue(layout.tabs().contains("a-right"));
    layout.maximize("a");
    assertTrue(layout.isMaximized());
    assertTrue(layout.maximizeCoversToolStrips());
    layout.restore();
    assertFalse(layout.isMaximized());
    layout.closeOthers("b");
    assertEquals(List.of("b"), layout.tabs());
    layout.closeAll();
    assertTrue(layout.tabs().isEmpty());
    layout.openTab("z");
    LayoutPersistence.save(layout);
    DockingLayout restarted = LayoutPersistence.loadOnRestart();
    assertTrue(restarted.tabs().isEmpty());
    assertNull(restarted.maximizedTab());
    layout.setToolPaneMode("tools", DockingLayout.PaneMode.PINNED_OVERLAY);
    assertEquals(DockingLayout.PaneMode.PINNED_OVERLAY, layout.toolPaneMode("tools"));
  }
}
