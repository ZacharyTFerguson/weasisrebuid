/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.weasis.core.api.util.LocalUtil;

public class Messages {
  public static final String BUNDLE_NAME = "org.weasis.dicom.viewer2d.messages";

  private Messages() {}

  public static String getString(String key) {
    return getString(key, LocalUtil.textLocale());
  }

  public static String getString(String key, Locale locale) {
    if (key == null || key.isBlank()) {
      return "";
    }
    Locale loc = locale == null ? Locale.ROOT : locale;
    try {
      return ResourceBundle.getBundle(BUNDLE_NAME, loc).getString(key);
    } catch (MissingResourceException e) {
      return key;
    }
  }
}
