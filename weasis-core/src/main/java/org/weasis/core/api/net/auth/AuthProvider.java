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

public class AuthProvider {
  private final String name;
  private final String authorizationUrl;
  private final String tokenUrl;

  public AuthProvider(String name, String authorizationUrl, String tokenUrl) {
    this.name = name;
    this.authorizationUrl = authorizationUrl;
    this.tokenUrl = tokenUrl;
  }

  public String getName() {
    return name;
  }

  public String getAuthorizationUrl() {
    return authorizationUrl;
  }

  public String getTokenUrl() {
    return tokenUrl;
  }
}
