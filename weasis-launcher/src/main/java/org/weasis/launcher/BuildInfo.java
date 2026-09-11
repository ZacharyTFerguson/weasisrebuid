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

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/** Maven-filtered versions used to expand {@code ${app.version}} and Felix coordinates. */
public final class BuildInfo {

  private final Properties properties = new Properties();

  private BuildInfo(Properties properties) {
    this.properties.putAll(properties);
  }

  public static BuildInfo load() {
    Properties props = new Properties();
    try (InputStream in = BuildInfo.class.getResourceAsStream("build.properties")) {
      if (in == null) {
        throw new IllegalStateException("build.properties missing from launcher jar");
      }
      props.load(in);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    return new BuildInfo(props);
  }

  public String get(String key) {
    return properties.getProperty(key, "");
  }

  public Map<String, String> asMap() {
    Map<String, String> map = new LinkedHashMap<>();
    for (String name : properties.stringPropertyNames()) {
      map.put(name, properties.getProperty(name));
    }
    return map;
  }
}
