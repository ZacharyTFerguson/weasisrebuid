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

/** Synchronization event from a source view. */
public class SynchEvent {

  private final DefaultView2d<?> view;
  private final int frameIndex;

  public SynchEvent(DefaultView2d<?> view, int frameIndex) {
    this.view = view;
    this.frameIndex = frameIndex;
  }

  public DefaultView2d<?> getView() {
    return view;
  }

  public int getFrameIndex() {
    return frameIndex;
  }
}
