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
import javax.swing.JPopupMenu;

public class GroupRadioMenu<T> implements GroupPopup {
  private final JPopupMenu popup = new JPopupMenu();
  private final ButtonGroup group = new ButtonGroup();

  public JPopupMenu getPopup() {
    return popup;
  }

  public ButtonGroup getGroup() {
    return group;
  }

  public void addItem(RadioMenuItem item) {
    if (item != null) {
      group.add(item);
      popup.add(item);
    }
  }
}
