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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.core.Messages;

class I18nResourceBundleTest {

  @Test
  void hostMessagesKeepFormatTokens() {
    String value = Messages.getString("format.example");
    assertTrue(value.contains("{0}"), value);
    assertEquals("Weasis Core", Messages.getString("bundle.name"));
  }

  @Test
  void fragmentClassLoaderOverlaysBasename(@TempDir Path dir) throws Exception {
    Path pkg = dir.resolve("org/weasis/core");
    Files.createDirectories(pkg);
    Files.writeString(
        pkg.resolve("messages_fr.properties"),
        "bundle.name=Noyau Weasis\nformat.example=Bonjour {0}\n",
        StandardCharsets.UTF_8);
    try (URLClassLoader loader =
        new URLClassLoader(new URL[] {dir.toUri().toURL()}, Messages.class.getClassLoader())) {
      ResourceBundle fr =
          ResourceBundle.getBundle("org.weasis.core.messages", java.util.Locale.FRENCH, loader);
      assertEquals("Noyau Weasis", fr.getString("bundle.name"));
      assertTrue(fr.getString("format.example").contains("{0}"));
    }
  }
}
