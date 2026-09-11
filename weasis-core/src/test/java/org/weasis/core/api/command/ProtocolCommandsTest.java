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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class ProtocolCommandsTest {

  @Test
  void weasisUriDecodesCommandsAfterQuestionMark() {
    String inner = "$dicom:get -l /tmp/syn";
    String uri =
        "weasis://?" + URLEncoder.encode(inner, StandardCharsets.UTF_8).replace("+", "%20");
    assertTrue(WeasisUri.isWeasisUri(uri));
    var cmds = WeasisUri.commands(uri);
    assertEquals(1, cmds.size());
    assertTrue(cmds.getFirst().startsWith("$dicom:get"));
    assertTrue(cmds.getFirst().contains("-l"));
    assertFalse(WeasisUri.isWeasisUri("$weasis:config cdb"));
  }

  @Test
  void weasisConfigProAndNativeCdb() {
    WeasisConfig cfg =
        WeasisConfig.parse(
            "$weasis:config pro=\"felix.extended.config.properties file:conf/dicomizer.json\" arg=\"-Dgosh.port=17181\"");
    assertEquals("file:conf/dicomizer.json", cfg.pro().get("felix.extended.config.properties"));
    assertTrue(WeasisConfig.KINDS.contains("pro"));
    WeasisConfig nativeCdb = WeasisConfig.parse("$weasis:config cdb");
    assertTrue(nativeCdb.cdbPresent());
    assertNull(nativeCdb.cdb());
  }

  @Test
  void dicomGetLocalAndManifestAndRsRequiresUrl() {
    DicomGetArgs local = DicomGetArgs.parse("$dicom:get", "-l", "/tmp/dicom");
    assertEquals(DicomGetArgs.Mode.LOCAL, local.mode());
    assertEquals("/tmp/dicom", local.value());
    DicomGetArgs wado = DicomGetArgs.parse("dicom:get", "-w", "manifest.xml");
    assertEquals(DicomGetArgs.Mode.MANIFEST, wado.mode());
    assertThrows(
        IllegalArgumentException.class, () -> DicomRsArgs.parse("dicom:rs", "-r", "study"));
    DicomRsArgs rs = DicomRsArgs.parse("-u", "https://pacs.example/rs", "--show-whole-study");
    assertEquals("https://pacs.example/rs", rs.url());
    assertEquals(DicomRsArgs.DEFAULT_ACCEPT_EXT, rs.acceptExt());
    assertTrue(rs.showWholeStudy());
    assertEquals(DicomCloseArgs.Mode.ALL, DicomCloseArgs.parse("-a").mode());
    assertEquals(DicomGetArgs.Mode.PORTABLE, DicomGetArgs.parse("$dicom:get", "-p").mode());
    assertEquals(ImageCommandArgs.GetMode.FILE, ImageCommandArgs.parseGet("-f", "/tmp/a.png"));
  }

  @Test
  void configApplySetsExtendedJsonProperty() {
    String key = "felix.extended.config.properties";
    System.clearProperty(key);
    try {
      WeasisConfig.parse(
              "$weasis:config pro=\"felix.extended.config.properties file:conf/non-dicom-explorer.json\"")
          .applyLaunch();
      assertEquals("file:conf/non-dicom-explorer.json", System.getProperty(key));
    } finally {
      System.clearProperty(key);
    }
  }
}
