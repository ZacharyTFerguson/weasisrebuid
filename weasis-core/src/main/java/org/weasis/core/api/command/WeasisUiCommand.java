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

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/** Gogo {@code weasis:ui}. WP-0 implements {@code -q} shutdown. */
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
      case "-v", "--visible", "-m", "--minimized" ->
          "UI chrome is a stub in WP-0 (window may already be visible)";
      case "-?", "--help" -> help();
      default -> help();
    };
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
