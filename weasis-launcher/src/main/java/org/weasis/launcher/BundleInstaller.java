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

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.framework.startlevel.FrameworkStartLevel;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;
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
    logUnresolved(framework);
  }

  /** Installed (unresolved) bundles after the start-level raise — Gogo smoke root-cause. */
  static void logUnresolved(Framework framework) {
    BundleContext context = framework.getBundleContext();
    for (Bundle bundle : context.getBundles()) {
      if (bundle.getState() != Bundle.INSTALLED) {
        continue;
      }
      LOGGER.warn("Bundle still INSTALLED: {}", bundle.getSymbolicName());
      BundleRevision revision = bundle.adapt(BundleRevision.class);
      if (revision == null) {
        continue;
      }
      for (BundleRequirement req :
          revision.getDeclaredRequirements(BundleRevision.PACKAGE_NAMESPACE)) {
        if ("optional".equals(req.getDirectives().get("resolution"))) {
          continue;
        }
        LOGGER.warn("  missing-capable package: {}", req.getDirectives().get("filter"));
      }
    }
  }
}
