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

import javax.swing.Icon;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;

public class DropDownButton extends JToggleButton {
  private JPopupMenu menu;

  public DropDownButton(String actionCommand, Icon icon) {
    super(new DropButtonIcon(icon));
    setActionCommand(actionCommand);
    addActionListener(e -> showPopup());
  }

  public void setMenu(JPopupMenu menu) {
    this.menu = menu;
  }

  public JPopupMenu getMenu() {
    return menu;
  }

  protected void showPopup() {
    if (menu != null) {
      menu.show(this, 0, getHeight());
    }
  }
}
