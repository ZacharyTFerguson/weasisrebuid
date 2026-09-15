/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.gui.util;

import javax.swing.ButtonGroup;
import javax.swing.JToggleButton;

public class JToggleButtonGroup<T> {
  private final ButtonGroup group = new ButtonGroup();
  private final JToggleButton[] buttons;

  public JToggleButtonGroup(T[] items) {
    this.buttons = new JToggleButton[items == null ? 0 : items.length];
    if (items != null) {
      for (int i = 0; i < items.length; i++) {
        buttons[i] = new JToggleButton(String.valueOf(items[i]));
        group.add(buttons[i]);
      }
    }
  }

  public JToggleButton[] getButtons() {
    return buttons;
  }

  public ButtonGroup getGroup() {
    return group;
  }
}
