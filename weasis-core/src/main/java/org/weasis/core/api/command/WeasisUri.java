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

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * {@code weasis://} scheme: commands are URL-encoded after {@code weasis://?}. The installed app
 * consumes the scheme; enterprise browsers should allow-list {@code weasis://*}.
 */
public final class WeasisUri {

  public static final String SCHEME = "weasis";

  private WeasisUri() {}

  public static boolean isWeasisUri(String raw) {
    if (raw == null) {
      return false;
    }
    String s = raw.trim().toLowerCase(Locale.ROOT);
    return s.startsWith("weasis:") || s.startsWith("weasis://");
  }

  public static List<String> commands(String raw) {
    String decoded = decode(raw);
    if (decoded.isBlank()) {
      return List.of();
    }
    String s = decoded.trim();
    if (!s.contains("$") && s.contains(":")) {
      return List.of(s.startsWith("$") ? s : "$" + s);
    }
    String[] parts = s.split("(?=\\$)");
    List<String> cmds = new ArrayList<>();
    for (String p : parts) {
      String t = p.trim();
      if (t.isEmpty()) {
        continue;
      }
      if (!t.startsWith("$")) {
        t = "$" + t;
      }
      cmds.add(t);
    }
    return cmds;
  }

  public static String decode(String raw) {
    if (raw == null || raw.isBlank()) {
      return "";
    }
    String s = raw.trim();
    String lower = s.toLowerCase(Locale.ROOT);
    if (lower.startsWith("weasis://") || lower.startsWith("weasis:")) {
      int slash = s.indexOf("://");
      if (slash >= 0) {
        s = s.substring(slash + 3);
      } else {
        s = s.substring("weasis:".length());
        if (s.startsWith("//")) {
          s = s.substring(2);
        }
      }
      if (s.startsWith("?")) {
        s = s.substring(1);
      }
    }
    return URLDecoder.decode(s, StandardCharsets.UTF_8);
  }
}
