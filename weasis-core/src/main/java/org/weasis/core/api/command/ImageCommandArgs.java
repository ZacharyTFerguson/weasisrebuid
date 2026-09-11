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

import java.util.List;
import java.util.Locale;

/**
 * {@code $image:get} {@code -f}/{@code -u}; {@code $image:close} {@code -a}/{@code -g}/{@code -s}.
 */
public final class ImageCommandArgs {

  public enum GetMode {
    FILE,
    URL,
    HELP
  }

  public enum CloseMode {
    ALL,
    GROUP,
    SERIES,
    HELP
  }

  private ImageCommandArgs() {}

  public static GetMode parseGet(String... args) {
    List<String> tokens = strip(args, "image:get");
    for (String t : tokens) {
      if ("-f".equals(t)) {
        return GetMode.FILE;
      }
      if ("-u".equals(t)) {
        return GetMode.URL;
      }
    }
    return GetMode.HELP;
  }

  public static String parseGetValue(String... args) {
    List<String> tokens = strip(args, "image:get");
    for (int i = 0; i < tokens.size(); i++) {
      if (("-f".equals(tokens.get(i)) || "-u".equals(tokens.get(i))) && i + 1 < tokens.size()) {
        return tokens.get(i + 1);
      }
    }
    return null;
  }

  public static CloseMode parseClose(String... args) {
    List<String> tokens = strip(args, "image:close");
    for (String t : tokens) {
      if ("-a".equals(t)) {
        return CloseMode.ALL;
      }
      if ("-g".equals(t)) {
        return CloseMode.GROUP;
      }
      if ("-s".equals(t)) {
        return CloseMode.SERIES;
      }
    }
    return CloseMode.HELP;
  }

  static List<String> strip(String[] args, String verb) {
    List<String> tokens = CommandTokens.of(args);
    if (!tokens.isEmpty()) {
      String first = tokens.getFirst();
      if (first.startsWith("$")) {
        first = first.substring(1);
      }
      if (first.toLowerCase(Locale.ROOT).startsWith(verb)) {
        return tokens.subList(1, tokens.size());
      }
    }
    return tokens;
  }
}
