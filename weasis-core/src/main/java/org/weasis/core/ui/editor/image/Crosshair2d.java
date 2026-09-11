/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.editor.image;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** 2D 3D-cursor / crosshair. Peers sharing FrameOfReferenceUID follow. */
public final class Crosshair2d {

  private double x;
  private double y;
  private String frameOfReferenceUid;
  private final List<Crosshair2d> peers = new ArrayList<>();

  public double x() {
    return x;
  }

  public double y() {
    return y;
  }

  public String frameOfReferenceUid() {
    return frameOfReferenceUid;
  }

  public void setFrameOfReferenceUid(String frameOfReferenceUid) {
    this.frameOfReferenceUid = frameOfReferenceUid;
  }

  public void addPeer(Crosshair2d peer) {
    if (peer != null && peer != this && !peers.contains(peer)) {
      peers.add(peer);
    }
  }

  public void moveTo(double x, double y) {
    this.x = x;
    this.y = y;
    for (Crosshair2d p : peers) {
      if (sameFor(p)) {
        p.x = x;
        p.y = y;
      }
    }
  }

  boolean sameFor(Crosshair2d other) {
    return other != null
        && frameOfReferenceUid != null
        && !frameOfReferenceUid.isBlank()
        && Objects.equals(frameOfReferenceUid, other.frameOfReferenceUid);
  }
}
