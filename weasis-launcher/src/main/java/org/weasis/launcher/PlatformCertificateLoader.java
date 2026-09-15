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

import java.security.KeyStore;
import java.security.cert.Certificate;

public final class PlatformCertificateLoader {
  private PlatformCertificateLoader() {}

  public static KeyStore load() {
    try {
      KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
      ks.load(null);
      return ks;
    } catch (Exception e) {
      return null;
    }
  }

  public static int count(KeyStore ks) {
    if (ks == null) {
      return 0;
    }
    int n = 0;
    try {
      var aliases = ks.aliases();
      while (aliases.hasMoreElements()) {
        Certificate c = ks.getCertificate(aliases.nextElement());
        if (c != null) {
          n++;
        }
      }
    } catch (Exception ignored) {
      // ignore
    }
    return n;
  }
}
