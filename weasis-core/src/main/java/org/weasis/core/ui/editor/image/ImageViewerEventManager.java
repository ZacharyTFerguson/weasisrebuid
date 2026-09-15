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

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/** Mouse → pan / zoom / W/L / scroll / crosshair. Zoom is centered on the view, not the cursor. */
public class ImageViewerEventManager {

  private final DefaultView2d<?> view;
  private int lastX;
  private int lastY;

  public ImageViewerEventManager(DefaultView2d<?> view) {
    this.view = view;
  }

  public DefaultView2d<?> getView() {
    return view;
  }

  public void mousePressed(MouseEvent e) {
    lastX = e.getX();
    lastY = e.getY();
  }

  public void mouseDragged(MouseEvent e) {
    int dx = e.getX() - lastX;
    int dy = e.getY() - lastY;
    lastX = e.getX();
    lastY = e.getY();
    String action = buttonAction(e);
    apply(action, dx, dy);
  }

  public void mouseReleased(MouseEvent e) {
    lastX = e.getX();
    lastY = e.getY();
  }

  public void mouseWheelMoved(MouseWheelEvent e) {
    String action = MouseActions.normalize(view.getMouseActions().getWheel());
    if (MouseActions.ZOOM.equals(action)) {
      view.increaseZoom(-e.getWheelRotation());
    } else if (MouseActions.isScroll(action)) {
      view.setFrameIndex(view.getFrameIndex() + e.getWheelRotation());
    }
  }

  public void apply(String action, int dx, int dy) {
    String a = MouseActions.normalize(action);
    if (MouseActions.isScroll(a)) {
      view.setFrameIndex(view.getFrameIndex() + (dy > 0 ? 1 : -1));
      return;
    }
    switch (a) {
      case MouseActions.PAN -> view.setPan(view.getPanX() + dx, view.getPanY() + dy);
      case MouseActions.ZOOM -> view.increaseZoom(dy < 0 ? 1 : dy > 0 ? -1 : 0);
      case MouseActions.ROTATION -> view.setRotation(view.getRotation() + dx);
      case MouseActions.WINLEVEL -> applyWindowLevel(dx, dy);
      case MouseActions.CROSSHAIR,
          MouseActions.DRAW,
          MouseActions.MEASURE,
          MouseActions.CONTEXT_MENU,
          MouseActions.NONE -> {
        // draw / measure / menu handled by graphics tools (WP-5)
      }
      default -> {
        // unknown
      }
    }
  }

  protected void applyWindowLevel(int dx, int dy) {
    // DICOM View2d overrides
  }

  String buttonAction(MouseEvent e) {
    if ((e.getModifiersEx() & MouseEvent.BUTTON2_DOWN_MASK) != 0) {
      return view.getMouseActions().getMiddle();
    }
    if ((e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) != 0) {
      return view.getMouseActions().getRight();
    }
    return view.getMouseActions().getLeft();
  }
}
