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

import javax.swing.JCheckBox;
import javax.swing.JPanel;

/** Which actions participate in Tile synch. */
public class SynchOptionsCheckBoxGroup extends JPanel {

  private final JCheckBox window = new JCheckBox("Window/Level", true);
  private final JCheckBox zoom = new JCheckBox("Zoom", true);
  private final JCheckBox pan = new JCheckBox("Pan", true);
  private final JCheckBox scroll = new JCheckBox("Scroll", true);

  public SynchOptionsCheckBoxGroup() {
    add(window);
    add(zoom);
    add(pan);
    add(scroll);
  }

  public boolean isWindow() {
    return window.isSelected();
  }

  public boolean isZoom() {
    return zoom.isSelected();
  }

  public boolean isPan() {
    return pan.isSelected();
  }

  public boolean isScroll() {
    return scroll.isSelected();
  }
}
