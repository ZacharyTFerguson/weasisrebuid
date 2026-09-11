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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * MX-14: one session, one app-wide <strong>manual</strong> sync. Manual does not leave the
 * container. Auto-sync MAY cross Stack containers of the same patient/study group.
 */
public final class SyncController {

  private static final AtomicReference<ManualSyncSession> APP_WIDE = new AtomicReference<>();

  private SyncController() {}

  public static void resetAppWideForTests() {
    APP_WIDE.set(null);
  }

  public static ManualSyncSession currentManual() {
    return APP_WIDE.get();
  }

  /**
   * Start manual sync in one container. Fails if another container already owns the app-wide manual
   * session.
   */
  public static ManualSyncSession startManual(String containerId, List<ViewSyncHandle> views) {
    Objects.requireNonNull(containerId, "containerId");
    ManualSyncSession existing = APP_WIDE.get();
    if (existing != null && !existing.containerId().equals(containerId)) {
      throw new IllegalStateException(
          "MX-14: one app-wide manual sync already owned by " + existing.containerId());
    }
    List<ViewSyncHandle> members = new ArrayList<>();
    if (views != null) {
      for (ViewSyncHandle v : views) {
        if (v != null && containerId.equals(v.containerId())) {
          v.setActionEnabled(SyncAction.SCROLL, true);
          members.add(v);
        }
      }
    }
    ManualSyncSession session = new ManualSyncSession(containerId, members);
    APP_WIDE.set(session);
    return session;
  }

  public static void stopManual() {
    APP_WIDE.set(null);
  }

  /** Auto FoR: same FrameOfReferenceUID, including across Stack containers when allowed. */
  public static boolean autoPeers(ViewSyncHandle a, ViewSyncHandle b, boolean crossContainer) {
    if (a == null || b == null || a == b) {
      return false;
    }
    String fa = a.frameOfReferenceUid();
    String fb = b.frameOfReferenceUid();
    if (fa == null || fa.isBlank() || fb == null || fb.isBlank() || !fa.equals(fb)) {
      return false;
    }
    if (!crossContainer && !a.containerId().equals(b.containerId())) {
      return false;
    }
    return true;
  }

  public static boolean actionMovesPeer(ViewSyncHandle src, ViewSyncHandle dst, SyncAction action) {
    if (src == null || dst == null || action == null) {
      return false;
    }
    return src.actionEnabled(action) && dst.actionEnabled(action);
  }

  public record ManualSyncSession(String containerId, List<ViewSyncHandle> members) {
    public ManualSyncSession {
      containerId = Objects.requireNonNull(containerId, "containerId");
      members = List.copyOf(members == null ? List.of() : members);
    }

    public boolean contains(ViewSyncHandle view) {
      return view != null && members.contains(view);
    }

    public boolean leavesContainer(ViewSyncHandle view) {
      return view != null && !containerId.equals(view.containerId());
    }
  }
}
