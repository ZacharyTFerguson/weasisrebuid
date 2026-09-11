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

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.weasis.core.api.gui.util.AbstractItemDialogPage;
import org.weasis.core.api.i18n.LocaleCoverage;
import org.weasis.core.api.service.UICore;
import org.weasis.core.api.service.WProperties;

/**
 * File &gt; Preferences &gt; General &gt; Language. Language (UI strings) and Regional format are
 * independent. Only Transifex coverage {@code >= 30%} locales are listed.
 */
public class LanguageSetting extends AbstractItemDialogPage {

  private final WProperties prefs;
  private final LocaleCoverage coverage;
  private final JComboBox<String> language = new JComboBox<>();
  private final JComboBox<String> format = new JComboBox<>();
  private final List<String> languageCodes = new ArrayList<>();

  public LanguageSetting() {
    this(UICore.getInstance().getSystemPreferences(), LocaleCoverage.loadDefault());
  }

  public LanguageSetting(WProperties prefs, LocaleCoverage coverage) {
    super("Language", 110);
    this.prefs = prefs == null ? new WProperties() : prefs;
    this.coverage = coverage == null ? LocaleCoverage.loadDefault() : coverage;
    LocaleCoverage.seed(this.prefs);
    initGui();
    load();
  }

  private void initGui() {
    languageCodes.clear();
    language.removeAllItems();
    for (LocaleCoverage.LocaleRow row : coverage.offered()) {
      languageCodes.add(row.code());
      language.addItem(row.code() + " — " + row.displayName() + " (" + row.percent() + "%)");
    }
    format.removeAllItems();
    format.addItem(LocaleCoverage.DEFAULT_FORMAT);
    for (String code : languageCodes) {
      format.addItem(code);
    }
    JPanel fields = new JPanel(new GridLayout(0, 2, 6, 4));
    fields.add(new JLabel("Language"));
    fields.add(language);
    fields.add(new JLabel("Regional format"));
    fields.add(format);
    add(fields, BorderLayout.NORTH);
  }

  void load() {
    String lang = prefs.getProperty(LocaleCoverage.LANG_CODE, LocaleCoverage.DEFAULT_LANG);
    int langIndex = languageCodes.indexOf(lang);
    if (langIndex < 0) {
      langIndex = languageCodes.indexOf(LocaleCoverage.DEFAULT_LANG);
    }
    if (langIndex >= 0) {
      language.setSelectedIndex(langIndex);
    }
    String fmt = prefs.getProperty(LocaleCoverage.FORMAT_CODE, LocaleCoverage.DEFAULT_FORMAT);
    format.setSelectedItem(fmt);
    if (format.getSelectedItem() == null) {
      format.setSelectedItem(LocaleCoverage.DEFAULT_FORMAT);
    }
  }

  public List<String> listedLanguageCodes() {
    return List.copyOf(languageCodes);
  }

  public String selectedLanguage() {
    int i = language.getSelectedIndex();
    if (i < 0 || i >= languageCodes.size()) {
      return LocaleCoverage.DEFAULT_LANG;
    }
    return languageCodes.get(i);
  }

  public String selectedFormat() {
    Object item = format.getSelectedItem();
    return item == null ? LocaleCoverage.DEFAULT_FORMAT : item.toString();
  }

  public void selectLanguage(String code) {
    int i = languageCodes.indexOf(code);
    if (i >= 0) {
      language.setSelectedIndex(i);
    }
  }

  public void selectFormat(String code) {
    format.setSelectedItem(code);
  }

  @Override
  public void closeAdditionalWindow() {
    prefs.setProperty(LocaleCoverage.LANG_CODE, selectedLanguage());
    prefs.setProperty(LocaleCoverage.FORMAT_CODE, selectedFormat());
  }

  @Override
  public void resetToDefaultValues() {
    prefs.setProperty(LocaleCoverage.LANG_CODE, LocaleCoverage.DEFAULT_LANG);
    prefs.setProperty(LocaleCoverage.FORMAT_CODE, LocaleCoverage.DEFAULT_FORMAT);
    load();
  }
}
