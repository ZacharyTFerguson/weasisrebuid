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
import javax.swing.JLabel;
import org.weasis.core.ui.editor.image.DefaultView2d;
import org.weasis.core.ui.util.WtoolBar;

/**
 * Reset chrome for {@code dcmview2d:reset} tokens: {@code -a} / winLevel / zoom / pan / rotation.
 */
public class ResetTools extends WtoolBar {

  public static final String NAME = "Reset";
  public static final String STATE = "reset-state";

  private DefaultView2d<?> view;
  private final JButton all = button(org.weasis.core.ui.editor.image.ResetTools.ALL);
  private final JButton winLevel = button(org.weasis.core.ui.editor.image.ResetTools.WINLEVEL);
  private final JButton zoom = button(org.weasis.core.ui.editor.image.ResetTools.ZOOM);
  private final JButton pan = button(org.weasis.core.ui.editor.image.ResetTools.PAN);
  private final JButton rotation = button(org.weasis.core.ui.editor.image.ResetTools.ROTATION);
  private final JLabel state = new JLabel("none");

  public ResetTools() {
    super(NAME, 20);
    setName("reset");
    add(all);
    add(winLevel);
    add(zoom);
    add(pan);
    add(rotation);
    state.setName(STATE);
    add(state);
  }

  public void bind(DefaultView2d<?> view) {
    this.view = view;
  }

  public DefaultView2d<?> boundView() {
    return view;
  }

  public JButton allButton() {
    return all;
  }

  public JButton winLevelButton() {
    return winLevel;
  }

  public JButton zoomButton() {
    return zoom;
  }

  public JButton panButton() {
    return pan;
  }

  public JButton rotationButton() {
    return rotation;
  }

  public JLabel stateLabel() {
    return state;
  }

  public String stateText() {
    return state.getText();
  }

  public void apply(org.weasis.core.ui.editor.image.ResetTools tool) {
    apply(view, tool);
  }

  public void apply(DefaultView2d<?> view, org.weasis.core.ui.editor.image.ResetTools tool) {
    if (view == null || tool == null) {
      return;
    }
    String token = token(tool);
    view.resetView(token);
    state.setText(token);
  }

  static String token(org.weasis.core.ui.editor.image.ResetTools tool) {
    if (tool == org.weasis.core.ui.editor.image.ResetTools.WINLEVEL) {
      return "winLevel";
    }
    if (tool == org.weasis.core.ui.editor.image.ResetTools.ZOOM) {
      return "zoom";
    }
    if (tool == org.weasis.core.ui.editor.image.ResetTools.PAN) {
      return "pan";
    }
    if (tool == org.weasis.core.ui.editor.image.ResetTools.ROTATION) {
      return "rotation";
    }
    return "-a";
  }

  static String buttonName(org.weasis.core.ui.editor.image.ResetTools tool) {
    String token = token(tool);
    return "-a".equals(token) ? "reset-all" : "reset-" + token;
  }

  private JButton button(org.weasis.core.ui.editor.image.ResetTools tool) {
    String name = buttonName(tool);
    JButton button =
        new JButton(
            new AbstractAction(name) {
              @Override
              public void actionPerformed(ActionEvent e) {
                apply(tool);
              }
            });
    button.setName(name);
    button.setToolTipText(token(tool));
    return button;
  }
}
