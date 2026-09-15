/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.explorer;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

/** {@code weasis.acquire.meta.{global,series,image}.{display,edit,required}} sets. */
public final class AcquireMeta {

  public enum Scope {
    GLOBAL,
    SERIES,
    IMAGE
  }

  public enum SetKind {
    DISPLAY,
    EDIT,
    REQUIRED
  }

  private AcquireMeta() {}

  public static String key(Scope scope, SetKind kind) {
    return "weasis.acquire.meta."
        + scope.name().toLowerCase(Locale.ROOT)
        + "."
        + kind.name().toLowerCase(Locale.ROOT);
  }

  public static List<String> tokens(Properties prefs, Scope scope, SetKind kind) {
    String raw = prefs == null ? null : prefs.getProperty(key(scope, kind), "");
    if (raw == null || raw.isBlank()) {
      return List.of();
    }
    return Arrays.stream(raw.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();
  }

  public static boolean required(Properties prefs, Scope scope, String tag) {
    return tokens(prefs, scope, SetKind.REQUIRED).contains(tag);
  }
}
