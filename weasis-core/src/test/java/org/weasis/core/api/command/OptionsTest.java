/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class OptionsTest {

  @Test
  void dicomGetLocalAndRemoteRepeat() {
    Option opt =
        Options.compile("l(local)w(wado)r(remote)z(zip)p(portable)i(iwado)")
            .parse("-l", "/tmp/a", "-l", "/tmp/b", "-r", "https://example.invalid/x.dcm");
    assertEquals(2, opt.values("l").size());
    assertEquals("/tmp/a", opt.values("l").get(0));
    assertEquals("/tmp/b", opt.values("l").get(1));
    assertEquals("https://example.invalid/x.dcm", opt.get("r"));
    assertTrue(opt.isSet("remote"));
  }

  @Test
  void dicomRsLongUrl() {
    Option opt =
        Options.compile("u(url)r(request)")
            .parse("--url", "https://demo.orthanc-server.com/dicom-web", "-r", "patientID=5Yp0E");
    assertEquals("https://demo.orthanc-server.com/dicom-web", opt.get("url"));
    assertEquals("patientID=5Yp0E", opt.get("r"));
  }

  @Test
  void imageGetFileAndUrl() {
    Option opt =
        Options.compile("f(file)u(url)").parse("-f", "/tmp/a.jpg", "-u", "https://x/y.jpg");
    assertEquals("/tmp/a.jpg", opt.get("f"));
    assertEquals("https://x/y.jpg", opt.get("u"));
  }

  @Test
  void dicomCloseAll() {
    Option opt = Options.compile("a(all)p(patient)y(study)s(series)").parse("--all");
    assertTrue(opt.isSet("a"));
    assertTrue(opt.isSet("all"));
  }
}
