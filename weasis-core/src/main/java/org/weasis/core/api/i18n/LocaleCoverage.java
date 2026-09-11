/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import org.weasis.core.api.service.WProperties;

/**
 * Transifex coverage catalog. File &gt; Preferences &gt; General lists only locales with {@code >=
 * } {@link #MIN_PERCENT} coverage. {@code locale.lang.code} default {@code en}; {@code
 * locale.format.code} default {@code system}.
 */
public final class LocaleCoverage {

  public static final int MIN_PERCENT = 30;
  public static final String LANG_CODE = "locale.lang.code";
  public static final String FORMAT_CODE = "locale.format.code";
  public static final String DEFAULT_LANG = "en";
  public static final String DEFAULT_FORMAT = "system";
  public static final String CATALOG_RESOURCE = "i18n/transifex-coverage.properties";

  public record LocaleRow(String code, String displayName, int percent) {

    public boolean offered() {
      return percent >= MIN_PERCENT;
    }
  }

  private final Map<String, Integer> percents;

  public LocaleCoverage(Map<String, Integer> percents) {
    this.percents = percents == null ? Map.of() : new LinkedHashMap<>(percents);
  }

  public static LocaleCoverage loadDefault() {
    Properties props = new Properties();
    try (InputStream in =
        LocaleCoverage.class.getClassLoader().getResourceAsStream(CATALOG_RESOURCE)) {
      if (in == null) {
        throw new IllegalStateException("missing " + CATALOG_RESOURCE);
      }
      props.load(in);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    return fromProperties(props);
  }

  public static LocaleCoverage fromProperties(Properties props) {
    Map<String, Integer> map = new LinkedHashMap<>();
    if (props != null) {
      List<String> keys = new ArrayList<>();
      for (String name : props.stringPropertyNames()) {
        keys.add(name);
      }
      Collections.sort(keys);
      for (String key : keys) {
        map.put(key, parsePercent(props.getProperty(key)));
      }
    }
    return new LocaleCoverage(map);
  }

  public static void seed(WProperties prefs) {
    WProperties target = prefs == null ? new WProperties() : prefs;
    if (target.getProperty(LANG_CODE) == null || target.getProperty(LANG_CODE).isBlank()) {
      String sys = System.getProperty(LANG_CODE, DEFAULT_LANG);
      target.setProperty(LANG_CODE, sys == null || sys.isBlank() ? DEFAULT_LANG : sys);
    }
    if (target.getProperty(FORMAT_CODE) == null || target.getProperty(FORMAT_CODE).isBlank()) {
      String sys = System.getProperty(FORMAT_CODE, DEFAULT_FORMAT);
      target.setProperty(FORMAT_CODE, sys == null || sys.isBlank() ? DEFAULT_FORMAT : sys);
    }
  }

  static int parsePercent(String raw) {
    if (raw == null || raw.isBlank()) {
      return 0;
    }
    try {
      return Integer.parseInt(raw.trim());
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  public List<LocaleRow> all() {
    List<LocaleRow> rows = new ArrayList<>();
    for (Map.Entry<String, Integer> e : percents.entrySet()) {
      rows.add(new LocaleRow(e.getKey(), displayName(e.getKey()), e.getValue()));
    }
    return List.copyOf(rows);
  }

  public List<LocaleRow> offered() {
    List<LocaleRow> rows = new ArrayList<>();
    for (LocaleRow row : all()) {
      if (row.offered()) {
        rows.add(row);
      }
    }
    return List.copyOf(rows);
  }

  public List<LocaleRow> hidden() {
    List<LocaleRow> rows = new ArrayList<>();
    for (LocaleRow row : all()) {
      if (!row.offered()) {
        rows.add(row);
      }
    }
    return List.copyOf(rows);
  }

  public boolean isOffered(String code) {
    if (code == null) {
      return false;
    }
    Integer p = percents.get(code);
    return p != null && p >= MIN_PERCENT;
  }

  public static String displayName(String code) {
    if (code == null || code.isBlank()) {
      return "";
    }
    Locale locale = Locale.forLanguageTag(code.replace('_', '-'));
    String name = locale.getDisplayName(Locale.ENGLISH);
    return name == null || name.isBlank() ? code : name;
  }
}
