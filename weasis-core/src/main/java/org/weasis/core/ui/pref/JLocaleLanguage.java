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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.JComboBox;
import org.weasis.core.Messages;
import org.weasis.core.api.util.LocalUtil;

public class JLocaleLanguage extends JComboBox<JLocale> {

  private final String bundleName;

  public JLocaleLanguage() {
    this(Messages.BUNDLE_NAME, JLocalePercentage.thresholdRatioFromSystem());
  }

  public JLocaleLanguage(String bundleName, double minCoverage) {
    super();
    this.bundleName =
        bundleName == null || bundleName.isBlank() ? Messages.BUNDLE_NAME : bundleName;
    rebuild(minCoverage);
  }

  public void rebuild(double minCoverage) {
    Locale previous = getSelectedLocale();
    removeAllItems();
    List<Locale> locales = LocalUtil.listedLanguages(bundleName, minCoverage);
    List<JLocale> items = new ArrayList<>();
    for (Locale locale : locales) {
      JLocale item = new JLocale(locale);
      items.add(item);
      addItem(item);
    }
    JLocale select = new JLocale(previous);
    if (items.contains(select)) {
      setSelectedItem(select);
    } else if (!items.isEmpty()) {
      setSelectedItem(items.getFirst());
    }
  }

  public Locale getSelectedLocale() {
    Object v = getSelectedItem();
    if (v instanceof JLocale loc) {
      return loc.getLocale();
    }
    return LocalUtil.textLocale();
  }
}
