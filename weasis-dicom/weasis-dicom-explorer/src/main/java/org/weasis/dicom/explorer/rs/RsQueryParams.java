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

/** {@code dicom:rs --url URL -r QUERYPARAMS --query-ext EXT -H header} style parameters. */
public final class RsQueryParams {

  private String url;
  private String rawQueryParams;
  private String queryExt;
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

  public void addRawQueryParams(String rawQueryParams) {
    if (rawQueryParams == null || rawQueryParams.isBlank()) {
      return;
    }
    if (this.rawQueryParams == null || this.rawQueryParams.isBlank()) {
      this.rawQueryParams = rawQueryParams;
      return;
    }
    this.rawQueryParams = this.rawQueryParams + "&" + rawQueryParams;
  }

  public String queryExt() {
    return queryExt;
  }

  public void setQueryExt(String queryExt) {
    this.queryExt = queryExt;
  }

  public Map<String, String> headers() {
    return Map.copyOf(headers);
  }

  public void addHeader(String name, String value) {
    if (name == null || name.isBlank()) {
      return;
    }
    headers.put(name, value == null ? "" : value);
  }

  public void addHeaderLine(String line) {
    if (line == null || line.isBlank()) {
      return;
    }
    int colon = line.indexOf(':');
    if (colon <= 0) {
      addHeader(line.trim(), "");
      return;
    }
    addHeader(line.substring(0, colon).trim(), line.substring(colon + 1).trim());
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
    StringJoiner joiner = new StringJoiner("&");
    for (Map.Entry<String, String> entry : query.entrySet()) {
      String key = URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8);
      String value =
          URLEncoder.encode(
              entry.getValue() == null ? "" : entry.getValue(), StandardCharsets.UTF_8);
      joiner.add(key + "=" + value);
    }
    String extra = normalizeExt(queryExt);
    if (!extra.isEmpty()) {
      joiner.add(extra);
    }
    String q = joiner.toString();
    return q.isEmpty() ? path : path + "?" + q;
  }

  public String buildWadoRsInstanceUrl() {
    Map<String, String> query = parseQueryParams();
    String study = first(query, "studyUID", "StudyInstanceUID");
    String series = first(query, "seriesUID", "SeriesInstanceUID");
    String sop = first(query, "objectUID", "SOPInstanceUID");
    if (study.isEmpty() || series.isEmpty() || sop.isEmpty()) {
      return "";
    }
    String base = url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    return base + "/studies/" + enc(study) + "/series/" + enc(series) + "/instances/" + enc(sop);
  }

  public String buildWadoRsSeriesUrl() {
    Map<String, String> query = parseQueryParams();
    String study = first(query, "studyUID", "StudyInstanceUID");
    String series = first(query, "seriesUID", "SeriesInstanceUID");
    if (study.isEmpty() || series.isEmpty()) {
      return "";
    }
    String base = url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    return base + "/studies/" + enc(study) + "/series/" + enc(series);
  }

  static String first(Map<String, String> query, String... keys) {
    for (String key : keys) {
      String v = query.get(key);
      if (v != null && !v.isBlank()) {
        return v;
      }
    }
    return "";
  }

  static String normalizeExt(String extra) {
    if (extra == null || extra.isBlank()) {
      return "";
    }
    String v = extra.trim();
    if (v.startsWith("&") || v.startsWith("?")) {
      v = v.substring(1);
    }
    if (v.startsWith("&")) {
      v = v.substring(1);
    }
    return v;
  }

  static String enc(String raw) {
    return URLEncoder.encode(raw == null ? "" : raw, StandardCharsets.UTF_8).replace("+", "%20");
  }
}
