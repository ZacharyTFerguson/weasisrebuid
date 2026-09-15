/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package org.weasis.core.api.net;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public final class HttpUtils {
  private HttpUtils() {}

  public static HttpClient client() {
    return HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build();
  }

  public static InputStream get(URI uri, URLParameters params) throws Exception {
    HttpRequest.Builder b = HttpRequest.newBuilder(uri).timeout(Duration.ofSeconds(30));
    if (params != null) {
      params.getUnmodifiableHeaders().forEach(b::header);
    }
    HttpResponse<InputStream> resp = client().send(b.GET().build(), HttpResponse.BodyHandlers.ofInputStream());
    return resp.body();
  }
}
