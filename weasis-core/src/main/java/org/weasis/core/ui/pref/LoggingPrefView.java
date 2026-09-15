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

import java.util.Locale;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import org.weasis.core.api.service.UICore;
import org.weasis.core.api.service.WProperties;

/**
 * Prefs &gt; Logging. Tokens are the documented PREFERENCES.md shipping keys (felix.log.level,
 * sling log level / stack.limit).
 */
public class LoggingPrefView extends ShellPrefPage {

  public static final String TITLE = "Logging";
  public static final String PREF_FELIX = "felix.log.level";
  public static final String PREF_SLING = "org.apache.sling.commons.log.level";
  public static final String PREF_STACK = "org.apache.sling.commons.log.stack.limit";
  public static final int DEFAULT_FELIX = 1;
  public static final String DEFAULT_SLING = "INFO";
  public static final int DEFAULT_STACK = 3;
  public static final String[] LEVELS = {"TRACE", "DEBUG", "INFO", "WARN", "ERROR"};

  private final WProperties prefs;
  private final JSpinner felixSpinner;
  private final JComboBox<String> slingCombo;
  private final JSpinner stackSpinner;

  public LoggingPrefView() {
    this(UICore.getInstance().getSystemPreferences());
  }

  public LoggingPrefView(WProperties prefs) {
    super(TITLE, 750);
    this.prefs = prefs == null ? new WProperties() : prefs;
    felixSpinner =
        new JSpinner(new SpinnerNumberModel(intPref(PREF_FELIX, DEFAULT_FELIX), 0, 4, 1));
    slingCombo = new JComboBox<>(LEVELS);
    slingCombo.setSelectedItem(slingPref());
    stackSpinner =
        new JSpinner(new SpinnerNumberModel(intPref(PREF_STACK, DEFAULT_STACK), -1, 99, 1));
    JPanel form = new JPanel();
    form.add(new JLabel("Felix log level"));
    form.add(felixSpinner);
    form.add(new JLabel("Sling log level"));
    form.add(slingCombo);
    form.add(new JLabel("Stack limit"));
    form.add(stackSpinner);
    add(form);
  }

  public int felixLevel() {
    return ((Number) felixSpinner.getValue()).intValue();
  }

  public void setFelixLevel(int level) {
    felixSpinner.setValue(level);
  }

  public String slingLevel() {
    return selectedSling();
  }

  public void setSlingLevel(String level) {
    slingCombo.setSelectedItem(normalizeSling(level));
  }

  public int stackLimit() {
    return ((Number) stackSpinner.getValue()).intValue();
  }

  public void setStackLimit(int limit) {
    stackSpinner.setValue(limit);
  }

  @Override
  public void closeAdditionalWindow() {
    prefs.putIntProperty(PREF_FELIX, felixLevel());
    prefs.setProperty(PREF_SLING, slingLevel());
    prefs.putIntProperty(PREF_STACK, stackLimit());
    System.setProperty(PREF_FELIX, Integer.toString(felixLevel()));
    System.setProperty(PREF_SLING, slingLevel());
    System.setProperty(PREF_STACK, Integer.toString(stackLimit()));
  }

  @Override
  public void resetToDefaultValues() {
    felixSpinner.setValue(DEFAULT_FELIX);
    slingCombo.setSelectedItem(DEFAULT_SLING);
    stackSpinner.setValue(DEFAULT_STACK);
  }

  int intPref(String key, int documented) {
    return prefs.getIntProperty(key, parseInt(System.getProperty(key), documented));
  }

  String slingPref() {
    return normalizeSling(
        prefs.getProperty(PREF_SLING, System.getProperty(PREF_SLING, DEFAULT_SLING)));
  }

  String selectedSling() {
    Object value = slingCombo.getSelectedItem();
    return value == null ? DEFAULT_SLING : normalizeSling(value.toString());
  }

  static String normalizeSling(String raw) {
    if (raw == null || raw.isBlank()) {
      return DEFAULT_SLING;
    }
    return matchLevel(raw.trim().toUpperCase(Locale.ROOT));
  }

  static String matchLevel(String upper) {
    for (String level : LEVELS) {
      if (level.equals(upper)) {
        return level;
      }
    }
    return DEFAULT_SLING;
  }

  static int parseInt(String raw, int documented) {
    if (raw == null || raw.isBlank()) {
      return documented;
    }
    return parseIntValue(raw.trim(), documented);
  }

  static int parseIntValue(String raw, int documented) {
    try {
      return Integer.parseInt(raw);
    } catch (NumberFormatException e) {
      return documented;
    }
  }
}
