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

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import org.weasis.core.ui.editor.image.SynchView;

/**
 * View sync handle. Default Stack = Scroll only. Default Tile = all user actions. FoR groups auto
 * sync; orphans excluded from auto only.
 */
public final class ViewSyncHandle {

  private final String viewId;
  private final String containerId;
  private String frameOfReferenceUid;
  private String orientation = "AXIAL";
  private SynchView synch = SynchView.STACK;
  private final EnumSet<SyncAction> enabled = EnumSet.noneOf(SyncAction.class);
  private boolean autoOverlayOn;
  private boolean masterSynchronize;

  public ViewSyncHandle(String viewId, String containerId) {
    this.viewId = Objects.requireNonNull(viewId, "viewId");
    this.containerId = Objects.requireNonNull(containerId, "containerId");
    applyModeDefaults(SynchView.STACK);
  }

  public String viewId() {
    return viewId;
  }

  public String containerId() {
    return containerId;
  }

  public String frameOfReferenceUid() {
    return frameOfReferenceUid;
  }

  public void setFrameOfReferenceUid(String frameOfReferenceUid) {
    this.frameOfReferenceUid = frameOfReferenceUid;
  }

  public String orientation() {
    return orientation;
  }

  public void setOrientation(String orientation) {
    this.orientation = orientation == null ? "AXIAL" : orientation;
  }

  public SynchView synch() {
    return synch;
  }

  public void setSynch(SynchView synch) {
    this.synch = synch == null ? SynchView.NONE : synch;
    applyModeDefaults(this.synch);
  }

  void applyModeDefaults(SynchView mode) {
    enabled.clear();
    if (mode == SynchView.STACK) {
      enabled.add(SyncAction.SCROLL);
    } else if (mode == SynchView.TILE) {
      enabled.addAll(EnumSet.allOf(SyncAction.class));
    }
  }

  public Set<SyncAction> enabledActions() {
    return Set.copyOf(enabled);
  }

  public void setActionEnabled(SyncAction action, boolean on) {
    if (action == null) {
      return;
    }
    if (on) {
      enabled.add(action);
    } else {
      enabled.remove(action);
    }
  }

  public boolean actionEnabled(SyncAction action) {
    return enabled.contains(action);
  }

  public boolean autoOverlayOn() {
    return autoOverlayOn;
  }

  public void setAutoOverlayOn(boolean autoOverlayOn) {
    this.autoOverlayOn = autoOverlayOn;
  }

  public boolean masterSynchronize() {
    return masterSynchronize;
  }

  public void setMasterSynchronize(boolean masterSynchronize) {
    this.masterSynchronize = masterSynchronize;
  }
}
