/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer3d;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * MX-15: the view being interacted with always applies camera/photometric/rendering locally. Only
 * peer views honor the per-action sync map. Camera Pan/Zoom/Rotation ON by default; photometric and
 * rendering actions OFF. Apply to all views does not FoR-filter.
 */
public final class View3dSync {

  public enum Action {
    PAN,
    ZOOM,
    ROTATION,
    WINDOW_LEVEL,
    PRESET,
    LUT_SHAPE,
    INVERT_LUT,
    LUT,
    RENDERING_TYPE,
    VOLUME_OPACITY,
    VOLUME_SHADING,
    ORTHOGRAPHIC,
    CROSSHAIR_CUT
  }

  private final Map<Action, Boolean> peerSync = new EnumMap<>(Action.class);

  public View3dSync() {
    for (Action a : Action.values()) {
      boolean camera = a == Action.PAN || a == Action.ZOOM || a == Action.ROTATION;
      peerSync.put(a, camera);
    }
  }

  public boolean peerEnabled(Action action) {
    return Boolean.TRUE.equals(peerSync.get(action));
  }

  public void setPeerEnabled(Action action, boolean enabled) {
    peerSync.put(action, enabled);
  }

  public void apply(Action action, View3d source, List<View3d> views) {
    source.applyLocal(action);
    if (views == null) {
      return;
    }
    for (View3d peer : views) {
      if (peer != source && peerEnabled(action)) {
        peer.applyLocal(action);
      }
    }
  }
}
