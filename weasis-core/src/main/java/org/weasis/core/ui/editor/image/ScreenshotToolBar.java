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

  public ScreenshotToolBar() {
    super(NAME, 25);
    JButton button = new JButton("Screenshot");
    button.addActionListener(e -> dialog.setVisible(true));
    add(button);
  }

  public ScreenshotDialog dialog() {
    return dialog;
  }
}
