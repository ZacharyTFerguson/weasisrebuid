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

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Launch helpers: {@code weasis://?commands} (v3.6.0+) and {@code $weasis:config} ({@code cdb},
 * {@code arg}, {@code pro}, {@code auth}, {@code wcfg}).
 */
public final class Utils {

  public static final String WEASIS_SCHEME = "weasis";

  private Utils() {}

  public static boolean isWeasisUri(String raw) {
    if (raw == null) {
      return false;
    }
    String s = raw.trim().toLowerCase(Locale.ROOT);
    return s.startsWith("weasis:") || s.startsWith("weasis://");
  }

  public static String decodeWeasisUri(String raw) {
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

  public static List<String> commands(String raw) {
    String decoded = isWeasisUri(raw) ? decodeWeasisUri(raw) : (raw == null ? "" : raw.trim());
    if (decoded.isBlank()) {
      return List.of();
    }
    String s = decoded.trim();
    if (!s.contains("$")) {
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

  public static boolean isConfigCommand(String command) {
    if (command == null) {
      return false;
    }
    String s = command.trim();
    if (s.startsWith("$")) {
      s = s.substring(1);
    }
    return s.startsWith("weasis:config");
  }

  public static LaunchRequest parseLaunch(String[] args) {
    Map<String, String> properties = new LinkedHashMap<>();
    List<String> commands = new ArrayList<>();
    if (args == null) {
      return new LaunchRequest(properties, commands);
    }
    for (String arg : args) {
      if (arg == null || arg.isBlank()) {
        continue;
      }
      List<String> cmds;
      if (isWeasisUri(arg)) {
        cmds = commands(arg);
      } else if (arg.trim().startsWith("$") || arg.contains("weasis:config")) {
        cmds = commands(arg.startsWith("$") ? arg : "$" + arg.trim());
      } else {
        continue;
      }
      for (String cmd : cmds) {
        if (isConfigCommand(cmd)) {
          parseConfig(cmd, properties, commands);
        } else {
          commands.add(cmd);
        }
      }
    }
    return new LaunchRequest(properties, commands);
  }

  static void parseConfig(String command, Map<String, String> properties, List<String> commands) {
    String body = command.trim();
    if (body.startsWith("$")) {
      body = body.substring(1);
    }
    if (body.startsWith("weasis:config")) {
      body = body.substring("weasis:config".length()).trim();
    }
    for (String token : tokenize(body)) {
      if (token.startsWith("pro=")) {
        String kv = token.substring("pro=".length()).trim();
        int sp = kv.indexOf(' ');
        if (sp > 0) {
          properties.put(kv.substring(0, sp), kv.substring(sp + 1).trim());
        } else if (!kv.isEmpty()) {
          properties.put(kv, "");
        }
      } else if (token.startsWith("arg=")) {
        String arg = token.substring("arg=".length()).trim();
        if (!arg.isEmpty()) {
          commands.add(arg.startsWith("$") ? arg : "$" + arg);
        }
      } else if (token.startsWith("cdb=")) {
        properties.put("weasis.cdb", token.substring("cdb=".length()));
      } else if ("cdb".equals(token)) {
        properties.put("weasis.cdb", "");
      } else if (token.startsWith("auth=")) {
        properties.put("weasis.auth", token.substring("auth=".length()));
      } else if (token.startsWith("wcfg=")) {
        properties.put("weasis.wcfg", token.substring("wcfg=".length()));
      }
    }
  }

  static List<String> tokenize(String s) {
    List<String> out = new ArrayList<>();
    if (s == null || s.isBlank()) {
      return out;
    }
    StringBuilder cur = new StringBuilder();
    boolean inQuote = false;
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c == '"') {
        inQuote = !inQuote;
        continue;
      }
      if (!inQuote && Character.isWhitespace(c)) {
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

  public record LaunchRequest(Map<String, String> properties, List<String> commands) {

    public LaunchRequest {
      properties = properties == null ? Map.of() : Map.copyOf(properties);
      commands = commands == null ? List.of() : List.copyOf(commands);
    }

    public void applyProperties() {
      for (Map.Entry<String, String> entry : properties.entrySet()) {
        if (entry.getKey() == null || entry.getKey().isBlank()) {
          continue;
        }
        if (System.getProperty(entry.getKey()) == null) {
          System.setProperty(entry.getKey(), entry.getValue() == null ? "" : entry.getValue());
        }
      }
    }
  }
}
