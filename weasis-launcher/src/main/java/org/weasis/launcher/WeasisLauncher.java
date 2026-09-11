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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Boots Apache Felix from {@code base.json}, then waits until the framework stops.
 *
 * <p>{@code gosh.port} is a VM property (default 17179), never a JSON pref (MX-16).
 */
public class WeasisLauncher {

  public static final String GOGO_PORT_PROPERTY = "gosh.port";
  public static final String DEFAULT_GOGO_PORT = "17179";
  public static final String DICOMIZER_GOGO_PORT = "17181";
  public static final String BASE_JSON_PROPERTY = "weasis.base.json";
  public static final String EXTENDED_CONFIG_PROPERTY = "felix.extended.config.properties";
  public static final String DICOMIZER_PROFILE = "dicomizer";

  private static final Logger LOGGER = LoggerFactory.getLogger(WeasisLauncher.class);

  public void launch(String[] args) throws Exception {
    suppressAssistiveTech();
    Path weasisHome = Path.of(System.getProperty("user.home"), ".weasis");
    Files.createDirectories(weasisHome.resolve("log"));
    BootLog.install(weasisHome.resolve("log"));

    BuildInfo buildInfo = BuildInfo.load();
    Path baseJson = resolveBaseJson();
    Path overlay = resolveExtendedJson(baseJson);
    LOGGER.info("Loading {} overlay={}", baseJson, overlay);
    ConfigData config = ConfigData.load(baseJson, overlay, buildInfo.asMap());

    applyWeasisSystemProperties(config);
    ensureGogoPort();
    buildInfo
        .asMap()
        .forEach(
            (key, value) -> {
              if (System.getProperty(key) == null && value != null && !value.isBlank()) {
                System.setProperty(key, value);
              }
            });
    Map<String, String> fwConfig = new HashMap<>(config.frameworkProperties());
    fwConfig.put(GOGO_PORT_PROPERTY, System.getProperty(GOGO_PORT_PROPERTY));
    // Keep the framework up when stdin is not a TTY (CI / background launch).
    fwConfig.put("gosh.args", " --noshutdown --nointeractive");
    FrameworkFactory factory = ServiceLoader.load(FrameworkFactory.class).iterator().next();
    Framework framework = factory.newFramework(fwConfig);
    framework.init();
    BundleInstaller.install(framework, config);
    framework.start();
    BundleInstaller.raiseStartLevel(framework, config);
    LauncherGogo.register(framework);
    GogoTelnet.start(framework, System.getProperty(GOGO_PORT_PROPERTY));
    LOGGER.info(
        "Felix {} started; Gogo {} ; weasis {}",
        framework.getSymbolicName(),
        System.getProperty(GOGO_PORT_PROPERTY),
        config.value("weasis.version"));

    String timeout = System.getProperty("weasis.boot.timeout.seconds");
    if (timeout != null && !timeout.isBlank()) {
      framework.waitForStop(Long.parseLong(timeout) * 1000L);
      framework.stop();
    } else {
      framework.waitForStop(0);
    }
  }

  static String resolveGogoPort(String existing) {
    return resolveGogoPort(existing, System.getProperty("weasis.profile"));
  }

  /** MX-16: desktop 17179; Dicomizer profile 17181. Never a JSON pref. */
  static String resolveGogoPort(String existing, String profile) {
    if (existing != null && !existing.isBlank()) {
      return existing;
    }
    if (DICOMIZER_PROFILE.equals(profile)) {
      return DICOMIZER_GOGO_PORT;
    }
    return DEFAULT_GOGO_PORT;
  }

  static void ensureGogoPort() {
    System.setProperty(GOGO_PORT_PROPERTY, resolveGogoPort(System.getProperty(GOGO_PORT_PROPERTY)));
  }

  static Path resolveExtendedJson(Path baseJson) {
    String raw = System.getProperty(EXTENDED_CONFIG_PROPERTY);
    if (raw == null || raw.isBlank()) {
      return null;
    }
    String path = raw.trim();
    if (path.startsWith("file:")) {
      path = path.substring("file:".length());
      if (path.startsWith("//")) {
        path = path.substring(1);
      }
    }
    Path candidate = Path.of(path);
    if (Files.isRegularFile(candidate)) {
      return candidate.toAbsolutePath().normalize();
    }
    if (baseJson != null && baseJson.getParent() != null) {
      Path nextToBase = baseJson.getParent().resolve(candidate.getFileName());
      if (Files.isRegularFile(nextToBase)) {
        return nextToBase.toAbsolutePath().normalize();
      }
      Path fromConfParent = baseJson.getParent().resolve(path);
      if (Files.isRegularFile(fromConfParent)) {
        return fromConfParent.toAbsolutePath().normalize();
      }
    }
    Path fromCwd = Path.of(System.getProperty("user.dir", ".")).resolve(path);
    if (Files.isRegularFile(fromCwd)) {
      return fromCwd.toAbsolutePath().normalize();
    }
    throw new IllegalStateException("extended config not found: " + raw);
  }

  static Path resolveBaseJson() {
    String override = System.getProperty(BASE_JSON_PROPERTY);
    if (override != null && !override.isBlank()) {
      return Path.of(override);
    }
    Path userDir = Path.of(System.getProperty("user.dir"));
    Path[] candidates =
        new Path[] {
          userDir.resolve("conf/base.json"),
          userDir.resolve("weasis-launcher/conf/base.json"),
          userDir.resolve("../weasis-launcher/conf/base.json")
        };
    for (Path candidate : candidates) {
      if (Files.isRegularFile(candidate)) {
        return candidate.toAbsolutePath().normalize();
      }
    }
    throw new IllegalStateException(
        "base.json not found (set -D"
            + BASE_JSON_PROPERTY
            + "=... or run with working directory weasis-launcher)");
  }

  private static void applyWeasisSystemProperties(ConfigData config) {
    config
        .values()
        .forEach(
            (key, value) -> {
              if (value == null) {
                return;
              }
              boolean launchPref = key.startsWith("weasis.") || key.startsWith("org.weasis.");
              if (launchPref && System.getProperty(key) == null) {
                System.setProperty(key, value);
              }
            });
  }

  private static void suppressAssistiveTech() {
    System.setProperty("javax.accessibility.assistive_technologies", "");
    System.setProperty("javax.accessibility.assistive_technologies.disabled", "true");
  }
}
