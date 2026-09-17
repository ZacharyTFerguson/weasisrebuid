/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.launcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class UtilsProtocolTest {

  @Test
  void decodesDocumentedRemoteGetUri() {
    String command = "$dicom:get -r \"https://nroduit.github.io/demo-archive/us-palette.dcm\"";
    String encoded = URLEncoder.encode(command, StandardCharsets.UTF_8).replace("+", "%20");
    String uri = "weasis://?" + encoded;
    assertTrue(Utils.isWeasisUri(uri));
    List<String> cmds = Utils.commands(uri);
    assertEquals(1, cmds.size());
    assertTrue(cmds.getFirst().contains("dicom:get"));
    assertTrue(cmds.getFirst().contains("-r"));
  }

  @Test
  void splitsCloseThenGet() {
    List<String> cmds =
        Utils.commands("$dicom:close --all $dicom:get -r \"https://example.invalid/a.dcm\"");
    assertEquals(2, cmds.size());
    assertTrue(cmds.get(0).contains("dicom:close"));
    assertTrue(cmds.get(1).contains("dicom:get"));
  }

  @Test
  void configProSetsExtendedJsonAndGogoPort() {
    Utils.LaunchRequest req =
        Utils.parseLaunch(
            new String[] {
              "$weasis:config pro=\"felix.extended.config.properties file:conf/dicomizer.json\" pro=\"gosh.port 17181\""
            });
    assertEquals(
        "file:conf/dicomizer.json", req.properties().get("felix.extended.config.properties"));
    assertEquals("17181", req.properties().get("gosh.port"));
    assertTrue(req.commands().isEmpty());
  }

  @Test
  void configArgBecomesCommand() {
    Utils.LaunchRequest req =
        Utils.parseLaunch(new String[] {"$weasis:config arg=\"$dicom:close --all\""});
    assertEquals(List.of("$dicom:close --all"), req.commands());
  }

  @Test
  void cdbWithoutValueMeansNativeInstall() {
    Utils.LaunchRequest req = Utils.parseLaunch(new String[] {"$weasis:config cdb"});
    assertEquals("", req.properties().get("weasis.cdb"));
  }

  @Test
  void overlayNonDicomProfile() throws Exception {
    Path base = Mx03ShippingPrefsTest.moduleRoot().resolve("conf/base.json");
    Path overlay = Mx03ShippingPrefsTest.moduleRoot().resolve("conf/non-dicom-explorer.json");
    assertTrue(Files.isRegularFile(overlay), overlay.toString());
    ConfigData data = ConfigData.load(base, overlay, Map.of("app.version", "4.7.3"));
    assertEquals("base-explorer", data.value("weasis.profile"));
    assertEquals("Weasis Image Explorer", data.value("weasis.name"));
    assertEquals(100, data.beginningStartLevel());
    assertEquals("", data.value("felix.auto.start.30").trim());
    assertTrue(data.value("felix.auto.start.40").contains("weasis-base-explorer"));
    Path dist =
        Mx03ShippingPrefsTest.moduleRoot()
            .resolve("../weasis-distributions/etc/config/non-dicom-explorer.json");
    assertTrue(Files.isRegularFile(dist));
  }
}
