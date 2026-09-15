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

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import org.weasis.core.ui.editor.image.dockable.MeasureTool;

/**
 * Graphics keys from SHORTCUTS.md: Ctrl+A/D select/deselect all, Delete remove, D/A/Y/G/B/M/N
 * tools, Esc reset.
 */
public class DrawingsKeyListeners {

  private final DefaultView2d<?> view;

  public DrawingsKeyListeners(DefaultView2d<?> view) {
    this.view = view;
  }

  public boolean keyPressed(KeyEvent e) {
    if (e == null || view == null) {
      return false;
    }
    boolean ctrl = (e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0;
    int code = e.getKeyCode();
    if (code == KeyEvent.VK_DELETE || code == KeyEvent.VK_BACK_SPACE) {
      view.deleteSelectedGraphics();
      return true;
    }
    if (ctrl && code == KeyEvent.VK_A) {
      view.selectAllGraphics();
      return true;
    }
    if (ctrl && code == KeyEvent.VK_D) {
      view.deselectAllGraphics();
      return true;
    }
    if (code == KeyEvent.VK_ESCAPE) {
      view.resetView("-a");
      return true;
    }
    if (ctrl) {
      return false;
    }
    return switch (code) {
      case KeyEvent.VK_N -> {
        view.getMouseActions().setLeft(MouseActions.NONE);
        yield true;
      }
      case KeyEvent.VK_M, KeyEvent.VK_D -> tool(MeasureTool.DISTANCE);
      case KeyEvent.VK_A -> tool(MeasureTool.ANGLE);
      case KeyEvent.VK_Y -> tool(MeasureTool.POLYLINE);
      case KeyEvent.VK_G -> {
        view.setMeasureTool(MeasureTool.RECTANGLE);
        view.getMouseActions().setLeft(MouseActions.DRAW);
        yield true;
      }
      case KeyEvent.VK_B -> tool(MeasureTool.TEXTBOX);
      default -> false;
    };
  }

  private boolean tool(String name) {
    view.setMeasureTool(name);
    view.getMouseActions().setLeft(MouseActions.MEASURE);
    return true;
  }
}
