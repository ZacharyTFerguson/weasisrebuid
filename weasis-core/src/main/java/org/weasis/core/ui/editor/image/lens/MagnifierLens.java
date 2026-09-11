/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.editor.image.lens;

/**
 * Magnifying lens. Freeze Parameters (keep W/L, LUT, filters while scrolling) ≠ Freeze Image (keep
 * pixels and processing). MX-07 names. Wheel changes lens zoom; double-click matches parent zoom.
 */
public final class MagnifierLens {

  private boolean visible;
  private double zoom = 2.0;
  private boolean synchronizeToParentZoom;
  private boolean showDrawings = true;
  private boolean freezeParameters;
  private boolean freezeImage;

  public boolean isVisible() {
    return visible;
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  public double getZoom() {
    return zoom;
  }

  public void setZoom(double zoom) {
    this.zoom = Math.max(0.01, zoom);
  }

  public void wheel(int rotation) {
    if (rotation < 0) {
      setZoom(zoom * 1.1);
    } else if (rotation > 0) {
      setZoom(zoom / 1.1);
    }
  }

  public void doubleClickMatchParent(double parentZoom) {
    if (parentZoom > 0) {
      this.zoom = parentZoom;
    }
  }

  public boolean isSynchronizeToParentZoom() {
    return synchronizeToParentZoom;
  }

  public void setSynchronizeToParentZoom(boolean synchronizeToParentZoom) {
    this.synchronizeToParentZoom = synchronizeToParentZoom;
  }

  public boolean isShowDrawings() {
    return showDrawings;
  }

  public void setShowDrawings(boolean showDrawings) {
    this.showDrawings = showDrawings;
  }

  public boolean isFreezeParameters() {
    return freezeParameters;
  }

  public void setFreezeParameters(boolean freezeParameters) {
    this.freezeParameters = freezeParameters;
  }

  public boolean isFreezeImage() {
    return freezeImage;
  }

  public void setFreezeImage(boolean freezeImage) {
    this.freezeImage = freezeImage;
  }

  public void resetFreeze() {
    freezeParameters = false;
    freezeImage = false;
  }
}
