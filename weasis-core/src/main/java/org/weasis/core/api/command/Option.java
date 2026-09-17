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
import java.util.Objects;

/**
 * POSIX flags from a compact spec such as {@code l(local)w(wado)r(remote)}. Long GNU options
 * ({@code --url}) are accepted even when not in the spec.
 */
public final class Option {

  private final Map<String, Flag> flags;
  private final Map<String, List<String>> values = new LinkedHashMap<>();
  private final List<String> remaining = new ArrayList<>();
  private boolean parsed;

  private Option(Map<String, Flag> flags) {
    this.flags = flags;
  }

  public static Option compile(String spec) {
    return new Option(parseSpec(spec));
  }

  public Option parse(String... args) {
    values.clear();
    remaining.clear();
    parsed = true;
    if (args == null) {
      return this;
    }
    for (int i = 0; i < args.length; i++) {
      String a = args[i];
      if (a == null || a.isBlank() || "--".equals(a)) {
        if ("--".equals(a)) {
          for (int j = i + 1; j < args.length; j++) {
            remaining.add(args[j]);
          }
          break;
        }
        continue;
      }
      if (a.startsWith("--")) {
        String body = a.substring(2);
        String name;
        String inline = null;
        int eq = body.indexOf('=');
        if (eq > 0) {
          name = body.substring(0, eq);
          inline = body.substring(eq + 1);
        } else {
          name = body;
        }
        Flag flag = find(name);
        if (inline != null) {
          add(flag != null ? flag.canonical : name, inline);
        } else if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
          add(flag != null ? flag.canonical : name, args[++i]);
        } else {
          add(flag != null ? flag.canonical : name, "");
        }
      } else if (a.startsWith("-") && a.length() > 1 && !a.startsWith("--")) {
        String letters = a.substring(1);
        for (int c = 0; c < letters.length(); c++) {
          String letter = String.valueOf(letters.charAt(c));
          Flag flag = find(letter);
          String key = flag != null ? flag.canonical : letter;
          boolean last = c == letters.length() - 1;
          if (last && i + 1 < args.length && !args[i + 1].startsWith("-")) {
            add(key, args[++i]);
          } else {
            add(key, "");
          }
        }
      } else {
        remaining.add(a);
      }
    }
    return this;
  }

  public boolean isSet(String name) {
    ensureParsed();
    return values.containsKey(canonical(name));
  }

  public String get(String name) {
    List<String> list = values(name);
    if (list.isEmpty()) {
      return null;
    }
    return list.getLast();
  }

  public List<String> values(String name) {
    ensureParsed();
    List<String> list = values.get(canonical(name));
    return list == null ? List.of() : List.copyOf(list);
  }

  public List<String> args() {
    ensureParsed();
    return List.copyOf(remaining);
  }

  private void add(String key, String value) {
    values.computeIfAbsent(key, k -> new ArrayList<>()).add(value == null ? "" : value);
  }

  private String canonical(String name) {
    Flag flag = find(name);
    return flag != null ? flag.canonical : name;
  }

  private Flag find(String name) {
    if (name == null) {
      return null;
    }
    Flag direct = flags.get(name);
    if (direct != null) {
      return direct;
    }
    return flags.get(name.toLowerCase(Locale.ROOT));
  }

  private void ensureParsed() {
    if (!parsed) {
      parse();
    }
  }

  private static Map<String, Flag> parseSpec(String spec) {
    Map<String, Flag> map = new LinkedHashMap<>();
    if (spec == null || spec.isBlank()) {
      return map;
    }
    int i = 0;
    while (i < spec.length()) {
      char ch = spec.charAt(i);
      if (!Character.isLetterOrDigit(ch)) {
        i++;
        continue;
      }
      String letter = String.valueOf(ch);
      i++;
      String longName = letter;
      if (i < spec.length() && spec.charAt(i) == '(') {
        int end = spec.indexOf(')', i);
        if (end > i) {
          longName = spec.substring(i + 1, end);
          i = end + 1;
        }
      }
      Flag flag = new Flag(letter, longName);
      map.put(letter, flag);
      map.put(longName, flag);
      map.put(longName.toLowerCase(Locale.ROOT), flag);
    }
    return map;
  }

  private record Flag(String canonical, String longName) {
    Flag {
      Objects.requireNonNull(canonical);
    }
  }
}
