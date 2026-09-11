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

import java.util.ArrayList;
import java.util.List;

/** Quote-aware tokenizer for {@code $dicom:*} / {@code $weasis:config} argument lists. */
public final class CommandTokens {

  private CommandTokens() {}

  public static List<String> split(String line) {
    List<String> out = new ArrayList<>();
    if (line == null || line.isBlank()) {
      return out;
    }
    StringBuilder cur = new StringBuilder();
    boolean inQuote = false;
    char quote = 0;
    for (int i = 0; i < line.length(); i++) {
      char c = line.charAt(i);
      if (inQuote) {
        if (c == quote) {
          inQuote = false;
        } else {
          cur.append(c);
        }
      } else if (c == '"' || c == '\'') {
        inQuote = true;
        quote = c;
      } else if (Character.isWhitespace(c)) {
        if (!cur.isEmpty()) {
          out.add(cur.toString());
          cur.setLength(0);
        }
      } else {
        cur.append(c);
      }
    }
    if (!cur.isEmpty()) {
      out.add(cur.toString());
    }
    return out;
  }

  public static List<String> of(String... args) {
    if (args == null || args.length == 0) {
      return List.of();
    }
    if (args.length == 1 && args[0] != null && args[0].contains(" ")) {
      return split(args[0]);
    }
    List<String> out = new ArrayList<>();
    for (String a : args) {
      if (a != null && !a.isBlank()) {
        out.add(a);
      }
    }
    return out;
  }
}
