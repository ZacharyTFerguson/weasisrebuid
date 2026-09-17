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
import java.util.Locale;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.weasis.core.Messages;
import org.weasis.core.api.gui.util.AbstractItemDialogPage;

public class LanguageSetting extends AbstractItemDialogPage {

  private final JLocaleLanguage languages;
  private final JLocalePercentage percentage;

  public LanguageSetting() {
    super("Language", 110);
    this.percentage = new JLocalePercentage();
    this.languages = new JLocaleLanguage();
    languages.setName("lang-list");
    percentage.setName("lang-percent");
    JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
    form.add(new JLabel(Messages.getString("language.title")));
    form.add(languages);
    form.add(new JLabel(Messages.getString("language.percent")));
    form.add(percentage);
    add(form, BorderLayout.CENTER);
    percentage.addActionListener(e -> languages.rebuild(percentage.getSelectedRatio()));
  }

  public JLocaleLanguage getLanguageCombo() {
    return languages;
  }

  public JLocalePercentage getPercentageCombo() {
    return percentage;
  }

  @Override
  public void closeAdditionalWindow() {
    Locale loc = languages.getSelectedLocale();
    String tag = loc == null ? "en" : loc.toLanguageTag();
    System.setProperty("locale.lang.code", tag);
    System.setProperty(
        JLocalePercentage.PROP_MIN_PERCENT, Integer.toString(percentage.getSelectedPercent()));
  }

  @Override
  public void resetToDefaultValues() {
    System.setProperty("locale.lang.code", "en");
    System.setProperty(
        JLocalePercentage.PROP_MIN_PERCENT, Integer.toString(JLocalePercentage.DEFAULT_PERCENT));
    percentage.setSelectedItem(
        JLocalePercentage.labelForPercent(JLocalePercentage.DEFAULT_PERCENT));
    languages.rebuild(JLocalePercentage.DEFAULT_RATIO);
  }
}
