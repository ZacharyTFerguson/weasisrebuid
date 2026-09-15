/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

/**
 * Locale helpers. WP-14: File &gt; Preferences lists languages with ≥ 30 % translation coverage.
 */
public final class LocalUtil {

  public static final List<Locale> CANDIDATE_LOCALES =
      List.of(
          Locale.ENGLISH,
          Locale.FRENCH,
          Locale.GERMAN,
          Locale.ITALIAN,
          Locale.JAPANESE,
          Locale.SIMPLIFIED_CHINESE,
          Locale.forLanguageTag("es"),
          Locale.forLanguageTag("pt"),
          Locale.forLanguageTag("nl"),
          Locale.forLanguageTag("ru"));

  private LocalUtil() {}

  public static Locale textLocale() {
    String lang = System.getProperty("locale.lang.code", "en");
    if (lang == null || lang.isBlank() || "system".equalsIgnoreCase(lang)) {
      return Locale.getDefault();
    }
    return Locale.forLanguageTag(lang.replace('_', '-'));
  }

  public static double coverage(Set<String> baseKeys, Set<String> localeKeys) {
    if (baseKeys == null || baseKeys.isEmpty()) {
      return 0.0;
    }
    int hit = 0;
    for (String key : baseKeys) {
      if (localeKeys != null && localeKeys.contains(key)) {
        hit++;
      }
    }
    return hit / (double) baseKeys.size();
  }

  public static double coverage(String bundleName, Locale locale) {
    if (isEnglish(locale)) {
      return 1.0;
    }
    Set<String> base = propertyKeys(bundleName, Locale.ROOT);
    Set<String> loc = propertyKeys(bundleName, locale);
    return coverage(base, loc);
  }

  public static boolean meetsThreshold(double coverage, double threshold) {
    return coverage + 1.0e-9 >= threshold;
  }

  public static boolean isEnglish(Locale locale) {
    if (locale == null || locale.getLanguage().isEmpty()) {
      return true;
    }
    return "en".equalsIgnoreCase(locale.getLanguage());
  }

  public static Set<String> propertyKeys(String bundleName, Locale locale) {
    LinkedHashSet<String> keys = new LinkedHashSet<>();
    String resource = resourcePath(bundleName, locale);
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    if (cl == null) {
      cl = LocalUtil.class.getClassLoader();
    }
    try (InputStream in = cl.getResourceAsStream(resource)) {
      if (in == null) {
        return keys;
      }
      Properties props = new Properties();
      props.load(new InputStreamReader(in, StandardCharsets.UTF_8));
      for (String name : props.stringPropertyNames()) {
        keys.add(name);
      }
    } catch (IOException e) {
      return keys;
    }
    return keys;
  }

  public static String resourcePath(String bundleName, Locale locale) {
    String base = bundleName.replace('.', '/');
    if (locale == null || locale.getLanguage().isEmpty() || isEnglish(locale)) {
      return base + ".properties";
    }
    StringBuilder path = new StringBuilder(base);
    path.append('_').append(locale.getLanguage().toLowerCase(Locale.ROOT));
    if (!locale.getCountry().isEmpty()) {
      String countryPath = path + "_" + locale.getCountry() + ".properties";
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      if (cl == null) {
        cl = LocalUtil.class.getClassLoader();
      }
      if (cl.getResource(countryPath) != null) {
        return countryPath;
      }
    }
    path.append(".properties");
    return path.toString();
  }

  public static List<Locale> listedLanguages(String bundleName, double minCoverage) {
    double threshold = Double.isNaN(minCoverage) ? 0.30 : minCoverage;
    List<Locale> out = new ArrayList<>();
    LinkedHashSet<String> seen = new LinkedHashSet<>();
    for (Locale locale : CANDIDATE_LOCALES) {
      if (!isEnglish(locale) && !meetsThreshold(coverage(bundleName, locale), threshold)) {
        continue;
      }
      String tag = isEnglish(locale) ? "en" : locale.getLanguage().toLowerCase(Locale.ROOT);
      if (!seen.add(tag)) {
        continue;
      }
      out.add(isEnglish(locale) ? Locale.ENGLISH : locale);
    }
    return List.copyOf(out);
  }
}
