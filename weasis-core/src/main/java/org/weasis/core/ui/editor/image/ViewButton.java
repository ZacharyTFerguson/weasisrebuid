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

import javax.swing.Icon;
import javax.swing.JToggleButton;

/** Overlay control painted on a {@link DefaultView2d} (cine, synch, …). */
public class ViewButton extends JToggleButton {

  private int overlayX;
  private int overlayY;
  private boolean show = true;

  public ViewButton() {
    this("", new AreaIcon());
  }

  public ViewButton(String text) {
    this(text, new AreaIcon());
  }

  public ViewButton(String text, Icon icon) {
    super(text == null ? "" : text, icon == null ? new AreaIcon() : icon);
    setName(getText().isBlank() ? "view-button" : getText());
    setFocusable(false);
  }

  public void setOverlay(int x, int y) {
    this.overlayX = x;
    this.overlayY = y;
  }

  public int overlayX() {
    return overlayX;
  }

  public int overlayY() {
    return overlayY;
  }

  public boolean isShown() {
    return show;
  }

  public void setShow(boolean show) {
    this.show = show;
  }

  public boolean hit(int x, int y) {
    return show && inRange(x, overlayX, iconWidth()) && inRange(y, overlayY, iconHeight());
  }

  static boolean inRange(int value, int start, int size) {
    return value >= start && value < start + size;
  }

  int iconWidth() {
    return getIcon() == null ? AreaIcon.SIZE : getIcon().getIconWidth();
  }

  int iconHeight() {
    return getIcon() == null ? AreaIcon.SIZE : getIcon().getIconHeight();
  }

  public void apply(DefaultView2d<?> view) {}
}
