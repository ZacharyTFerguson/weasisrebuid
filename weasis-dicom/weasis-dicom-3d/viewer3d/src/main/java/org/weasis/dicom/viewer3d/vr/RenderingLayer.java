/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer3d.vr;

import java.util.ArrayList;
import java.util.List;

public class RenderingLayer {

  private RenderingType type = RenderingType.COMPOSITE;
  private CrosshairCutMode cutMode = CrosshairCutMode.NONE;
  private final List<RenderingLayerChangeListener> listeners = new ArrayList<>();

  public RenderingType getType() {
    return type;
  }

  public void setType(RenderingType type) {
    if (type != null && type != this.type) {
      this.type = type;
      fire();
    }
  }

  public CrosshairCutMode getCutMode() {
    return cutMode;
  }

  public void setCutMode(CrosshairCutMode cutMode) {
    if (cutMode != null && cutMode != this.cutMode) {
      this.cutMode = cutMode;
      fire();
    }
  }

  public void addListener(RenderingLayerChangeListener listener) {
    if (listener != null) {
      listeners.add(listener);
    }
  }

  private void fire() {
    for (RenderingLayerChangeListener listener : List.copyOf(listeners)) {
      listener.renderingLayerChanged(this);
    }
  }
}
