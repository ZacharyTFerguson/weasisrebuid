/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model;

import java.util.List;

/** One SOP in a KO/PR referenced series; empty frames means the whole instance. */
public class ReferencedImage {

  private final String sopInstanceUid;
  private final List<Integer> frames;

  public ReferencedImage(String sopInstanceUid) {
    this(sopInstanceUid, List.of());
  }

  public ReferencedImage(String sopInstanceUid, List<Integer> frames) {
    this.sopInstanceUid = sopInstanceUid == null ? "" : sopInstanceUid;
    this.frames = frames == null ? List.of() : List.copyOf(frames);
  }

  public String sopInstanceUid() {
    return sopInstanceUid;
  }

  public List<Integer> frames() {
    return frames;
  }

  public boolean matches(String sop, int frame) {
    if (!sameSop(sop)) {
      return false;
    }
    return frames.isEmpty() || frames.contains(frame);
  }

  boolean sameSop(String sop) {
    return sopInstanceUid.equals(sop == null ? "" : sop);
  }
}
