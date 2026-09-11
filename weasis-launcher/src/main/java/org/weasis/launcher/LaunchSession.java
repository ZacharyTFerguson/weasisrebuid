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

import java.util.ArrayList;
import java.util.List;
import org.weasis.core.api.command.WeasisConfig;
import org.weasis.core.api.command.WeasisUri;

/** Parses {@code weasis://} and {@code $weasis:config} from process args before Felix starts. */
public final class LaunchSession {

  private final List<WeasisConfig> configs;
  private final List<String> commands;

  public LaunchSession(List<WeasisConfig> configs, List<String> commands) {
    this.configs = configs == null ? List.of() : List.copyOf(configs);
    this.commands = commands == null ? List.of() : List.copyOf(commands);
  }

  public List<WeasisConfig> configs() {
    return configs;
  }

  public List<String> commands() {
    return commands;
  }

  public static LaunchSession fromArgs(String[] args) {
    List<WeasisConfig> configs = new ArrayList<>();
    List<String> commands = new ArrayList<>();
    if (args == null) {
      return new LaunchSession(configs, commands);
    }
    for (String arg : args) {
      if (arg == null || arg.isBlank()) {
        continue;
      }
      if (WeasisUri.isWeasisUri(arg)) {
        for (String cmd : WeasisUri.commands(arg)) {
          addCommand(cmd, configs, commands);
        }
      } else if (arg.trim().startsWith("$")
          || arg.contains("weasis:config")
          || arg.contains("dicom:")) {
        for (String cmd :
            WeasisUri.commands(arg.startsWith("$") || arg.contains("://") ? arg : "$" + arg)) {
          addCommand(cmd, configs, commands);
        }
      }
    }
    return new LaunchSession(configs, commands);
  }

  public void applyConfigs() {
    for (WeasisConfig c : configs) {
      c.applyLaunch();
    }
  }

  static void addCommand(String cmd, List<WeasisConfig> configs, List<String> commands) {
    if (WeasisConfig.isConfigCommand(cmd)) {
      configs.add(WeasisConfig.parse(cmd));
    } else {
      commands.add(cmd);
    }
  }
}
