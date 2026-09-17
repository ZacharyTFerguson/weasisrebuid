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

import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import org.weasis.core.ui.util.PrintOptions;

/**
 * Viewer context menu (shortcut Q). Items are the documented left-mouse tokens plus reset/print.
 */
public class ContextMenuHandler {

  private JPopupMenu lastMenu;

  public JPopupMenu lastMenu() {
    return lastMenu;
  }

  public JPopupMenu build(DefaultView2d<?> view) {
    JPopupMenu menu = new JPopupMenu("Viewer");
    for (String action : ViewerToolBar.ACTIONS) {
      menu.add(
          new AbstractAction(action) {
            @Override
            public void actionPerformed(ActionEvent e) {
              if (view != null) {
                view.getMouseActions().setLeft(action);
              }
            }
          });
    }
    menu.addSeparator();
    menu.add(
        new AbstractAction("reset") {
          @Override
          public void actionPerformed(ActionEvent e) {
            if (view != null) {
              view.resetView("-a");
            }
          }
        });
    menu.add(
        new AbstractAction("print") {
          @Override
          public void actionPerformed(ActionEvent e) {
            if (view != null) {
              view.requestPrint(new PrintOptions());
            }
          }
        });
    lastMenu = menu;
    return menu;
  }

  public void show(DefaultView2d<?> view, int x, int y) {
    JPopupMenu menu = build(view);
    if (view != null) {
      view.setComponentPopupMenu(menu);
      if (view.isShowing() && !GraphicsEnvironment.isHeadless()) {
        menu.show(view, x, y);
      }
    }
  }
}
