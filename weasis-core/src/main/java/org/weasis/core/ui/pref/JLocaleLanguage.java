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

public class JLocaleLanguage extends JComboBox<JLocale> {
  public JLocaleLanguage() {
    super();
    addItem(new JLocale(Locale.ENGLISH));
    addItem(new JLocale(Locale.FRENCH));
    addItem(new JLocale(Locale.GERMAN));
    addItem(new JLocale(Locale.ITALIAN));
    addItem(new JLocale(Locale.JAPANESE));
    addItem(new JLocale(Locale.SIMPLIFIED_CHINESE));
    addItem(new JLocale(Locale.getDefault()));
    setSelectedItem(new JLocale(Locale.forLanguageTag(System.getProperty("locale.lang.code", "en"))));
  }

  public Locale getSelectedLocale() {
    Object v = getSelectedItem();
    return v instanceof JLocale loc ? loc.getLocale() : Locale.getDefault();
  }
}
