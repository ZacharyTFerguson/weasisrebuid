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

import javax.swing.JMenu;

public abstract class DynamicMenu extends JMenu {
  protected DynamicMenu(String title) {
    super(title);
    addMenuListener(
        new javax.swing.event.MenuListener() {
          @Override
          public void menuSelected(javax.swing.event.MenuEvent e) {
            popupMenuWillBecomeVisible();
          }

          @Override
          public void menuDeselected(javax.swing.event.MenuEvent e) {}

          @Override
          public void menuCanceled(javax.swing.event.MenuEvent e) {}
        });
  }

  public abstract void popupMenuWillBecomeVisible();
}
