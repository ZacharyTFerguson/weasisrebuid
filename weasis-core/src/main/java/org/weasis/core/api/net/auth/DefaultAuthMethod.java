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

import java.util.Map;

public class DefaultAuthMethod implements AuthMethod {
  private final String id;
  private final String header;
  private final String token;

  public DefaultAuthMethod(String id, String header, String token) {
    this.id = id;
    this.header = header == null ? "Authorization" : header;
    this.token = token == null ? "" : token;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public Map<String, String> authorizationHeaders() {
    if (token.isBlank()) {
      return Map.of();
    }
    String value = token.startsWith("Bearer ") ? token : "Bearer " + token;
    return Map.of(header, value);
  }
}
