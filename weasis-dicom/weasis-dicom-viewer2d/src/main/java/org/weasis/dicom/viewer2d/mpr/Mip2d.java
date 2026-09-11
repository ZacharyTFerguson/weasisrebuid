/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.mpr;

/**
 * 2D MIP is a mode of the 2D viewer (SRS §4.5 / CHECKLIST MIP rows). Thickness is N slices on each
 * side of the current image. Live graphics vanish on scroll until Build a new series.
 */
public final class Mip2d {

  private MipProjector.Mode mode = MipProjector.Mode.NONE;
  private int thickness;
  private boolean liveGraphics;
  private int frameIndex;

  public MipProjector.Mode mode() {
    return mode;
  }

  public void setMode(MipProjector.Mode mode) {
    MipProjector.Mode prev = this.mode;
    this.mode = mode == null ? MipProjector.Mode.NONE : mode;
    if (prev == MipProjector.Mode.NONE && this.mode != MipProjector.Mode.NONE) {
      if (thickness == 0) {
        thickness = 2;
      }
    }
  }

  public int thickness() {
    return thickness;
  }

  public void setThickness(int thickness) {
    this.thickness = Math.max(0, thickness);
  }

  /** N=3 → 7 slices (3 before + current + 3 after). */
  public int slabSize() {
    if (mode == MipProjector.Mode.NONE) {
      return 1;
    }
    return 2 * thickness + 1;
  }

  public boolean indicatorVisible() {
    return mode != MipProjector.Mode.NONE;
  }

  public void setLiveGraphics(boolean liveGraphics) {
    this.liveGraphics = liveGraphics;
  }

  public boolean liveGraphics() {
    return liveGraphics;
  }

  public void scrollTo(int frameIndex) {
    this.frameIndex = frameIndex;
    this.liveGraphics = false;
  }

  public int frameIndex() {
    return frameIndex;
  }
}
