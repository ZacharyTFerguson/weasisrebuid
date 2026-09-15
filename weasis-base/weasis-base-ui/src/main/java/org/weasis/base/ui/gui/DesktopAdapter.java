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

import java.awt.Desktop;
import java.awt.desktop.AboutEvent;
import java.awt.desktop.AboutHandler;
import java.awt.desktop.QuitEvent;
import java.awt.desktop.QuitHandler;
import java.awt.desktop.QuitResponse;
import org.weasis.core.api.command.WeasisUiCommand;

public class DesktopAdapter implements AboutHandler, QuitHandler {
  private final WeasisWin window;

  public DesktopAdapter(WeasisWin window) {
    this.window = window;
    if (Desktop.isDesktopSupported()) {
      Desktop desktop = Desktop.getDesktop();
      if (desktop.isSupported(Desktop.Action.APP_ABOUT)) {
        desktop.setAboutHandler(this);
      }
      if (desktop.isSupported(Desktop.Action.APP_QUIT_HANDLER)) {
        desktop.setQuitHandler(this);
      }
    }
  }

  @Override
  public void handleAbout(AboutEvent e) {
    if (window != null) {
      new WeasisAboutBox(window).setVisible(true);
    }
  }

  @Override
  public void handleQuitRequestWith(QuitEvent e, QuitResponse response) {
    try {
      new WeasisUiCommand().ui("-q");
    } catch (Exception ignored) {
      // bundle may not be started in tests
    }
    if (response != null) {
      response.performQuit();
    }
  }
}
