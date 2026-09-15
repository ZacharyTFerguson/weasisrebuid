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

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import org.weasis.core.ui.editor.image.DefaultView2d;
import org.weasis.core.ui.util.WtoolBar;

/**
 * Reset chrome for {@code dcmview2d:reset} tokens: {@code -a} / winLevel / zoom / pan / rotation.
 */
public class ResetTools extends WtoolBar {

  public static final String NAME = "Reset";

  private DefaultView2d<?> view;

  public ResetTools() {
    super(NAME, 20);
    for (org.weasis.core.ui.editor.image.ResetTools tool :
        org.weasis.core.ui.editor.image.ResetTools.values()) {
      add(button(tool));
    }
  }

  public void bind(DefaultView2d<?> view) {
    this.view = view;
  }

  public DefaultView2d<?> boundView() {
    return view;
  }

  public void apply(org.weasis.core.ui.editor.image.ResetTools tool) {
    apply(view, tool);
  }

  public void apply(DefaultView2d<?> view, org.weasis.core.ui.editor.image.ResetTools tool) {
    if (view == null || tool == null) {
      return;
    }
    switch (tool) {
      case WINLEVEL -> view.resetView("winLevel");
      case ZOOM -> view.resetView("zoom");
      case PAN -> view.resetView("pan");
      case ROTATION -> view.resetView("rotation");
      default -> view.resetView("-a");
    }
  }

  private JButton button(org.weasis.core.ui.editor.image.ResetTools tool) {
    JButton button =
        new JButton(
            new AbstractAction(tool.name()) {
              @Override
              public void actionPerformed(ActionEvent e) {
                apply(tool);
              }
            });
    button.setName(tool.name());
    return button;
  }
}
