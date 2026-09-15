/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.main;

import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Explorer thumbnail mouse/key map: Ctrl+click toggle, Shift+click range, Ctrl+A all, Enter open.
 */
public class ThumbnailMouseAndKeyAdapter extends MouseAdapter {

  private final SeriesSelectionModel model;

  public ThumbnailMouseAndKeyAdapter(SeriesSelectionModel model) {
    this.model = model == null ? new SeriesSelectionModel() : model;
  }

  public SeriesSelectionModel getModel() {
    return model;
  }

  public void pressed(int index, boolean ctrl, boolean shift) {
    model.click(index, ctrl, shift);
  }

  public void pressed(int index, MouseEvent e) {
    if (e == null) {
      pressed(index, false, false);
      return;
    }
    int mods = e.getModifiersEx();
    pressed(
        index, (mods & InputEvent.CTRL_DOWN_MASK) != 0, (mods & InputEvent.SHIFT_DOWN_MASK) != 0);
  }

  public boolean keyPressed(KeyEvent e) {
    if (e == null) {
      return false;
    }
    boolean ctrl = (e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0;
    if (ctrl && e.getKeyCode() == KeyEvent.VK_A) {
      model.selectAll();
      return true;
    }
    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
      model.enter();
      return true;
    }
    return false;
  }

  public KeyAdapter keyAdapter() {
    return new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        ThumbnailMouseAndKeyAdapter.this.keyPressed(e);
      }
    };
  }
}
