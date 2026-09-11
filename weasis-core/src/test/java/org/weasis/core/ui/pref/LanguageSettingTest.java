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

import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.i18n.LocaleCoverage;
import org.weasis.core.api.service.WProperties;

class LanguageSettingTest {

  @Test
  void listsOnlyCoverageFilteredLocalesAndPersistsIndependentFormat() {
    Properties catalog = new Properties();
    catalog.setProperty("en", "100");
    catalog.setProperty("fr", "80");
    catalog.setProperty("ja", "10");
    LocaleCoverage coverage = LocaleCoverage.fromProperties(catalog);
    WProperties prefs = new WProperties();
    LanguageSetting page = new LanguageSetting(prefs, coverage);
    assertTrue(page.listedLanguageCodes().contains("en"));
    assertTrue(page.listedLanguageCodes().contains("fr"));
    assertFalse(page.listedLanguageCodes().contains("ja"));
    assertEquals("en", page.selectedLanguage());
    assertEquals("system", page.selectedFormat());
    page.selectLanguage("fr");
    page.selectFormat("system");
    page.closeAdditionalWindow();
    assertEquals("fr", prefs.getProperty(LocaleCoverage.LANG_CODE));
    assertEquals("system", prefs.getProperty(LocaleCoverage.FORMAT_CODE));
    page.selectFormat("fr");
    page.closeAdditionalWindow();
    assertEquals("fr", prefs.getProperty(LocaleCoverage.LANG_CODE));
    assertEquals("fr", prefs.getProperty(LocaleCoverage.FORMAT_CODE));
  }

  @Test
  void resetRestoresEnAndSystem() {
    Properties catalog = new Properties();
    catalog.setProperty("en", "100");
    catalog.setProperty("de", "40");
    WProperties prefs = new WProperties();
    LanguageSetting page = new LanguageSetting(prefs, LocaleCoverage.fromProperties(catalog));
    page.selectLanguage("de");
    page.selectFormat("de");
    page.closeAdditionalWindow();
    page.resetToDefaultValues();
    assertEquals("en", page.selectedLanguage());
    assertEquals("system", page.selectedFormat());
  }
}
