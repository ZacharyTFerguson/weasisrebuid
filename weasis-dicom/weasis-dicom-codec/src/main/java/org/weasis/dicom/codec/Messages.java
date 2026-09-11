/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.codec;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/** Host-bundle Messages hook. Fragments from weasis-i18n-dist overlay this basename. */
public class Messages {

  static final String BUNDLE_NAME = "org.weasis.dicom.codec.messages";
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
