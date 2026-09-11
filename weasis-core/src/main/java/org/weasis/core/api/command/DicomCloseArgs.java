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
 * {@code $dicom:close}: {@code -a} all, {@code -p} patient, {@code -y} study, {@code -s} series.
 */
public final class DicomCloseArgs {

  public enum Mode {
    ALL,
    PATIENT,
    STUDY,
    SERIES,
    HELP
  }

  private final Mode mode;
  private final String value;

  public DicomCloseArgs(Mode mode, String value) {
    this.mode = mode == null ? Mode.HELP : mode;
    this.value = value;
  }

  public Mode mode() {
    return mode;
  }

  public String value() {
    return value;
  }

  public static DicomCloseArgs parse(String... args) {
    List<String> tokens = CommandTokens.of(args);
    if (!tokens.isEmpty()) {
      String first = tokens.getFirst();
      if (first.startsWith("$")) {
        first = first.substring(1);
      }
      if (first.toLowerCase(Locale.ROOT).startsWith("dicom:close")) {
        tokens = tokens.subList(1, tokens.size());
      }
    }
    Mode mode = Mode.HELP;
    String value = null;
    for (int i = 0; i < tokens.size(); i++) {
      String t = tokens.get(i);
      String next = i + 1 < tokens.size() ? tokens.get(i + 1) : null;
      switch (t) {
        case "-a" -> mode = Mode.ALL;
        case "-p" -> {
          mode = Mode.PATIENT;
          value = next;
        }
        case "-y" -> {
          mode = Mode.STUDY;
          value = next;
        }
        case "-s" -> {
          mode = Mode.SERIES;
          value = next;
        }
        default -> {
          // skip
        }
      }
    }
    return new DicomCloseArgs(mode, value);
  }
}
