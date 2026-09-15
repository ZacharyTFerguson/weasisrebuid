/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import org.weasis.core.ui.editor.image.ImageViewerEventManager;
import org.weasis.dicom.viewer2d.mpr.MprController;
import org.weasis.dicom.viewer2d.mpr.MprView;

/** DICOM 2D event manager (Weasis type name). Applies W/L on the bound {@link View2d}. */
public class EventManager extends ImageViewerEventManager {

  private final View2d view2d;

  public EventManager(View2d view) {
    super(view);
    this.view2d = view;
  }

  public View2d getView2d() {
    return view2d;
  }

  @Override
  public void keyPressed(KeyEvent e) {
    if (handleMprShortcut(e)) {
      return;
    }
    super.keyPressed(e);
  }

  @Override
  public void mouseWheelMoved(MouseWheelEvent e) {
    if (handleMprWheel(e)) {
      return;
    }
    super.mouseWheelMoved(e);
  }

  boolean handleMprShortcut(KeyEvent e) {
    if (!(view2d instanceof MprView mpr) || e == null) {
      return false;
    }
    MprController controller = mpr.getController();
    if (controller == null) {
      return false;
    }
    int mods = e.getModifiersEx();
    boolean alt = (mods & InputEvent.ALT_DOWN_MASK) != 0;
    boolean ctrl = (mods & InputEvent.CTRL_DOWN_MASK) != 0;
    if (!alt) {
      return false;
    }
    controller.setSelectedView(mpr);
    int code = e.getKeyCode();
    if (ctrl && code == KeyEvent.VK_B) {
      controller.cycleMipType();
      return true;
    }
    if (code == KeyEvent.VK_X) {
      if (ctrl) {
        controller.centerAll();
      } else {
        controller.centerSelected();
      }
      return true;
    }
    if (code == KeyEvent.VK_C) {
      if (ctrl) {
        controller.toggleCenterAll();
      } else {
        controller.toggleCenterSelected();
      }
      return true;
    }
    if (code == KeyEvent.VK_V) {
      if (ctrl) {
        controller.toggleCrosshairAll();
      } else {
        controller.toggleCrosshairSelected();
      }
      return true;
    }
    return false;
  }

  boolean handleMprWheel(MouseWheelEvent e) {
    if (!(view2d instanceof MprView mpr) || e == null) {
      return false;
    }
    if ((e.getModifiersEx() & InputEvent.ALT_DOWN_MASK) == 0) {
      return false;
    }
    MprController controller = mpr.getController();
    if (controller == null) {
      return false;
    }
    controller.setSelectedView(mpr);
    controller.addSelectedThickness(-e.getWheelRotation());
    return true;
  }

  @Override
  protected void applyWindowLevel(int dx, int dy) {
    view2d.setWindowLevel(view2d.getWindow() + dx, view2d.getLevel() - dy);
  }
}
