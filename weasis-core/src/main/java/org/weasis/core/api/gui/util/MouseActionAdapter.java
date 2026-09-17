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

import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public abstract class MouseActionAdapter extends MouseAdapter {
  protected int buttonMask = InputEvent.BUTTON1_DOWN_MASK;

  public void setButtonMaskEx(int buttonMask) {
    this.buttonMask = buttonMask;
  }

  public int getButtonMaskEx() {
    return buttonMask;
  }

  public abstract void mouseDragged(MouseEvent e);

  @Override
  public void mouseWheelMoved(MouseWheelEvent e) {}
}
