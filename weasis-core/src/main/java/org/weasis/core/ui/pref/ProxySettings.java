/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.pref;

import java.util.Properties;
import org.weasis.core.api.service.WProperties;

/**
 * MX-06: Direct vs Manual proxy. Switching to Direct does not clear in-session JVM hosts. Manual
 * dialog has no user/password fields (those are launch {@code -D} only).
 */
public final class ProxySettings {

  public static final String TYPE = "weasis.proxy.type";
  public static final String TYPE_DIRECT = "direct";
  public static final String TYPE_MANUAL = "manual";

  public static final String HTTP_HOST = "weasis.proxy.http.host";
  public static final String HTTP_PORT = "weasis.proxy.http.port";
  public static final String HTTPS_HOST = "weasis.proxy.https.host";
  public static final String HTTPS_PORT = "weasis.proxy.https.port";
  public static final String FTP_HOST = "weasis.proxy.ftp.host";
  public static final String FTP_PORT = "weasis.proxy.ftp.port";
  public static final String SOCKS_HOST = "weasis.proxy.socks.host";
  public static final String SOCKS_PORT = "weasis.proxy.socks.port";
  public static final String EXCEPTIONS = "weasis.proxy.exceptions";

  public static final int DEFAULT_HTTP_PORT = 80;
  public static final int DEFAULT_HTTPS_PORT = 443;
  public static final int DEFAULT_FTP_PORT = 80;
  public static final int DEFAULT_SOCKS_PORT = 1080;

  private ProxySettings() {}

  public static boolean isManual(WProperties prefs) {
    return prefs != null && TYPE_MANUAL.equalsIgnoreCase(prefs.getProperty(TYPE, TYPE_DIRECT));
  }

  /**
   * Writes a JVM networking property only when Manual is on and {@code value} is non-blank. Direct
   * never clears an existing in-session host.
   */
  public static void applyProxyProperty(
      WProperties prefs, Properties jvm, String jvmKey, String value) {
    if (jvm == null || jvmKey == null) {
      return;
    }
    if (isManual(prefs) && value != null && !value.isBlank()) {
      jvm.setProperty(jvmKey, value);
    }
  }

  public static void applyAll(WProperties prefs, Properties jvm) {
    if (prefs == null || jvm == null) {
      return;
    }
    applyProxyProperty(prefs, jvm, "http.proxyHost", prefs.getProperty(HTTP_HOST));
    applyProxyProperty(
        prefs, jvm, "http.proxyPort", portOrEmpty(prefs, HTTP_PORT, DEFAULT_HTTP_PORT));
    applyProxyProperty(prefs, jvm, "https.proxyHost", prefs.getProperty(HTTPS_HOST));
    applyProxyProperty(
        prefs, jvm, "https.proxyPort", portOrEmpty(prefs, HTTPS_PORT, DEFAULT_HTTPS_PORT));
    applyProxyProperty(prefs, jvm, "ftp.proxyHost", prefs.getProperty(FTP_HOST));
    applyProxyProperty(prefs, jvm, "ftp.proxyPort", portOrEmpty(prefs, FTP_PORT, DEFAULT_FTP_PORT));
    applyProxyProperty(prefs, jvm, "socksProxyHost", prefs.getProperty(SOCKS_HOST));
    applyProxyProperty(
        prefs, jvm, "socksProxyPort", portOrEmpty(prefs, SOCKS_PORT, DEFAULT_SOCKS_PORT));
    String exceptions = prefs.getProperty(EXCEPTIONS);
    applyProxyProperty(prefs, jvm, "http.nonProxyHosts", exceptions);
    if (prefs.getProperty(FTP_HOST) != null && !prefs.getProperty(FTP_HOST).isBlank()) {
      applyProxyProperty(prefs, jvm, "ftp.nonProxyHosts", exceptions);
    }
  }

  static String portOrEmpty(WProperties prefs, String key, int defaultPort) {
    if (!isManual(prefs)) {
      return "";
    }
    String raw = prefs.getProperty(key);
    if (raw == null || raw.isBlank()) {
      return Integer.toString(defaultPort);
    }
    return raw;
  }
}
