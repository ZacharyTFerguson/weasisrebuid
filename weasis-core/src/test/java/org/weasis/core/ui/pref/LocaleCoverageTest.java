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

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.weasis.core.Messages;
import org.weasis.core.api.util.LocalUtil;

class LocaleCoverageTest {

  @AfterEach
  void restoreLang() {
    System.setProperty("locale.lang.code", "en");
    System.clearProperty(JLocalePercentage.PROP_MIN_PERCENT);
  }

  @Test
  void thirtyPercentThresholdHidesItalianAndKeepsFrench() {
    assertEquals(0.4, LocalUtil.coverage(Messages.BUNDLE_NAME, Locale.FRENCH), 1e-9);
    assertEquals(0.2, LocalUtil.coverage(Messages.BUNDLE_NAME, Locale.ITALIAN), 1e-9);
    assertTrue(LocalUtil.meetsThreshold(0.4, JLocalePercentage.DEFAULT_RATIO));
    assertFalse(LocalUtil.meetsThreshold(0.2, JLocalePercentage.DEFAULT_RATIO));
    List<Locale> listed =
        LocalUtil.listedLanguages(Messages.BUNDLE_NAME, JLocalePercentage.DEFAULT_RATIO);
    assertTrue(listed.contains(Locale.ENGLISH));
    assertTrue(listed.contains(Locale.FRENCH));
    assertFalse(listed.contains(Locale.ITALIAN));
    assertFalse(listed.contains(Locale.JAPANESE));
  }

  @Test
  void zeroPercentThresholdListsItalian() {
    List<Locale> listed = LocalUtil.listedLanguages(Messages.BUNDLE_NAME, 0.0);
    assertTrue(listed.contains(Locale.ITALIAN));
  }

  @Test
  void languageComboUsesThirtyPercentDefault() {
    JLocaleLanguage combo = new JLocaleLanguage();
    assertTrue(containsLang(combo, "en"));
    assertTrue(containsLang(combo, "fr"));
    assertFalse(containsLang(combo, "it"));
  }

  @Test
  void messagesHonorLocaleLangCode() {
    System.setProperty("locale.lang.code", "fr");
    assertEquals("Annuler", Messages.getString("cancel"));
    System.setProperty("locale.lang.code", "en");
    assertEquals("Cancel", Messages.getString("cancel"));
  }

  @Test
  void languageSettingPersistsPrefKeys() {
    LanguageSetting page = new LanguageSetting();
    page.getPercentageCombo().setSelectedItem("30 %");
    page.closeAdditionalWindow();
    assertEquals("30", System.getProperty(JLocalePercentage.PROP_MIN_PERCENT));
    assertFalse(System.getProperty("locale.lang.code", "").isBlank());
  }

  @Test
  void coverageCountsOnlyLocaleKeys() {
    Set<String> base =
        new LinkedHashSet<>(List.of("a", "b", "c", "d", "e", "f", "g", "h", "i", "j"));
    Set<String> loc = Set.of("a", "b", "c", "d");
    assertEquals(0.4, LocalUtil.coverage(base, loc), 1e-9);
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
