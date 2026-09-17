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

/** Scroll/cine sequence: map a vertical drag to one frame step. */
public class SequenceHandler {

  public int step(int dy) {
    if (dy > 0) {
      return 1;
    }
    if (dy < 0) {
      return -1;
    }
    return 0;
  }

  public int apply(DefaultView2d<?> view, int dy) {
    if (view == null) {
      return 0;
    }
    view.setFrameIndex(view.getFrameIndex() + step(dy));
    return view.getFrameIndex();
  }
}
