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
import org.weasis.core.api.util.LocalUtil;

public class JLocaleLanguage extends JComboBox<JLocale> {

  private final List<String> bundleNames;

  public JLocaleLanguage() {
    this(LocalUtil.documentedBundles(), JLocalePercentage.thresholdRatioFromSystem());
  }

  public JLocaleLanguage(String bundleName, double minCoverage) {
    this(singleBundle(bundleName), minCoverage);
  }

  public JLocaleLanguage(List<String> bundleNames, double minCoverage) {
    super();
    this.bundleNames = copyBundles(bundleNames);
    rebuild(minCoverage);
  }

  public void rebuild(double minCoverage) {
    Locale previous = getSelectedLocale();
    removeAllItems();
    selectPrevious(addLocales(minCoverage), previous);
  }

  public Locale getSelectedLocale() {
    Object v = getSelectedItem();
    if (v instanceof JLocale loc) {
      return loc.getLocale();
    }
    return LocalUtil.textLocale();
  }

  List<JLocale> addLocales(double minCoverage) {
    List<JLocale> items = new ArrayList<>();
    for (Locale locale : LocalUtil.listedLanguages(bundleNames, minCoverage)) {
      JLocale item = new JLocale(locale);
      items.add(item);
      addItem(item);
    }
    return items;
  }

  void selectPrevious(List<JLocale> items, Locale previous) {
    JLocale select = new JLocale(previous);
    if (items.contains(select)) {
      setSelectedItem(select);
      return;
    }
    if (!items.isEmpty()) {
      setSelectedItem(items.getFirst());
    }
  }

  static List<String> singleBundle(String bundleName) {
    if (bundleName == null || bundleName.isBlank()) {
      return LocalUtil.documentedBundles();
    }
    return List.of(bundleName);
  }

  static List<String> copyBundles(List<String> bundleNames) {
    if (bundleNames == null || bundleNames.isEmpty()) {
      return LocalUtil.documentedBundles();
    }
    return List.copyOf(bundleNames);
  }
}
