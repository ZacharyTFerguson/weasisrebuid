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

/**
 * MX-14: Gogo {@code None|Stack|Tile} is {@link Mode}; Frame of Reference vs manual is {@link Kind}
 * and is never the same switch.
 */
public class SynchData {

  public enum Mode {
    NONE,
    STACK,
    TILE;

    public static Mode fromView(SynchView view) {
      if (view == SynchView.TILE) {
        return TILE;
      }
      if (view == SynchView.NONE) {
        return NONE;
      }
      return STACK;
    }

    public SynchView toView() {
      return switch (this) {
        case NONE -> SynchView.NONE;
        case TILE -> SynchView.TILE;
        case STACK -> SynchView.STACK;
      };
    }
  }

  public enum Kind {
    FRAME_OF_REFERENCE,
    MANUAL
  }

  private Mode mode = Mode.STACK;
  private Kind kind = Kind.FRAME_OF_REFERENCE;

  public Mode getMode() {
    return mode;
  }

  public void setMode(Mode mode) {
    this.mode = mode == null ? Mode.NONE : mode;
  }

  public Kind getKind() {
    return kind;
  }

  public void setKind(Kind kind) {
    this.kind = kind == null ? Kind.FRAME_OF_REFERENCE : kind;
  }

  public boolean isFrameOfReference() {
    return kind == Kind.FRAME_OF_REFERENCE;
  }

  public boolean isManual() {
    return kind == Kind.MANUAL;
  }
}
