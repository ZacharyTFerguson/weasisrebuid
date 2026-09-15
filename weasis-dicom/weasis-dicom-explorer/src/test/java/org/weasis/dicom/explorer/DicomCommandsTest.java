/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DicomCommandsTest {

  private final DicomCommands commands = new DicomCommands();

  @Test
  void rsBuildsQidoUrl() {
    String out =
        commands.rs("--url", "https://demo.orthanc-server.com/dicom-web", "-r", "patientID=5Yp0E");
    assertTrue(out.contains("/studies"));
    assertTrue(out.contains("patientID=5Yp0E"));
  }

  @Test
  void closeAll() {
    assertEquals("close-all", commands.close("--all"));
    assertEquals("close-all", commands.close("-a"));
  }

  @Test
  void getRemoteIsParsedWithoutNetwork() {
    String out =
        commands.get("-r", "https://example.invalid/a.dcm", "-w", "https://example.invalid/mf.xml");
    assertTrue(out.contains("remote https://example.invalid/a.dcm"));
    assertTrue(out.contains("manifest https://example.invalid/mf.xml"));
  }
}
