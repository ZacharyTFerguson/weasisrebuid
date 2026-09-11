/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.editor.image.sync;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.weasis.core.ui.editor.image.SynchView;

class SyncControllerTest {

  @AfterEach
  void reset() {
    SyncController.resetAppWideForTests();
  }

  @Test
  void defaultStackIsScrollOnlyTileEnablesAll() {
    ViewSyncHandle stack = new ViewSyncHandle("v1", "c1");
    assertEquals(SynchView.STACK, stack.synch());
    assertTrue(stack.actionEnabled(SyncAction.SCROLL));
    assertFalse(stack.actionEnabled(SyncAction.PAN));
    ViewSyncHandle tile = new ViewSyncHandle("v2", "c1");
    tile.setSynch(SynchView.TILE);
    assertTrue(tile.actionEnabled(SyncAction.ZOOM));
    assertTrue(tile.actionEnabled(SyncAction.WINDOW_LEVEL));
  }

  @Test
  void mx14OneAppWideManualDoesNotLeaveContainer() {
    ViewSyncHandle a = new ViewSyncHandle("a", "c1");
    ViewSyncHandle b = new ViewSyncHandle("b", "c1");
    ViewSyncHandle other = new ViewSyncHandle("o", "c2");
    SyncController.ManualSyncSession s = SyncController.startManual("c1", List.of(a, b, other));
    assertEquals(2, s.members().size());
    assertTrue(s.contains(a));
    assertTrue(s.leavesContainer(other));
    assertThrows(
        IllegalStateException.class, () -> SyncController.startManual("c2", List.of(other)));
    SyncController.stopManual();
    SyncController.startManual("c2", List.of(other));
  }

  @Test
  void autoForCrossesContainersWhenAllowed() {
    ViewSyncHandle a = new ViewSyncHandle("a", "c1");
    ViewSyncHandle b = new ViewSyncHandle("b", "c2");
    a.setFrameOfReferenceUid("1.2.3");
    b.setFrameOfReferenceUid("1.2.3");
    assertTrue(SyncController.autoPeers(a, b, true));
    assertFalse(SyncController.autoPeers(a, b, false));
    ViewSyncHandle orphan = new ViewSyncHandle("z", "c1");
    assertFalse(SyncController.autoPeers(a, orphan, true));
    a.setActionEnabled(SyncAction.ZOOM, true);
    b.setActionEnabled(SyncAction.ZOOM, false);
    assertFalse(SyncController.actionMovesPeer(a, b, SyncAction.ZOOM));
    b.setActionEnabled(SyncAction.ZOOM, true);
    assertTrue(SyncController.actionMovesPeer(a, b, SyncAction.ZOOM));
  }
}
