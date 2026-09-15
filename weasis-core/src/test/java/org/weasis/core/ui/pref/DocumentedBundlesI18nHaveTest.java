/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.pref;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.util.LocalUtil;

class DocumentedBundlesI18nHaveTest {

  @AfterEach
  void restoreLang() {
    System.setProperty("locale.lang.code", "en");
    System.clearProperty(JLocalePercentage.PROP_MIN_PERCENT);
  }

  @Test
  void documentedBundlesMatchFixtureMessagesTypes() throws IOException {
    Set<String> fromFixture = fixtureBundleNames();
    assertEquals(18, fromFixture.size());
    assertEquals(fromFixture, new LinkedHashSet<>(LocalUtil.documentedBundles()));
    assertTrue(fromFixture.contains("org.weasis.core.messages"));
    assertTrue(fromFixture.contains("org.weasis.dicom.explorer.messages"));
    assertTrue(fromFixture.contains("org.weasis.dicom.viewer2d.messages"));
    assertTrue(fromFixture.contains("org.weasis.launcher.messages"));
  }

  @Test
  void listedLanguagesAreAtLeastThirtyPercentOfDocumentedBundles() {
    List<String> bundles = LocalUtil.documentedBundles();
    assertEquals(0.4, LocalUtil.coverage(bundles, Locale.FRENCH), 1e-9);
    assertEquals(0.2, LocalUtil.coverage(bundles, Locale.ITALIAN), 1e-9);
    assertTrue(LocalUtil.meetsThreshold(0.4, JLocalePercentage.DEFAULT_RATIO));
    assertFalse(LocalUtil.meetsThreshold(0.2, JLocalePercentage.DEFAULT_RATIO));
    List<Locale> listed = LocalUtil.listedLanguages(bundles, JLocalePercentage.DEFAULT_RATIO);
    assertTrue(listed.contains(Locale.ENGLISH));
    assertTrue(listed.contains(Locale.FRENCH));
    assertFalse(listed.contains(Locale.ITALIAN));
    assertFalse(listed.contains(Locale.JAPANESE));
    for (Locale locale : listed) {
      assertTrue(
          LocalUtil.meetsThreshold(
              LocalUtil.coverage(bundles, locale), JLocalePercentage.DEFAULT_RATIO),
          locale.toLanguageTag());
    }
  }

  @Test
  void languageComboUsesDocumentedBundlesThirtyPercent() {
    JLocaleLanguage combo = new JLocaleLanguage();
    assertTrue(containsLang(combo, "en"));
    assertTrue(containsLang(combo, "fr"));
    assertFalse(containsLang(combo, "it"));
    assertFalse(containsLang(combo, "ja"));
  }

  private static Set<String> fixtureBundleNames() throws IOException {
    Path root = findRepoRoot();
    Path fixture = root.resolve("docs/weasis-spec/fixtures/weasis-4.7.3-main-java.txt");
    try (Stream<String> lines = Files.lines(fixture)) {
      return lines
          .map(String::trim)
          .filter(s -> s.endsWith("/Messages.java"))
          .map(DocumentedBundlesI18nHaveTest::bundleNameFromMessagesPath)
          .collect(Collectors.toCollection(LinkedHashSet::new));
    }
  }

  static String bundleNameFromMessagesPath(String path) {
    int marker = path.indexOf("/src/main/java/");
    String rel = path.substring(marker + "/src/main/java/".length());
    String pkgDir = rel.substring(0, rel.length() - "Messages.java".length());
    String pkg = pkgDir.replace('/', '.');
    if (pkg.endsWith(".")) {
      pkg = pkg.substring(0, pkg.length() - 1);
    }
    return pkg + ".messages";
  }

  private static Path findRepoRoot() {
    Path dir = Path.of("").toAbsolutePath();
    for (int i = 0; i < 8 && dir != null; i++) {
      if (Files.isRegularFile(
          dir.resolve("docs/weasis-spec/fixtures/weasis-4.7.3-main-java.txt"))) {
        return dir;
      }
      dir = dir.getParent();
    }
    fail("Could not find repo root with weasis-spec fixtures");
    return Path.of(".");
  }

  private static boolean containsLang(JLocaleLanguage combo, String lang) {
    for (int i = 0; i < combo.getItemCount(); i++) {
      JLocale item = combo.getItemAt(i);
      if (item != null && lang.equalsIgnoreCase(item.getLocale().getLanguage())) {
        return true;
      }
    }
    return false;
  }
}
