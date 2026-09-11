/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.command;

import javax.swing.JFrame;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.weasis.core.api.gui.util.GuiExecutor;
import org.weasis.core.api.service.UICore;

/** Gogo {@code weasis:ui}. {@code -q} shutdown; {@code -v}/{@code -m} the aggregator window. */
@Component(
    immediate = true,
    service = WeasisUiCommand.class,
    property = {"osgi.command.scope=weasis", "osgi.command.function=ui"})
public class WeasisUiCommand {

  private BundleContext bundleContext;

  @Activate
  public void activate(BundleContext bundleContext) {
    this.bundleContext = bundleContext;
  }

  public String ui() {
    return help();
  }

  public String ui(String arg) throws BundleException {
    if (arg == null) {
      return help();
    }
    return switch (arg) {
      case "-q", "--quit" -> {
        bundleContext.getBundle(0).stop();
        yield "stopping";
      }
      case "-v", "--visible" -> showWindow(false);
      case "-m", "--minimized" -> showWindow(true);
      case "-?", "--help" -> help();
      default -> help();
    };
  }

  static String showWindow(boolean minimize) {
    JFrame window = UICore.getInstance().getApplicationWindow();
    if (window == null) {
      return GraphicsEnvironmentNote.HEADLESS;
    }
    GuiExecutor.execute(
        () -> {
          if (minimize) {
            window.setExtendedState(window.getExtendedState() | JFrame.ICONIFIED);
          } else {
            window.setExtendedState(window.getExtendedState() & ~JFrame.ICONIFIED);
            window.setVisible(true);
            window.toFront();
          }
        });
    return minimize ? "minimized" : "visible";
  }

  static String help() {
    return """
        Manage user interface
        Usage: weasis:ui (-q | -v | -m)
          -q --quit        shutdown Weasis
          -v --visible     set window on top
          -m --minimized   minimize the window
          -? --help       show help""";
  }
}
