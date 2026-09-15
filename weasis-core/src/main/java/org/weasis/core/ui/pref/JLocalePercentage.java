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

import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JComboBox;

/** Minimum translation coverage shown in File &gt; Preferences &gt; Language. Default is 30 %. */
public class JLocalePercentage extends JComboBox<String> {

  public static final String PROP_MIN_PERCENT = "weasis.i18n.min.percent";
  public static final int DEFAULT_PERCENT = 30;
  public static final double DEFAULT_RATIO = 0.30;
  public static final String[] LABELS = {"0 %", "30 %", "50 %", "100 %"};

  public JLocalePercentage() {
    super(LABELS);
    setSelectedItem(labelForPercent(thresholdPercentFromSystem()));
  }

  public static int thresholdPercentFromSystem() {
    String raw = System.getProperty(PROP_MIN_PERCENT, Integer.toString(DEFAULT_PERCENT));
    if (raw == null || raw.isBlank()) {
      return DEFAULT_PERCENT;
    }
    String digits = raw.replaceAll("[^0-9]", "");
    if (digits.isEmpty()) {
      return DEFAULT_PERCENT;
    }
    try {
      int value = Integer.parseInt(digits);
      if (value < 0) {
        return 0;
      }
      if (value > 100) {
        return 100;
      }
      return value;
    } catch (NumberFormatException e) {
      return DEFAULT_PERCENT;
    }
  }

  public static double thresholdRatioFromSystem() {
    return thresholdPercentFromSystem() / 100.0;
  }

  public static String labelForPercent(int percent) {
    return percent + " %";
  }

  public static double parseLabel(String label) {
    if (label == null || label.isBlank()) {
      return DEFAULT_RATIO;
    }
    String digits = label.replaceAll("[^0-9]", "");
    if (digits.isEmpty()) {
      return DEFAULT_RATIO;
    }
    try {
      return Integer.parseInt(digits) / 100.0;
    } catch (NumberFormatException e) {
      return DEFAULT_RATIO;
    }
  }

  public double getSelectedRatio() {
    Object sel = getSelectedItem();
    return parseLabel(sel == null ? null : sel.toString());
  }

  public int getSelectedPercent() {
    return (int) Math.round(getSelectedRatio() * 100.0);
  }

  public NumberFormat getPercentFormat() {
    return NumberFormat.getPercentInstance(Locale.getDefault());
  }
}
