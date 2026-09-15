/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package org.weasis.pref;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Launch {@code weasis:config} properties ({@code cdb}, {@code pro}, …). Distinct from JSON {@code
 * org.weasis.launcher.ConfigData}.
 */
public class ConfigData {
  private final Map<String, String> properties = new LinkedHashMap<>();

  public ConfigData() {}

  public ConfigData(Map<String, String> properties) {
    if (properties != null) {
      this.properties.putAll(properties);
    }
  }

  public void addProperty(String key, String value) {
    if (key != null && value != null) {
      properties.put(key, value);
    }
  }

  public String getProperty(String key) {
    return properties.get(key);
  }

  public String getProperty(String key, String def) {
    return properties.getOrDefault(key, def);
  }

  public Map<String, String> getProperties() {
    return Map.copyOf(properties);
  }

  public void applySystemProperties() {
    properties.forEach(
        (k, v) -> {
          if (System.getProperty(k) == null) {
            System.setProperty(k, v);
          }
        });
  }

  public static ConfigData fromArgs(String[] args) {
    ConfigData data = new ConfigData();
    if (args == null) {
      return data;
    }
    for (String a : args) {
      if (a == null) {
        continue;
      }
      if (a.startsWith("pro=")) {
        String rest = a.substring(4).trim();
        int sp = rest.indexOf(' ');
        if (sp > 0) {
          data.addProperty(rest.substring(0, sp), rest.substring(sp + 1).trim());
        } else {
          data.addProperty(rest, "true");
        }
      } else if (a.startsWith("cdb=")) {
        data.addProperty("weasis.codebase.url", a.substring(4));
      }
    }
    return data;
  }
}
