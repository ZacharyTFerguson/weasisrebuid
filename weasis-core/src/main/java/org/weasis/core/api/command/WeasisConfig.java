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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * {@code $weasis:config} is launch-only. Kinds: {@code cdb}, {@code arg}, {@code pro="key value"},
 * {@code auth}, {@code wcfg}. {@code cdb} without a value uses the natively installed build.
 */
public final class WeasisConfig {

  public static final Set<String> KINDS = Set.of("cdb", "arg", "pro", "auth", "wcfg");
  public static final String CDB_PROPERTY = "weasis.cdb";
  public static final String AUTH_PROPERTY = "weasis.auth";
  public static final String WCFG_PROPERTY = "weasis.wcfg";

  private final boolean cdbPresent;
  private final String cdb;
  private final List<String> args;
  private final Map<String, String> pro;
  private final String auth;
  private final String wcfg;

  public WeasisConfig(
      boolean cdbPresent,
      String cdb,
      List<String> args,
      Map<String, String> pro,
      String auth,
      String wcfg) {
    this.cdbPresent = cdbPresent;
    this.cdb = cdb;
    this.args = args == null ? List.of() : List.copyOf(args);
    this.pro = pro == null ? Map.of() : Map.copyOf(pro);
    this.auth = auth;
    this.wcfg = wcfg;
  }

  public boolean cdbPresent() {
    return cdbPresent;
  }

  public String cdb() {
    return cdb;
  }

  public List<String> args() {
    return args;
  }

  public Map<String, String> pro() {
    return pro;
  }

  public String auth() {
    return auth;
  }

  public String wcfg() {
    return wcfg;
  }

  public static boolean isConfigCommand(String command) {
    if (command == null) {
      return false;
    }
    String s = command.trim();
    if (s.startsWith("$")) {
      s = s.substring(1);
    }
    return s.toLowerCase(Locale.ROOT).startsWith("weasis:config");
  }

  public static WeasisConfig parse(String command) {
    String s = command == null ? "" : command.trim();
    if (s.startsWith("$")) {
      s = s.substring(1);
    }
    if (s.toLowerCase(Locale.ROOT).startsWith("weasis:config")) {
      s = s.substring("weasis:config".length()).trim();
    }
    List<String> tokens = CommandTokens.split(s);
    boolean cdbPresent = false;
    String cdb = null;
    List<String> args = new ArrayList<>();
    Map<String, String> pro = new LinkedHashMap<>();
    String auth = null;
    String wcfg = null;
    for (String t : tokens) {
      int eq = t.indexOf('=');
      String kind = eq < 0 ? t : t.substring(0, eq);
      String val = eq < 0 ? "" : t.substring(eq + 1);
      switch (kind) {
        case "cdb" -> {
          cdbPresent = true;
          cdb = val.isBlank() ? null : val;
        }
        case "arg" -> args.add(val);
        case "pro" -> {
          String kv = val.trim();
          int sp = kv.indexOf(' ');
          if (sp < 0) {
            pro.put(kv, "");
          } else {
            pro.put(kv.substring(0, sp), kv.substring(sp + 1).trim());
          }
        }
        case "auth" -> auth = val;
        case "wcfg" -> wcfg = val;
        default -> {
          // ignore unknown (not a config kind)
        }
      }
    }
    return new WeasisConfig(cdbPresent, cdb, args, pro, auth, wcfg);
  }

  public void applyLaunch() {
    if (cdbPresent) {
      if (cdb == null || cdb.isBlank()) {
        System.clearProperty(CDB_PROPERTY);
      } else {
        System.setProperty(CDB_PROPERTY, cdb);
      }
    }
    for (Map.Entry<String, String> e : pro.entrySet()) {
      if (e.getKey() != null && !e.getKey().isBlank()) {
        System.setProperty(e.getKey(), e.getValue() == null ? "" : e.getValue());
      }
    }
    if (auth != null && !auth.isBlank()) {
      System.setProperty(AUTH_PROPERTY, auth);
    }
    if (wcfg != null && !wcfg.isBlank()) {
      System.setProperty(WCFG_PROPERTY, wcfg);
    }
    for (String arg : args) {
      if (arg.startsWith("-D") && arg.contains("=")) {
        int eq = arg.indexOf('=');
        System.setProperty(arg.substring(2, eq), arg.substring(eq + 1));
      }
    }
  }
}
