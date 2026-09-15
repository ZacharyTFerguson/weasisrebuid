/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.eclipse.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.editor.image;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import org.weasis.core.ui.util.WtoolBar;

/** 2D rotation chrome. Mouse action {@code rotation}; reset includes {@code rotation}. */
public class RotationToolBar extends WtoolBar {

  public static final String NAME = "Rotation";

  private double selected;

  public RotationToolBar() {
    super(NAME, 25);
    add(button("0°", 0));
    add(button("90°", 90));
    add(button("180°", 180));
    add(button("270°", 270));
  }

  public double selectedRotation() {
    return selected;
  }

  public void setSelectedRotation(double rotation) {
    this.selected = rotation;
  }

  public void apply(DefaultView2d<?> view) {
    if (view != null) {
      view.setRotation(selected);
    }
  }

  private JButton button(String label, double rotation) {
    JButton button =
        new JButton(
            new AbstractAction(label) {
              @Override
              public void actionPerformed(ActionEvent e) {
                setSelectedRotation(rotation);
              }
            });
    button.setToolTipText(label);
    return button;
  }
}
