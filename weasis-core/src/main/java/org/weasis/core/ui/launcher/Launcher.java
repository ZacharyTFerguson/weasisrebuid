/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.launcher;

import java.util.ArrayList;
import java.util.List;

public class Launcher {
  private String name = "";
  private String command = "";
  private final List<Placeholder> placeholders = new ArrayList<>();

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name == null ? "" : name;
  }

  public String getCommand() {
    return command;
  }

  public void setCommand(String command) {
    this.command = command == null ? "" : command;
  }

  public List<Placeholder> getPlaceholders() {
    return placeholders;
  }

  public String resolve() {
    String cmd = command;
    for (Placeholder p : placeholders) {
      cmd = p.apply(cmd);
    }
    return cmd;
  }
}
