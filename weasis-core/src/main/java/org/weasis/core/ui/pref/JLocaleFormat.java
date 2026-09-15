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

public class JLocaleFormat extends JComboBox<String> {
  public JLocaleFormat() {
    super(new String[] {"system", "en-US", "fr-FR", "de-DE"});
  }

  public NumberFormat getNumberFormat() {
    String sel = (String) getSelectedItem();
    if (sel == null || "system".equals(sel)) {
      return NumberFormat.getInstance();
    }
    return NumberFormat.getInstance(Locale.forLanguageTag(sel));
  }
}
