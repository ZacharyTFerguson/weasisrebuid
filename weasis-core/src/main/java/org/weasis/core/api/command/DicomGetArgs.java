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
 * {@code $dicom:get} flags: {@code -l} local, {@code -w} manifest, {@code -r} URI, {@code -z},
 * {@code -p}, {@code -i}.
 */
public final class DicomGetArgs {

  public enum Mode {
    LOCAL,
    REMOTE,
    MANIFEST,
    ZIP,
    PORTABLE,
    IWADO,
    HELP
  }

  private final Mode mode;
  private final String value;

  public DicomGetArgs(Mode mode, String value) {
    this.mode = mode == null ? Mode.HELP : mode;
    this.value = value;
  }

  public Mode mode() {
    return mode;
  }

  public String value() {
    return value;
  }

  public static DicomGetArgs parse(String... args) {
    List<String> tokens = CommandTokens.of(args);
    if (!tokens.isEmpty()) {
      String first = tokens.getFirst();
      if (first.startsWith("$")) {
        first = first.substring(1);
      }
      if (first.toLowerCase(Locale.ROOT).startsWith("dicom:get")) {
        tokens = tokens.subList(1, tokens.size());
      }
    }
    Mode mode = Mode.HELP;
    String value = null;
    for (int i = 0; i < tokens.size(); i++) {
      String t = tokens.get(i);
      switch (t) {
        case "-l" -> {
          mode = Mode.LOCAL;
          value = i + 1 < tokens.size() ? tokens.get(i + 1) : null;
        }
        case "-r" -> {
          mode = Mode.REMOTE;
          value = i + 1 < tokens.size() ? tokens.get(i + 1) : null;
        }
        case "-w" -> {
          mode = Mode.MANIFEST;
          value = i + 1 < tokens.size() ? tokens.get(i + 1) : null;
        }
        case "-z" -> {
          mode = Mode.ZIP;
          value = i + 1 < tokens.size() ? tokens.get(i + 1) : null;
        }
        case "-p" -> {
          mode = Mode.PORTABLE;
          value = i + 1 < tokens.size() ? tokens.get(i + 1) : null;
        }
        case "-i", "--iwado" -> {
          mode = Mode.IWADO;
          value = i + 1 < tokens.size() ? tokens.get(i + 1) : null;
        }
        default -> {
          // skip
        }
      }
    }
    return new DicomGetArgs(mode, value);
  }
}
