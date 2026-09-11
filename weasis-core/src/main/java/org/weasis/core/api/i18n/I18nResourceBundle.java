/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.i18n;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Host-bundle i18n hook. OSGi fragments from {@code weasis-i18n-dist} attach to the host class
 * loader; {@link ResourceBundle#getBundle(String, Locale, ClassLoader)} then sees the fragment
 * properties. Format tokens such as {@code {0}} are returned unchanged (translators must not
 * translate them).
 */
public final class I18nResourceBundle {

  private I18nResourceBundle() {}

  public static ResourceBundle load(Class<?> host, String baseName) {
    ClassLoader loader =
        host == null ? I18nResourceBundle.class.getClassLoader() : host.getClassLoader();
    if (loader == null) {
      loader = ClassLoader.getSystemClassLoader();
    }
    return ResourceBundle.getBundle(baseName, Locale.getDefault(), loader);
  }

  public static String getString(Class<?> host, String baseName, String key) {
    return getString(load(host, baseName), key);
  }

  public static String getString(ResourceBundle bundle, String key) {
    if (key == null) {
      return "!null!";
    }
    try {
      return bundle.getString(key);
    } catch (MissingResourceException e) {
      return '!' + key + '!';
    }
  }
}
