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

import javax.swing.JButton;
import org.weasis.core.ui.util.WtoolBar;

/** Viewer screenshot toolbar. Opens {@link ScreenshotDialog}. */
public class ScreenshotToolBar extends WtoolBar {

  public static final String NAME = "Screenshot";

  private final ScreenshotDialog dialog = new ScreenshotDialog();
  private final JButton button = new JButton("Screenshot");
  private DefaultView2d<?> view;

  public ScreenshotToolBar() {
    super(NAME, 25);
    button.setName("screenshot");
    button.addActionListener(e -> showDialog());
    add(button);
  }

  public void bind(DefaultView2d<?> view) {
    this.view = view;
    dialog.bind(view);
  }

  public DefaultView2d<?> boundView() {
    return view;
  }

  public ScreenshotDialog dialog() {
    return dialog;
  }

  public JButton button() {
    return button;
  }

  public void showDialog() {
    dialog.bind(view);
    dialog.setVisible(true);
  }
}
