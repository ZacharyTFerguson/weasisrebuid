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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * DICOMweb QIDO-RS helpers. QIDO {@code Accept} is always {@code application/dicom+json}; a
 * configured retrieve Accept applies to WADO/STOW, not QIDO.
 */
public final class RsQuery {

  public static final String QIDO_ACCEPT = "application/dicom+json";

  public enum ServiceType {
    ALL_RESTFUL,
    QIDO_RS,
    STOW_RS,
    WADO_URI,
    WADO_RS
  }

  private ServiceType serviceType = ServiceType.ALL_RESTFUL;
  private String retrieveAccept;

  public Map<String, String> qidoHeaders() {
    Map<String, String> headers = new LinkedHashMap<>();
    headers.put("Accept", QIDO_ACCEPT);
    return headers;
  }

  public Map<String, String> retrieveHeaders() {
    Map<String, String> headers = new LinkedHashMap<>();
    if (retrieveAccept != null && !retrieveAccept.isBlank()) {
      headers.put("Accept", retrieveAccept);
    }
    return headers;
  }

  public void setRetrieveAccept(String retrieveAccept) {
    this.retrieveAccept = retrieveAccept;
  }

  public ServiceType serviceType() {
    return serviceType;
  }

  public void setServiceType(ServiceType serviceType) {
    this.serviceType = serviceType == null ? ServiceType.ALL_RESTFUL : serviceType;
  }

  /** Build {@code {base}/studies?PatientID=...} style QIDO URL (no PHI in tests). */
  public String buildStudiesUrl(String baseUrl, Map<String, String> queryParams) {
    Objects.requireNonNull(baseUrl, "baseUrl");
    String base = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    String path = base + "/studies";
    if (queryParams == null || queryParams.isEmpty()) {
      return path;
    }
    StringJoiner joiner = new StringJoiner("&");
    for (Map.Entry<String, String> entry : queryParams.entrySet()) {
      String key = URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8);
      String value =
          URLEncoder.encode(
              entry.getValue() == null ? "" : entry.getValue(), StandardCharsets.UTF_8);
      joiner.add(key + "=" + value);
    }
    return path + "?" + joiner;
  }

  public URI buildStudiesUri(String baseUrl, Map<String, String> queryParams) {
    return URI.create(buildStudiesUrl(baseUrl, queryParams));
  }

  public String buildWadoRsSeriesUrl(String baseUrl, String studyUid, String seriesUid) {
    String base = strip(baseUrl);
    return base + "/studies/" + enc(studyUid) + "/series/" + enc(seriesUid);
  }

  public String buildWadoRsInstanceUrl(
      String baseUrl, String studyUid, String seriesUid, String sopUid) {
    return buildWadoRsSeriesUrl(baseUrl, studyUid, seriesUid) + "/instances/" + enc(sopUid);
  }

  static String strip(String baseUrl) {
    Objects.requireNonNull(baseUrl, "baseUrl");
    return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
  }

  static String enc(String raw) {
    return URLEncoder.encode(raw == null ? "" : raw, StandardCharsets.UTF_8).replace("+", "%20");
  }
}
