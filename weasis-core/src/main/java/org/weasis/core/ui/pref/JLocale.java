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
import java.util.Objects;

public class JLocale {
  private final Locale locale;

  public JLocale(Locale locale) {
    this.locale = locale == null ? Locale.getDefault() : locale;
  }

  public Locale getLocale() {
    return locale;
  }

  @Override
  public String toString() {
    String display = locale.getDisplayName(locale);
    return display == null || display.isBlank() ? locale.toLanguageTag() : display;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof JLocale other && Objects.equals(locale, other.locale);
  }

  @Override
  public int hashCode() {
    return locale.hashCode();
  }
}
