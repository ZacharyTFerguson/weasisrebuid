/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.util;

import java.util.Locale;

public final class LocalUtil {
  private LocalUtil() {}

  public static Locale textLocale() {
    String lang = System.getProperty("locale.lang.code", "en");
    if (lang == null || lang.isBlank() || "system".equalsIgnoreCase(lang)) {
      return Locale.getDefault();
    }
    return Locale.forLanguageTag(lang.replace('_', '-'));
  }
}
