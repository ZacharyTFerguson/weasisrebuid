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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses Weasis {@code base.json} ({@code weasisPreferences} array). Precedence: Java system
 * property (except type AP) → JSON → defaults. {@code gosh.port} is not a pref.
 */
public final class ConfigData {

  private static final Pattern PLACEHOLDER = Pattern.compile("\\$\\{([^}]+)}");
  private static final Pattern AUTO_START = Pattern.compile("felix\\.auto\\.start\\.(\\d+)");
  private static final Pattern AUTO_INSTALL = Pattern.compile("felix\\.auto\\.install\\.(\\d+)");
  private static final ObjectMapper MAPPER = new ObjectMapper();

  private final Path source;
  private final Map<String, Preference> preferences;
  private final Map<String, String> resolved;

  private ConfigData(
      Path source, Map<String, Preference> preferences, Map<String, String> resolved) {
    this.source = source;
    this.preferences = preferences;
    this.resolved = resolved;
  }

  public static ConfigData load(Path json, Map<String, String> buildInfo) throws IOException {
    Objects.requireNonNull(json, "json");
    Map<String, String> seeds = new LinkedHashMap<>();
    seeds.put("user.home", System.getProperty("user.home"));
    String mavenRepo =
        System.getProperty(
            "maven.repo.local",
            Path.of(System.getProperty("user.home"), ".m2/repository").toString());
    seeds.put("maven.localRepository", mavenRepo);
    seeds.put("settings.localRepository", mavenRepo);
    seeds.put("maven.local.repo", mavenRepo);
    if (buildInfo != null) {
      seeds.putAll(buildInfo);
    }
    seeds.put("native.library.spec", NativeLibrary.spec());

    JsonNode root = MAPPER.readTree(Files.readString(json));
    JsonNode array = root.get("weasisPreferences");
    if (array == null || !array.isArray()) {
      throw new IllegalArgumentException(json + " is missing weasisPreferences[]");
    }

    Map<String, Preference> prefs = new LinkedHashMap<>();
    for (JsonNode node : array) {
      String code = text(node, "code");
      prefs.put(
          code,
          new Preference(
              code,
              text(node, "value"),
              text(node, "description"),
              text(node, "type"),
              text(node, "javaType"),
              text(node, "category")));
    }

    Map<String, String> raw = new LinkedHashMap<>(seeds);
    for (Preference pref : prefs.values()) {
      if (!"AP".equals(pref.type())) {
        String sys = System.getProperty(pref.code());
        if (sys != null) {
          raw.put(pref.code(), sys);
          continue;
        }
      }
      raw.put(pref.code(), pref.value());
    }

    Map<String, String> resolved = resolveAll(raw);
    return new ConfigData(json, Collections.unmodifiableMap(prefs), resolved);
  }

  public Path source() {
    return source;
  }

  public String value(String code) {
    return resolved.get(code);
  }

  public String value(String code, String fallback) {
    String v = resolved.get(code);
    return v == null || v.isBlank() ? fallback : v;
  }

  public int intValue(String code, int fallback) {
    String v = resolved.get(code);
    if (v == null || v.isBlank()) {
      return fallback;
    }
    return Integer.parseInt(v.trim());
  }

  public Map<String, String> values() {
    return Collections.unmodifiableMap(resolved);
  }

  public Map<String, Preference> preferences() {
    return preferences;
  }

  public int beginningStartLevel() {
    return intValue("org.osgi.framework.startlevel.beginning", 130);
  }

  public int bundleStartLevel() {
    return intValue("felix.startlevel.bundle", 300);
  }

  public Map<String, String> frameworkProperties() {
    Map<String, String> fw = new LinkedHashMap<>();
    for (Preference pref : preferences.values()) {
      if (pref.code().startsWith("felix.auto.")) {
        continue;
      }
      String category = pref.category();
      if ("FELIX_CONFIG".equals(category)
          || "FELIX_INSTALL".equals(category)
          || pref.code().startsWith("org.osgi.")
          || pref.code().startsWith("felix.")) {
        fw.put(pref.code(), resolved.get(pref.code()));
      }
    }
    fw.put("org.osgi.framework.startlevel.beginning", "1");
    fw.put("felix.startlevel.bundle", Integer.toString(bundleStartLevel()));
    return fw;
  }

  public List<AutoBundle> autoBundles() {
    Map<Integer, AutoBundle> byLevel = new TreeMap<>();
    for (Map.Entry<String, String> entry : resolved.entrySet()) {
      Matcher start = AUTO_START.matcher(entry.getKey());
      Matcher install = AUTO_INSTALL.matcher(entry.getKey());
      int level;
      boolean doStart;
      if (start.matches()) {
        level = Integer.parseInt(start.group(1));
        doStart = true;
      } else if (install.matches()) {
        level = Integer.parseInt(install.group(1));
        doStart = false;
      } else {
        continue;
      }
      List<Path> files = new ArrayList<>();
      for (String token : entry.getValue().trim().split("\\s+")) {
        if (token.isBlank()) {
          continue;
        }
        files.add(toPath(token));
      }
      byLevel.put(level, new AutoBundle(level, doStart, files));
    }
    return List.copyOf(byLevel.values());
  }

  public boolean containsCode(String code) {
    return preferences.containsKey(code);
  }

  private static Map<String, String> resolveAll(Map<String, String> raw) {
    Map<String, String> current = new LinkedHashMap<>(raw);
    for (int pass = 0; pass < 12; pass++) {
      boolean changed = false;
      Map<String, String> next = new LinkedHashMap<>();
      for (Map.Entry<String, String> entry : current.entrySet()) {
        String resolved = substitute(entry.getValue(), current);
        next.put(entry.getKey(), resolved);
        if (!Objects.equals(entry.getValue(), resolved)) {
          changed = true;
        }
      }
      current = next;
      if (!changed) {
        break;
      }
    }
    return current;
  }

  static String substitute(String value, Map<String, String> props) {
    if (value == null || !value.contains("${")) {
      return value;
    }
    Matcher matcher = PLACEHOLDER.matcher(value);
    StringBuilder out = new StringBuilder();
    while (matcher.find()) {
      String key = matcher.group(1);
      String replacement = props.get(key);
      if (replacement == null) {
        replacement = System.getProperty(key, "");
      }
      matcher.appendReplacement(out, Matcher.quoteReplacement(replacement));
    }
    matcher.appendTail(out);
    return out.toString();
  }

  private static Path toPath(String token) {
    String path = token;
    if (path.startsWith("file:")) {
      path = path.substring("file:".length());
    }
    if (path.startsWith("//")) {
      path = path.substring(1);
    }
    return Path.of(path);
  }

  private static String text(JsonNode node, String field) {
    JsonNode value = node.get(field);
    return value == null || value.isNull() ? "" : value.asText();
  }

  public record Preference(
      String code,
      String value,
      String description,
      String type,
      String javaType,
      String category) {}

  public record AutoBundle(int startLevel, boolean start, List<Path> files) {}
}
