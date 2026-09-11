/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.launcher;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Launcher i18n cannot hot-swap: the bundle is loaded once into a static final field. Restart the
 * process after replacing launcher resources.
 */
public class Messages {

  static final String BUNDLE_NAME = "org.weasis.launcher.messages";
  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

  private Messages() {}

  public static String getString(String key) {
    if (key == null) {
      return "!null!";
    }
    try {
      return RESOURCE_BUNDLE.getString(key);
    } catch (MissingResourceException e) {
      return "!" + key + "!";
    }
  }
}
