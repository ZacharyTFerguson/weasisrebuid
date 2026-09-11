/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.fusion;

/** Snapshot applied once when opening MPR from a fused 2D view. Not live-linked back to 2D. */
public final class FusionState {

  private final FusionOp op;
  private final String overlayLut;
  private final double overlayOpacity;
  private final double baseOpacity;

  public FusionState(FusionOp op, String overlayLut, double overlayOpacity, double baseOpacity) {
    this.op = op == null ? new FusionOp() : op.copy();
    this.overlayLut = overlayLut;
    this.overlayOpacity = overlayOpacity;
    this.baseOpacity = baseOpacity;
  }

  public FusionOp op() {
    return op.copy();
  }

  public String overlayLut() {
    return overlayLut;
  }

  public double overlayOpacity() {
    return overlayOpacity;
  }

  public double baseOpacity() {
    return baseOpacity;
  }
}
