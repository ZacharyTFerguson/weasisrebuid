/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.send;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;

class StowRSTest {

  @Test
  void stowUrlAndMultipartBody() {
    StowRS stow = new StowRS();
    assertEquals("https://host/dicom-web/studies", stow.resolveStowUrl("https://host/dicom-web"));
    byte[] body =
        stow.buildMultipartBody("BOUND", List.of("DICM".getBytes(StandardCharsets.UTF_8)));
    String text = new String(body, StandardCharsets.ISO_8859_1);
    assertTrue(text.contains("--BOUND"));
    assertTrue(text.contains("Content-Type: application/dicom"));
    assertTrue(text.contains("DICM"));
    assertTrue(stow.contentTypeFor("BOUND").contains("multipart/related"));
  }
}
