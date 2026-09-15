/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.rs;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

/** {@code dicom:rs -u URL -r QUERYPARAMS} style parameters. */
public final class RsQueryParams {

  private String url;
  private String rawQueryParams;
  private final Map<String, String> headers = new LinkedHashMap<>();

  public String url() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String rawQueryParams() {
    return rawQueryParams;
  }

  public void setRawQueryParams(String rawQueryParams) {
    this.rawQueryParams = rawQueryParams;
  }

  public Map<String, String> headers() {
    return Map.copyOf(headers);
  }

  public void addHeader(String name, String value) {
    headers.put(name, value);
  }

  public Map<String, String> parseQueryParams() {
    Map<String, String> map = new LinkedHashMap<>();
    if (rawQueryParams == null || rawQueryParams.isBlank()) {
      return map;
    }
    for (String pair : rawQueryParams.split("&")) {
      int eq = pair.indexOf('=');
      if (eq > 0) {
        map.put(pair.substring(0, eq), pair.substring(eq + 1));
      } else if (!pair.isEmpty()) {
        map.put(pair, "");
      }
    }
    return map;
  }

  public String buildQidoStudiesUrl() {
    Objects.requireNonNull(url, "url");
    String base = url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    String path = base + "/studies";
    Map<String, String> query = parseQueryParams();
    if (query.isEmpty()) {
      return path;
    }
    StringJoiner joiner = new StringJoiner("&");
    for (Map.Entry<String, String> entry : query.entrySet()) {
      String key = URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8);
      String value =
          URLEncoder.encode(
              entry.getValue() == null ? "" : entry.getValue(), StandardCharsets.UTF_8);
      joiner.add(key + "=" + value);
    }
    return path + "?" + joiner;
  }
}
