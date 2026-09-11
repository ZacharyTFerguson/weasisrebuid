/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.connector;

import java.util.Locale;

/**
 * Package tab: import {@code weasis-native xxx.zip}; Translation tab: {@code
 * weasis-i18n-dist-*.zip}.
 */
public final class PackageTab {

  private PackageTab() {}

  public static boolean isNativeZip(String filename) {
    if (filename == null) {
      return false;
    }
    String n = filename.trim().toLowerCase(Locale.ROOT);
    return n.startsWith("weasis-native") && n.endsWith(".zip");
  }

  public static boolean isI18nDistZip(String filename) {
    if (filename == null) {
      return false;
    }
    String n = filename.trim().toLowerCase(Locale.ROOT);
    return n.startsWith("weasis-i18n-dist") && n.endsWith(".zip");
  }
}
