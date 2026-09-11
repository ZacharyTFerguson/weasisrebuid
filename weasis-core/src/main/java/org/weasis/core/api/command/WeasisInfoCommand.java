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

import org.osgi.service.component.annotations.Component;

/**
 * Gogo {@code weasis:info}. Usage: {@code weasis:info (-v | -a)}.
 *
 * <p>Same verb as {@code $weasis:info} at launch (without {@code $} in the console).
 */
@Component(
    immediate = true,
    service = WeasisInfoCommand.class,
    property = {"osgi.command.scope=weasis", "osgi.command.function=info"})
public class WeasisInfoCommand {

  public String info() {
    return help();
  }

  public String info(String arg) {
    if (arg == null) {
      return help();
    }
    return switch (arg) {
      case "-v", "--version" -> version();
      case "-a", "--all" -> specifications();
      case "-?", "--help" -> help();
      default -> help();
    };
  }

  public static String version() {
    return System.getProperty("weasis.version", "4.7.3");
  }

  public static String specifications() {
    return """
        Weasis %s
        OSGi Apache Felix %s
        Java %s (%s)
        gosh.port %s
        """
        .formatted(
            version(),
            System.getProperty("felix.framework.version", "7.0.5"),
            System.getProperty("java.version"),
            System.getProperty("java.vendor"),
            System.getProperty("gosh.port", "17179"))
        .trim();
  }

  static String help() {
    return """
        Show information about Weasis
        Usage: weasis:info (-v | -a)
          -v --version    show version
          -a --all        show weasis specifications
          -? --help       show help""";
  }
}
