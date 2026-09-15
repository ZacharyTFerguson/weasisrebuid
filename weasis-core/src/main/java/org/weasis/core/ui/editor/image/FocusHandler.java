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

/** Which canvas is the focused 2D view (layout selection). */
public class FocusHandler {

  private DefaultView2d<?> focused;

  public void focus(DefaultView2d<?> view) {
    this.focused = view;
  }

  public DefaultView2d<?> focused() {
    return focused;
  }

  public boolean isFocused(DefaultView2d<?> view) {
    return view != null && view == focused;
  }
}
