/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.i18n;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import org.junit.jupiter.api.Test;

class BaseViewer2dI18nFragmentTest {

  @Test
  void fragmentHostMatchesAndNameEndsWithI18n() throws IOException {
    Path mf = Path.of("target/classes/META-INF/MANIFEST.MF");
    assertTrue(Files.isRegularFile(mf), mf.toAbsolutePath().toString());
    Manifest manifest;
    try (InputStream in = Files.newInputStream(mf)) {
      manifest = new Manifest(in);
    }
    Attributes attrs = manifest.getMainAttributes();
    String name = attrs.getValue("Bundle-Name");
    String bsn = attrs.getValue("Bundle-SymbolicName");
    String host = attrs.getValue("Fragment-Host");
    assertTrue(name != null && name.toLowerCase().endsWith("i18n"), name);
    assertTrue(bsn != null && bsn.endsWith("i18n"), bsn);
    assertTrue(host != null && host.startsWith("org.weasis.base.viewer2d"), host);
  }

  @Test
  void germanMessagesAreOnTheClasspath() {
    assertTrue(
        Thread.currentThread()
                .getContextClassLoader()
                .getResource("org/weasis/base/viewer2d/messages_de.properties")
            != null);
  }
}
