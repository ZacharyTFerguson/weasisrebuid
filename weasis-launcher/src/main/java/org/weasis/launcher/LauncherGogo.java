/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.launcher;

import java.util.Hashtable;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;

/**
 * Registers {@code weasis:info} / {@code weasis:ui} on the system bundle so Gogo works even before
 * SCR processes {@code org.weasis.core.api.command}.
 */
final class LauncherGogo {

  private final Framework framework;

  LauncherGogo(Framework framework) {
    this.framework = framework;
  }

  static void register(Framework framework) {
    BundleContext context = framework.getBundleContext();
    Hashtable<String, Object> props = new Hashtable<>();
    props.put("osgi.command.scope", "weasis");
    props.put("osgi.command.function", new String[] {"info", "ui"});
    context.registerService(LauncherGogo.class, new LauncherGogo(framework), props);
  }

  public String info() {
    return helpInfo();
  }

  public String info(String arg) {
    if (arg == null) {
      return helpInfo();
    }
    return switch (arg) {
      case "-v", "--version" -> System.getProperty("weasis.version", "4.7.3");
      case "-a", "--all" -> specifications();
      default -> helpInfo();
    };
  }

  public String ui() {
    return helpUi();
  }

  public String ui(String arg) throws BundleException {
    if (arg == null) {
      return helpUi();
    }
    return switch (arg) {
      case "-q", "--quit" -> {
        framework.stop();
        yield "stopping";
      }
      case "-v", "--visible" -> "visible";
      case "-m", "--minimized" -> "minimized";
      default -> helpUi();
    };
  }

  private static String specifications() {
    return """
        Weasis %s
        OSGi Apache Felix %s
        Java %s (%s)
        gosh.port %s
        """
        .formatted(
            System.getProperty("weasis.version", "4.7.3"),
            System.getProperty("felix.framework.version", "7.0.5"),
            System.getProperty("java.version"),
            System.getProperty("java.vendor"),
            System.getProperty("gosh.port", "17179"))
        .trim();
  }

  private static String helpInfo() {
    return """
        Show information about Weasis
        Usage: weasis:info (-v | -a)
          -v --version    show version
          -a --all        show weasis specifications
          -? --help       show help""";
  }

  private static String helpUi() {
    return """
        Manage user interface
        Usage: weasis:ui (-q | -v | -m)
          -q --quit        shutdown Weasis
          -v --visible     set window on top
          -m --minimized   minimize the window""";
  }
}
