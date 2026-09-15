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

import java.net.http.HttpClient;

public class JavaNetHttpClient {
  private final HttpClient client;

  public JavaNetHttpClient(JavaNetHttpClientConfig config) {
    HttpClient.Builder b = HttpClient.newBuilder();
    if (config != null) {
      b.connectTimeout(config.getTimeout());
      b.followRedirects(
          config.isFollowRedirects() ? HttpClient.Redirect.NORMAL : HttpClient.Redirect.NEVER);
    }
    this.client = b.build();
  }

  public HttpClient getClient() {
    return client;
  }
}
