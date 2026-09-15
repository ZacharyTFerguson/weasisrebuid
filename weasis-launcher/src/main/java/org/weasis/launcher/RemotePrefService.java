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

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Properties;

public class RemotePrefService {
  private final String baseUrl;

  public RemotePrefService(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public String getBaseUrl() {
    return baseUrl;
  }

  public Properties read() {
    Properties p = new Properties();
    if (baseUrl == null || baseUrl.isBlank()) {
      return p;
    }
    try {
      HttpRequest req =
          HttpRequest.newBuilder(URI.create(baseUrl))
              .timeout(Duration.ofSeconds(10))
              .GET()
              .build();
      HttpResponse<String> resp =
          HttpClient.newHttpClient().send(req, HttpResponse.BodyHandlers.ofString());
      p.setProperty("status", Integer.toString(resp.statusCode()));
      p.setProperty("body", resp.body() == null ? "" : resp.body());
    } catch (Exception ignored) {
      // ViewerHub optional
    }
    return p;
  }
}
