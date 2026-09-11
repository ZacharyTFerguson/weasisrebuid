/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.base.explorer;

import org.weasis.core.api.i18n.I18nResourceBundle;

/** Host-bundle Messages hook. Fragments from weasis-i18n-dist overlay this basename. */
public class Messages {

  static final String BUNDLE_NAME = "org.weasis.base.explorer.messages";

  private Messages() {}

  public static String getString(String key) {
    return I18nResourceBundle.getString(Messages.class, BUNDLE_NAME, key);
  }
}
