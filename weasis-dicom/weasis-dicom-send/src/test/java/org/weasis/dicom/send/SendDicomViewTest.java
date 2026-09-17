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
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SendDicomViewTest {

  @Test
  void protocolSelectionAndStowMultipartFromFileList(@TempDir Path dir) throws Exception {
    SendDicomView view = new SendDicomFactory().newView();
    assertEquals(SendDicomFactory.Protocol.C_STORE, view.protocol());
    view.setProtocol(SendDicomFactory.Protocol.STOW_RS);
    view.setDestination("https://host/dicom-web");
    Path part = dir.resolve("a.dcm");
    Files.write(part, "DICM".getBytes(StandardCharsets.UTF_8));
    view.addFile(part.toFile());
    assertEquals("https://host/dicom-web/studies", view.resolvedStowUrl());
    String body = new String(view.buildStowBody("BOUND"), StandardCharsets.ISO_8859_1);
    assertTrue(body.contains("--BOUND"));
    assertTrue(body.contains("Content-Type: application/dicom"));
    assertTrue(body.contains("DICM"));
    assertEquals("https://host/dicom-web/studies", view.prepareSend());

    view.setProtocol(SendDicomFactory.Protocol.C_STORE);
    view.setDestination("PACS");
    assertEquals("PACS", view.prepareSend());
    assertEquals(SendDicomFactory.Protocol.C_STORE, view.protocol());
  }

  @Test
  void sendCstoreStowMapSetsNamedState() {
    SendDicomView view = new SendDicomFactory().newView();
    assertEquals("send-page", view.getName());
    assertEquals("send-cstore", view.cstoreButton().getName());
    assertEquals("send-stow", view.stowButton().getName());
    assertEquals("send-state", view.stateLabel().getName());
    assertEquals("none", view.stateText());
    view.cstoreButton().doClick();
    assertEquals("C-STORE", view.stateText());
    assertEquals(SendDicomFactory.Protocol.C_STORE, view.protocol());
    view.stowButton().doClick();
    assertEquals("STOW-RS", view.stateText());
    assertEquals(SendDicomFactory.Protocol.STOW_RS, view.protocol());
    view.resetToDefaultValues();
    assertEquals("none", view.stateText());
  }
}
