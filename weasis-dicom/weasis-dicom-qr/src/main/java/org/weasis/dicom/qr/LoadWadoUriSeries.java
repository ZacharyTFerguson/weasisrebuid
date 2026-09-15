/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.qr;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

/** WADO-URI request builder ({@code requestType=WADO}). */
public final class LoadWadoUriSeries {

  public String buildWadoUri(
      String baseUrl, String studyUid, String seriesUid, String objectUid, String contentType) {
    Objects.requireNonNull(baseUrl, "baseUrl");
    Map<String, String> params = new LinkedHashMap<>();
    params.put("requestType", "WADO");
    if (studyUid != null) {
      params.put("studyUID", studyUid);
    }
    if (seriesUid != null) {
      params.put("seriesUID", seriesUid);
    }
    if (objectUid != null) {
      params.put("objectUID", objectUid);
    }
    if (contentType != null) {
      params.put("contentType", contentType);
    }
    String base = baseUrl.contains("?") ? baseUrl : baseUrl;
    StringJoiner joiner = new StringJoiner("&");
    for (Map.Entry<String, String> entry : params.entrySet()) {
      joiner.add(entry.getKey() + "=" + entry.getValue());
    }
    String separator = base.contains("?") ? "&" : "?";
    return base + separator + joiner;
  }

  public URI buildWadoUri(
      URI base, String studyUid, String seriesUid, String objectUid, String contentType) {
    return URI.create(buildWadoUri(base.toString(), studyUid, seriesUid, objectUid, contentType));
  }
}
