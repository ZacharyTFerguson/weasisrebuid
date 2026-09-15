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
 * Locale helpers. WP-14: File &gt; Preferences lists languages with ≥ 30 % translation coverage of
 * documented {@code Messages} bundles.
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

  /** Fixture {@code Messages.java} types: package + {@code .messages}. */
  public static final List<String> DOCUMENTED_BUNDLES =
      List.of(
          "org.weasis.acquire.messages",
          "org.weasis.acquire.explorer.messages",
          "org.weasis.base.explorer.messages",
          "org.weasis.base.ui.messages",
          "org.weasis.base.viewer2d.messages",
          "org.weasis.core.messages",
          "org.weasis.dicom.viewer3d.messages",
          "org.weasis.dicom.au.messages",
          "org.weasis.dicom.codec.messages",
          "org.weasis.dicom.explorer.messages",
          "org.weasis.dicom.isowriter.messages",
          "org.weasis.dicom.qr.messages",
          "org.weasis.dicom.rt.messages",
          "org.weasis.dicom.send.messages",
          "org.weasis.dicom.sr.messages",
          "org.weasis.dicom.viewer2d.messages",
          "org.weasis.dicom.wave.messages",
          "org.weasis.launcher.messages");

  public record CoverageHits(int hits, int total) {
    public double ratio() {
      if (total <= 0) {
        return 0.0;
      }
      return hits / (double) total;
    }
  }

  private LocalUtil() {}

  public static List<String> documentedBundles() {
    return DOCUMENTED_BUNDLES;
  }

  public static Locale textLocale() {
    String lang = System.getProperty("locale.lang.code", "en");
    if (lang == null || lang.isBlank() || "system".equalsIgnoreCase(lang)) {
      return Locale.getDefault();
    }
    return Locale.forLanguageTag(lang.replace('_', '-'));
  }

  public static int translatedCount(Set<String> baseKeys, Set<String> localeKeys) {
    int hit = 0;
    if (baseKeys == null) {
      return 0;
    }
    for (String key : baseKeys) {
      if (localeKeys != null && localeKeys.contains(key)) {
        hit++;
      }
    }
    return hit;
  }

  public static double coverage(Set<String> baseKeys, Set<String> localeKeys) {
    if (baseKeys == null || baseKeys.isEmpty()) {
      return 0.0;
    }
    return translatedCount(baseKeys, localeKeys) / (double) baseKeys.size();
  }

  public static double coverage(String bundleName, Locale locale) {
    return coverage(List.of(bundleName), locale);
  }

  public static double coverage(List<String> bundleNames, Locale locale) {
    if (isEnglish(locale)) {
      return 1.0;
    }
    return hitsAndTotal(bundleNames, locale).ratio();
  }

  public static CoverageHits hitsAndTotal(List<String> bundleNames, Locale locale) {
    int hits = 0;
    int total = 0;
    if (bundleNames == null) {
      return new CoverageHits(0, 0);
    }
    for (String name : bundleNames) {
      CoverageHits one = bundleHits(name, locale);
      hits += one.hits();
      total += one.total();
    }
    return new CoverageHits(hits, total);
  }

  public static CoverageHits bundleHits(String bundleName, Locale locale) {
    if (bundleName == null || bundleName.isBlank()) {
      return new CoverageHits(0, 0);
    }
    Set<String> base = propertyKeys(bundleName, Locale.ROOT);
    if (base.isEmpty()) {
      return new CoverageHits(0, 0);
    }
    return new CoverageHits(translatedCount(base, propertyKeys(bundleName, locale)), base.size());
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
    try (InputStream in =
        resourceClassLoader().getResourceAsStream(resourcePath(bundleName, locale))) {
      return loadKeys(in, keys);
    } catch (IOException e) {
      return keys;
    }
  }

  static Set<String> loadKeys(InputStream in, LinkedHashSet<String> keys) throws IOException {
    if (in == null) {
      return keys;
    }
    Properties props = new Properties();
    props.load(new InputStreamReader(in, StandardCharsets.UTF_8));
    keys.addAll(props.stringPropertyNames());
    return keys;
  }

  static ClassLoader resourceClassLoader() {
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    if (cl == null) {
      return LocalUtil.class.getClassLoader();
    }
    return cl;
  }

  public static String resourcePath(String bundleName, Locale locale) {
    String base = bundleName.replace('.', '/');
    if (useRootProperties(locale)) {
      return base + ".properties";
    }
    return localeProperties(base, locale);
  }

  static boolean useRootProperties(Locale locale) {
    return locale == null || locale.getLanguage().isEmpty() || isEnglish(locale);
  }

  static String localeProperties(String base, Locale locale) {
    String langPath = base + "_" + locale.getLanguage().toLowerCase(Locale.ROOT);
    String country = countryResource(langPath, locale);
    if (country != null) {
      return country;
    }
    return langPath + ".properties";
  }

  static String countryResource(String langPath, Locale locale) {
    if (locale.getCountry().isEmpty()) {
      return null;
    }
    String countryPath = langPath + "_" + locale.getCountry() + ".properties";
    return resourceExists(countryPath) ? countryPath : null;
  }

  static boolean resourceExists(String resource) {
    return resourceClassLoader().getResource(resource) != null;
  }

  public static List<Locale> listedLanguages(String bundleName, double minCoverage) {
    return listedLanguages(List.of(bundleName == null ? "" : bundleName), minCoverage);
  }

  public static List<Locale> listedLanguages(List<String> bundleNames, double minCoverage) {
    return filterCandidates(bundleNames, threshold(minCoverage));
  }

  static double threshold(double minCoverage) {
    return Double.isNaN(minCoverage) ? 0.30 : minCoverage;
  }

  static List<Locale> filterCandidates(List<String> bundleNames, double threshold) {
    List<Locale> out = new ArrayList<>();
    LinkedHashSet<String> seen = new LinkedHashSet<>();
    for (Locale locale : CANDIDATE_LOCALES) {
      addIfListed(out, seen, bundleNames, locale, threshold);
    }
    return List.copyOf(out);
  }

  static void addIfListed(
      List<Locale> out,
      LinkedHashSet<String> seen,
      List<String> bundleNames,
      Locale locale,
      double threshold) {
    if (!includeLocale(bundleNames, locale, threshold)) {
      return;
    }
    if (!seen.add(languageTag(locale))) {
      return;
    }
    out.add(isEnglish(locale) ? Locale.ENGLISH : locale);
  }

  static boolean includeLocale(List<String> bundleNames, Locale locale, double threshold) {
    if (isEnglish(locale)) {
      return true;
    }
    return meetsThreshold(coverage(bundleNames, locale), threshold);
  }

  static String languageTag(Locale locale) {
    if (isEnglish(locale)) {
      return "en";
    }
    return locale.getLanguage().toLowerCase(Locale.ROOT);
  }
}
