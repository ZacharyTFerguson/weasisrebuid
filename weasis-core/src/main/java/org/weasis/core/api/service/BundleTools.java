/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.osgi.framework.BundleContext;
import org.weasis.core.api.media.data.Codec;

/** Bundle-level helpers. Codec list is filled when WP-2 registers {@code Codec} services. */
public final class BundleTools {

  private static volatile BundleContext bundleContext;
  private static final List<Codec> CODECS = new CopyOnWriteArrayList<>();

  private BundleTools() {}

  public static BundleContext getBundleContext() {
    return bundleContext;
  }

  public static void setBundleContext(BundleContext context) {
    bundleContext = context;
  }

  public static WProperties getSystemPreferences() {
    return UICore.getInstance().getSystemPreferences();
  }

  public static List<Codec> getCodecPlugins() {
    return List.copyOf(CODECS);
  }

  public static void registerCodec(Codec codec) {
    if (codec != null && !CODECS.contains(codec)) {
      CODECS.add(codec);
    }
  }

  public static void unregisterCodec(Codec codec) {
    CODECS.remove(codec);
  }
}
