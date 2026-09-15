/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package org.weasis.base.ui.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import org.weasis.core.api.command.WeasisUiCommand;

public class WeasisWinListener extends WindowAdapter {
  private final WeasisWin window;

  public WeasisWinListener(WeasisWin window) {
    this.window = window;
  }

  public WeasisWin getWindow() {
    return window;
  }

  @Override
  public void windowClosing(WindowEvent e) {
    try {
      new WeasisUiCommand().ui("-q");
    } catch (Exception ignored) {
      // bundle may not be started in tests
    }
  }
}
