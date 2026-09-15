/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.explorer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.zip.GZIPOutputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class AcquirePatientCommandTest {

  static final String XML =
      "<patient><name>SYNTHETIC^ACQUIRE</name><id>SYN-ACQ-1</id><sex>O</sex></patient>";

  @Test
  void xmlInboundUrlSafeAndFileUrl(@TempDir Path dir) throws Exception {
    AcquirePatientStore store = new AcquirePatientStore();
    AcquirePatientCommand cmd = new AcquirePatientCommand(store);
    assertTrue(cmd.patient().contains("Usage: acquire:patient"));
    assertEquals("ok SYN-ACQ-1", cmd.patient("-x", XML));
    assertEquals("SYNTHETIC^ACQUIRE", store.get().patientName());

    byte[] gz = gzip(XML.getBytes(StandardCharsets.UTF_8));
    AcquirePatientStore storeI = new AcquirePatientStore();
    assertEquals(
        "ok SYN-ACQ-1",
        new AcquirePatientCommand(storeI).patient("-i", Base64.getEncoder().encodeToString(gz)));
    assertEquals("SYN-ACQ-1", storeI.get().patientId());

    AcquirePatientStore storeS = new AcquirePatientStore();
    assertEquals(
        "ok SYN-ACQ-1",
        new AcquirePatientCommand(storeS).patient("-s", Base64.getUrlEncoder().encodeToString(gz)));

    Path xmlFile = dir.resolve("patient.xml");
    Files.writeString(xmlFile, XML);
    AcquirePatientStore storeU = new AcquirePatientStore();
    assertEquals(
        "ok SYN-ACQ-1",
        new AcquirePatientCommand(storeU).patient("-u", xmlFile.toUri().toString()));
  }

  static byte[] gzip(byte[] raw) throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (GZIPOutputStream gz = new GZIPOutputStream(baos)) {
      gz.write(raw);
    }
    return baos.toByteArray();
  }
}
