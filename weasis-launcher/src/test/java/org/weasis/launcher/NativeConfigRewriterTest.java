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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class NativeConfigRewriterTest {

  @Test
  void rewritesMavenFileUrlsToResourcesBundle() {
    String json =
        """
        {
          "weasisPreferences": [
            {"code": "felix.auto.start.1", "value": "file:${maven.localRepository}/org/apache/felix/gogo/${felix.gogo.version}/gogo-runtime-1.1.6.jar file:${maven.localRepository}/org/foo/bar/1.0/bar-1.0.jar"}
          ]
        }
        """;
    String rewritten = NativeConfigRewriter.rewriteFileUrls(json);
    assertTrue(rewritten.contains("file:${weasis.resources.path}/bundle/gogo-runtime-1.1.6.jar"));
    assertTrue(rewritten.contains("file:${weasis.resources.path}/bundle/bar-1.0.jar"));
    assertFalse(
        rewritten.contains("felix.auto.start.1") && rewritten.contains("maven.localRepository"));
    assertFalse(NativeConfigRewriter.usesMavenRepo(rewritten));
  }

  @Test
  void nativeJsonResolvesBundlesWithoutMavenRepo(@TempDir Path stage) throws Exception {
    Path bundle = stage.resolve("bundle");
    Files.createDirectories(bundle);
    Files.writeString(bundle.resolve("gogo-runtime-1.1.6.jar"), "fake");
    Path json = stage.resolve("base.json");
    Files.writeString(
        json,
        NativeConfigRewriter.rewriteFileUrls(
            """
            {
              "weasisPreferences": [
                {"code": "org.osgi.framework.startlevel.beginning", "value": "130", "type": "A", "category": "FELIX_CONFIG"},
                {"code": "felix.startlevel.bundle", "value": "300", "type": "A", "category": "FELIX_CONFIG"},
                {"code": "felix.auto.start.1", "value": "file:${maven.localRepository}/org/apache/felix/gogo/1.1.6/gogo-runtime-1.1.6.jar", "type": "A", "category": "FELIX_INSTALL"}
              ]
            }
            """));
    System.setProperty(NativeConfigRewriter.RESOURCES_PATH, stage.toString());
    try {
      ConfigData data = ConfigData.load(json, Map.of("app.version", "4.7.3"));
      assertEquals(1, data.autoBundles().size());
      Path jar = data.autoBundles().getFirst().files().getFirst();
      assertEquals(bundle.resolve("gogo-runtime-1.1.6.jar"), jar);
      assertTrue(Files.isRegularFile(jar));
      assertFalse(jar.toString().contains(".m2"));
    } finally {
      System.clearProperty(NativeConfigRewriter.RESOURCES_PATH);
    }
  }

  @Test
  void distAndLauncherJsonDoNotAutoStartI18n() throws Exception {
    Path root = Mx03ShippingPrefsTest.moduleRoot();
    for (Path json :
        new Path[] {
          root.resolve("conf/base.json"),
          root.resolve("../weasis-distributions/etc/config/base.json")
        }) {
      String text = Files.readString(json);
      assertTrue(Files.isRegularFile(json), json.toString());
      for (String line : text.split("\n")) {
        if (line.contains("felix.auto.start") || line.contains("felix.auto.install")) {
          assertFalse(line.contains("i18n"), line);
          assertFalse(line.contains("nl_fr"), line);
        }
      }
      ConfigData data = ConfigData.load(json, Map.of("app.version", "4.7.3"));
      assertEquals("en", data.value("locale.lang.code"));
      assertEquals("system", data.value("locale.format.code"));
      assertEquals("dicom,DICOM,IMAGES,images", data.value("weasis.portable.dicom.directory"));
    }
    Path fragment = root.resolve("../weasis-i18n-dist/org.weasis.core.nl_fr/META-INF/MANIFEST.MF");
    assertTrue(Files.isRegularFile(fragment));
    assertTrue(Files.readString(fragment).contains("Fragment-Host: org.weasis.core"));
  }

  @Test
  void launcherMessagesCannotHotSwap() throws Exception {
    Field bundle = Messages.class.getDeclaredField("RESOURCE_BUNDLE");
    assertTrue(Modifier.isStatic(bundle.getModifiers()));
    assertTrue(Modifier.isFinal(bundle.getModifiers()));
    for (Method method : Messages.class.getDeclaredMethods()) {
      String name = method.getName().toLowerCase();
      assertFalse(name.contains("reload"), method.getName());
      assertFalse(name.contains("setbundle"), method.getName());
    }
    assertTrue(Messages.getString("format.example").contains("{0}"));
  }
}
