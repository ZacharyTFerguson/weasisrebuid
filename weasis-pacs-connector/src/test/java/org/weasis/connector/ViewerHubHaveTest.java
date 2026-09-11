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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.command.WeasisConfig;
import org.weasis.core.api.command.WeasisUri;

class ViewerHubHaveTest {

  static final String NATIVE = "https://hub.example/weasis-native-4.7.3.zip";

  @Test
  void displayAndIidProduceWeasisSchemeWithCdb() {
    ViewerHub hub = new ViewerHub(ViewerSelectionRules.defaults(), NATIVE);
    Map<String, List<String>> q =
        DisplayQuery.fromQueryString(
            "patientID=SYN-1&studyUID=2.25.1&cdb=" + NATIVE + "&access_token=tok");
    ViewerHub.LaunchResult display = hub.handle(ViewerHub.DISPLAY, q);
    assertEquals(302, display.status());
    assertTrue(WeasisUri.isWeasisUri(display.location()), display.location());
    List<String> cmds = WeasisUri.commands(display.location());
    assertTrue(cmds.stream().anyMatch(WeasisConfig::isConfigCommand), cmds.toString());
    WeasisConfig cfg = WeasisConfig.parse(cmds.getFirst());
    assertTrue(cfg.cdbPresent());
    assertEquals(NATIVE, cfg.cdb());
    assertEquals("tok", cfg.auth());

    Map<String, List<String>> iid =
        DisplayQuery.fromQueryString("requestType=STUDY&studyUID=2.25.1&cdb=" + NATIVE);
    ViewerHub.LaunchResult iidResult = hub.handle(ViewerHub.IID, iid);
    assertEquals(302, iidResult.status());
    assertTrue(iidResult.location().startsWith("weasis://"));
    assertEquals(400, hub.handle(ViewerHub.IID, Map.of()).status());
  }

  @Test
  void dcm4cheeViewButtonOpensCloneViaWeasis() {
    String href = IidMatrix.viewButtonHref("5.32.0", NATIVE, "tok", true);
    assertTrue(href.startsWith("../../../weasis-pacs-connector/IHEInvokeImageDisplay?"));
    assertTrue(href.contains("cdb="));
    assertTrue(href.contains("access_token="));
    assertTrue(href.contains("target=_self"));
    assertTrue(href.contains("{{patientID}}"));
    Map<String, List<String>> q =
        DisplayQuery.fromQueryString(href.substring(href.indexOf('?') + 1));
    q.put("patientID", List.of("SYN-1"));
    ViewerHub hub = new ViewerHub(ViewerSelectionRules.defaults(), NATIVE);
    ViewerHub.LaunchResult result = hub.handle(ViewerHub.IID, q);
    assertEquals(302, result.status(), result.error());
    assertTrue(WeasisUri.isWeasisUri(result.location()));
    assertTrue(
        WeasisUri.commands(result.location()).stream().anyMatch(c -> c.contains("weasis:config")));
  }

  @Test
  void connectorVersionMatrixAndPlaceholders() {
    assertEquals(IidMatrix.ConnectorLine.V7, IidMatrix.connectorForArc("5.30.1"));
    assertEquals("../../", IidMatrix.iidContextPath(IidMatrix.ConnectorLine.V7));
    assertEquals(IidMatrix.ConnectorLine.V8, IidMatrix.connectorForArc("5.31.0"));
    assertEquals("../../../", IidMatrix.iidContextPath(IidMatrix.ConnectorLine.V8));
    assertEquals(IidMatrix.Placeholder.BRACES, IidMatrix.placeholderForArc("5.19.0"));
    assertEquals("{}", IidMatrix.patientPlaceholder(IidMatrix.Placeholder.BRACES));
    assertEquals(IidMatrix.Placeholder.NAMED, IidMatrix.placeholderForArc("5.19.1"));
    assertEquals("IID_PATIENT_URL", IidMatrix.patientPlaceholder(IidMatrix.Placeholder.NAMED));
    assertEquals("IID_STUDY_URL", IidMatrix.studyPlaceholder(IidMatrix.Placeholder.NAMED));
    assertEquals(IidMatrix.Placeholder.MUSTACHE, IidMatrix.placeholderForArc("5.22.2"));
    assertThrows(
        IllegalArgumentException.class, () -> IidMatrix.viewButtonHref("5.32", "", null, true));
  }

  @Test
  void dateFiltersAreViewerHubEnglishNotTypicalIhe() {
    assertEquals(DateFilters.Meaning.OLDER_THAN, DateFilters.lowerDateTime());
    assertEquals(DateFilters.Meaning.MORE_RECENT_THAN, DateFilters.upperDateTime());
    Instant study = Instant.parse("2020-06-01T00:00:00Z");
    Instant lower = Instant.parse("2024-01-01T00:00:00Z");
    Instant upper = Instant.parse("2018-01-01T00:00:00Z");
    assertTrue(DateFilters.matches(study, lower, null));
    assertFalse(DateFilters.matches(study, Instant.parse("2019-01-01T00:00:00Z"), null));
    assertTrue(DateFilters.matches(study, null, upper));
    assertFalse(DateFilters.matches(study, null, Instant.parse("2021-01-01T00:00:00Z")));
  }

  @Test
  void groupsCannotMixUserAndHost() {
    assertFalse(GroupPolicy.canBelong(GroupPolicy.Kind.USER, GroupPolicy.Kind.HOST_GROUP));
    assertFalse(GroupPolicy.canBelong(GroupPolicy.Kind.HOST, GroupPolicy.Kind.USER_GROUP));
    assertTrue(GroupPolicy.canBelong(GroupPolicy.Kind.USER, GroupPolicy.Kind.USER_GROUP));
    assertTrue(GroupPolicy.canBelong(GroupPolicy.Kind.HOST, GroupPolicy.Kind.HOST_GROUP));
  }

  @Test
  void packageTabAndVersionCompatibilityAndCache() {
    assertTrue(PackageTab.isNativeZip("weasis-native 4.7.3.zip"));
    assertTrue(PackageTab.isI18nDistZip("weasis-i18n-dist-4.7.3.zip"));
    assertFalse(PackageTab.isNativeZip("other.zip"));
    VersionCompatibility vc = VersionCompatibility.pin473();
    assertEquals("4.7.0", vc.mapping("4.7.3").minimalVersion());
    assertEquals("4.7.3", vc.mapping("4.7.3").i18nVersion());
    assertEquals("4.7.3", vc.qualifierFromUserAgent("Weasis/4.7.3 (Linux)"));
    assertEquals(VersionCompatibility.DEFAULT_QUALIFIER, vc.qualifierFromUserAgent("Mozilla/5.0"));
    ManifestCache cache = new ManifestCache(1_000);
    cache.put("q", "<wado>", 0);
    assertEquals("<wado>", cache.get("q", 500));
    assertEquals(null, cache.get("q", 2_000));
    assertEquals(180L, ViewerHub.MANIFEST_TTL_SECONDS);
    assertEquals(3, ViewerHub.connectors().size());
  }

  @Test
  void containsInDescriptionIsCaseAndDiacriticInsensitive() {
    assertTrue(ViewerHub.containsInsensitive("Hôpital CT", "hopital"));
    assertTrue(ViewerHub.containsInsensitive("Chest", "CHEST"));
    assertFalse(ViewerHub.containsInsensitive("Chest", "brain"));
  }

  @Test
  void selectionRulesDefaultWeasisAndModalitiesOr() {
    ViewerSelectionRules rules =
        ViewerSelectionRules.defaults()
            .addFirst(
                new ViewerSelectionRules.Rule(
                    DisplayQuery.Viewer.OHIF, List.of("US", "XA"), "ALL"));
    DisplayQuery us = DisplayQuery.parse(Map.of("modalitiesInStudy", List.of("US")));
    assertEquals(DisplayQuery.Viewer.OHIF, rules.pick(us));
    DisplayQuery ct = DisplayQuery.parse(Map.of("modalitiesInStudy", List.of("CT")));
    assertEquals(DisplayQuery.Viewer.WEASIS, rules.pick(ct));
  }

  @Test
  void rsRequiresUrlWhenArchiveIsHttp() {
    DisplayQuery q =
        DisplayQuery.parse(
            Map.of(
                "archive", List.of("https://pacs.example/rs"),
                "cdb", List.of(NATIVE),
                "patientID", List.of("SYN-1")));
    String uri = WeasisLaunch.weasisUri(q, NATIVE);
    String decoded = WeasisUri.decode(uri);
    assertTrue(decoded.contains("$dicom:rs"));
    assertTrue(decoded.contains("-u https://pacs.example/rs"));
  }

  @Test
  void notAFelixAutoStartBundle() throws Exception {
    Path dir = Path.of(System.getProperty("user.dir")).toAbsolutePath();
    Path repo =
        Files.isRegularFile(dir.resolve("../weasis-launcher/conf/base.json"))
            ? dir.getParent()
            : dir;
    Path json = repo.resolve("weasis-launcher/conf/base.json");
    assertTrue(Files.isRegularFile(json), json.toString());
    String text = Files.readString(json);
    for (String line : text.split("\n")) {
      if (line.contains("felix.auto.start") || line.contains("felix.auto.install")) {
        assertFalse(line.contains("pacs-connector"), line);
        assertFalse(line.contains("viewerhub"), line.toLowerCase());
      }
    }
    assertEquals(3 * 60, ManifestCache.TTL.toSeconds());
    assertNotNull(DisplayQuery.Viewer.MICRODICOM);
  }
}
