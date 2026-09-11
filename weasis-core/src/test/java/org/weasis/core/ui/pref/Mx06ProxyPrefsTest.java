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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.service.WProperties;

/** MX-06: Direct vs Manual; Direct does not clear in-session hosts; no user/password fields. */
class Mx06ProxyPrefsTest {

  @Test
  void switchingToDirectDoesNotClearInSessionHosts() {
    WProperties prefs = new WProperties();
    prefs.put(ProxySettings.TYPE, ProxySettings.TYPE_MANUAL);
    prefs.put(ProxySettings.HTTP_HOST, "proxy.example");
    prefs.put(ProxySettings.HTTP_PORT, "8080");
    Properties jvm = new Properties();
    ProxySettings.applyAll(prefs, jvm);
    assertEquals("proxy.example", jvm.getProperty("http.proxyHost"));

    prefs.put(ProxySettings.TYPE, ProxySettings.TYPE_DIRECT);
    ProxySettings.applyAll(prefs, jvm);
    assertEquals("proxy.example", jvm.getProperty("http.proxyHost"));
    assertEquals("8080", jvm.getProperty("http.proxyPort"));
  }

  @Test
  void manualWritesOnlyWhenHostHasText() {
    WProperties prefs = new WProperties();
    prefs.put(ProxySettings.TYPE, ProxySettings.TYPE_MANUAL);
    Properties jvm = new Properties();
    ProxySettings.applyProxyProperty(prefs, jvm, "http.proxyHost", " ");
    assertFalse(jvm.containsKey("http.proxyHost"));
    ProxySettings.applyProxyProperty(prefs, jvm, "http.proxyHost", "h");
    assertEquals("h", jvm.getProperty("http.proxyHost"));
  }

  @Test
  void dialogHasNoUserPasswordFieldsAndClearsLeftoverPwd() {
    WProperties prefs = new WProperties();
    prefs.put(ProxyPrefView.AUTH_PWD_LEFTOVER, "secret");
    ProxyPrefView view = new ProxyPrefView(prefs);
    assertFalse(view.hasUserPasswordFields());
    assertFalse(prefs.containsKey(ProxyPrefView.AUTH_PWD_LEFTOVER));
    assertTrue(view.getDirectButton().isSelected());
  }

  @Test
  void emptyManualPortsUseDocumentedDefaults() {
    WProperties prefs = new WProperties();
    prefs.put(ProxySettings.TYPE, ProxySettings.TYPE_MANUAL);
    prefs.put(ProxySettings.HTTP_HOST, "h");
    Properties jvm = new Properties();
    ProxySettings.applyAll(prefs, jvm);
    assertEquals("80", jvm.getProperty("http.proxyPort"));
    assertEquals("443", jvm.getProperty("https.proxyPort"));
    assertEquals("1080", jvm.getProperty("socksProxyPort"));
  }
}
