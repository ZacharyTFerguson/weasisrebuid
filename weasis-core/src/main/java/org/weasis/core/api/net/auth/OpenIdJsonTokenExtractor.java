/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package org.weasis.core.api.net.auth;

public class OpenIdJsonTokenExtractor {
  public OpenIdOAuth2AccessToken extract(String json) {
    if (json == null || json.isBlank()) {
      return new OpenIdOAuth2AccessToken("", "", "");
    }
    return new OpenIdOAuth2AccessToken(
        field(json, "access_token"), field(json, "id_token"), field(json, "refresh_token"));
  }

  static String field(String json, String name) {
    String needle = "\"" + name + "\"";
    int i = json.indexOf(needle);
    if (i < 0) {
      return "";
    }
    int colon = json.indexOf(':', i + needle.length());
    if (colon < 0) {
      return "";
    }
    int q1 = json.indexOf('"', colon + 1);
    if (q1 < 0) {
      return "";
    }
    int q2 = json.indexOf('"', q1 + 1);
    return q2 < 0 ? "" : json.substring(q1 + 1, q2);
  }
}
