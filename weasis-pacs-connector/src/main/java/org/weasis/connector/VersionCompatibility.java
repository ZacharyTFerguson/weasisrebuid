/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.connector;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ViewerHub version-compatibility: release → minimal + i18n. User-Agent qualifier; unknown uses
 * {@code default}.
 */
public final class VersionCompatibility {

  public static final String DEFAULT_QUALIFIER = "default";
  private static final Pattern UA_VERSION = Pattern.compile("Weasis[/ ]([0-9]+(?:\\.[0-9]+)*)");

  public record Mapping(String releaseVersion, String minimalVersion, String i18nVersion) {}

  private final Map<String, Mapping> byRelease;
  private final String defaultQualifier;

  public VersionCompatibility(Map<String, Mapping> byRelease, String defaultQualifier) {
    this.byRelease = byRelease == null ? Map.of() : Map.copyOf(byRelease);
    this.defaultQualifier = defaultQualifier == null ? DEFAULT_QUALIFIER : defaultQualifier;
  }

  public static VersionCompatibility pin473() {
    Map<String, Mapping> map = new LinkedHashMap<>();
    map.put("4.7.3", new Mapping("4.7.3", "4.7.0", "4.7.3"));
    return new VersionCompatibility(map, DEFAULT_QUALIFIER);
  }

  public Mapping mapping(String releaseVersion) {
    return byRelease.get(releaseVersion);
  }

  public String qualifierFromUserAgent(String userAgent) {
    if (userAgent == null || userAgent.isBlank()) {
      return defaultQualifier;
    }
    Matcher matcher = UA_VERSION.matcher(userAgent);
    if (matcher.find()) {
      return matcher.group(1);
    }
    return defaultQualifier;
  }

  public String defaultQualifier() {
    return defaultQualifier;
  }
}
