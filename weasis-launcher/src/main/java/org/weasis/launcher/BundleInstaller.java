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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.framework.startlevel.FrameworkStartLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Installs {@code felix.auto.start.*} / {@code felix.auto.install.*} jars. Missing files are
 * skipped so later WPs can fill the start-level catalog without breaking WP-0 boot.
 */
public final class BundleInstaller {

  private static final Logger LOGGER = LoggerFactory.getLogger(BundleInstaller.class);

  private BundleInstaller() {}

  public static void install(Framework framework, ConfigData config) throws BundleException {
    BundleContext context = framework.getBundleContext();
    List<ConfigData.AutoBundle> autos = config.autoBundles();
    for (ConfigData.AutoBundle auto : autos) {
      for (Path jar : auto.files()) {
        if (!Files.isRegularFile(jar)) {
          LOGGER.warn("Skip missing bundle (WP not built yet): {}", jar);
          continue;
        }
        String location = jar.toUri().toString();
        try (InputStream in = Files.newInputStream(jar)) {
          Bundle bundle = context.installBundle(location, in);
          bundle.adapt(BundleStartLevel.class).setStartLevel(auto.startLevel());
          if (auto.start()) {
            bundle.start();
          }
          LOGGER.info(
              "{} {} @{} ({})",
              auto.start() ? "start" : "install",
              bundle.getSymbolicName(),
              auto.startLevel(),
              jar.getFileName());
        } catch (Exception e) {
          throw new BundleException("Cannot install " + jar, e);
        }
      }
    }
  }

  /**
   * Installs i18n fragment jars from {@code weasis.i18n.dir} (native zip). Fragments are not a
   * {@code felix.auto.start.*} line and are not started.
   */
  public static void installI18nFragments(Framework framework, ConfigData config)
      throws BundleException {
    if (framework == null || config == null) {
      return;
    }
    String dir = config.value(NativeConfigRewriter.I18N_DIR);
    if (dir == null || dir.isBlank()) {
      dir = System.getProperty(NativeConfigRewriter.I18N_DIR, "");
    }
    if (dir == null || dir.isBlank()) {
      return;
    }
    Path folder = Path.of(dir);
    if (!Files.isDirectory(folder)) {
      LOGGER.info("i18n dir missing (ok in Maven-dev): {}", folder);
      return;
    }
    BundleContext context = framework.getBundleContext();
    try (Stream<Path> walk = Files.list(folder)) {
      List<Path> jars =
          walk.filter(p -> p.getFileName().toString().endsWith(".jar"))
              .filter(Files::isRegularFile)
              .sorted()
              .toList();
      for (Path jar : jars) {
        String location = jar.toUri().toString();
        try (InputStream in = Files.newInputStream(jar)) {
          context.installBundle(location, in);
          LOGGER.info("i18n fragment install {} (no start)", jar.getFileName());
        } catch (Exception e) {
          throw new BundleException("Cannot install i18n fragment " + jar, e);
        }
      }
    } catch (IOException e) {
      throw new BundleException("Cannot list i18n dir " + folder, e);
    }
  }

  public static void raiseStartLevel(Framework framework, ConfigData config) {
    int beginning = config.beginningStartLevel();
    CountDownLatch raised = new CountDownLatch(1);
    framework
        .adapt(FrameworkStartLevel.class)
        .setStartLevel(beginning, event -> raised.countDown());
    try {
      if (!raised.await(60, TimeUnit.SECONDS)) {
        throw new IllegalStateException("Timed out raising framework start level to " + beginning);
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("Interrupted raising framework start level", e);
    }
    LOGGER.info("Framework start level {}", beginning);
  }
}
