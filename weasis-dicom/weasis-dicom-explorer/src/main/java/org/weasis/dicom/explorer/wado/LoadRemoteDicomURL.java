/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.wado;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;
import org.weasis.dicom.explorer.wado.ManifestModelBuilder.ArcQuery;
import org.weasis.dicom.explorer.wado.ManifestModelBuilder.Instance;
import org.weasis.dicom.explorer.wado.ManifestModelBuilder.QueryMode;
import org.weasis.dicom.explorer.wado.ManifestModelBuilder.Series;
import org.weasis.dicom.explorer.wado.ManifestModelBuilder.Study;

/** WADO-URI, WADO-RS, and DirectDownload URL assembly (no live PACS). */
public class LoadRemoteDicomURL {

  public String retrieveUrl(ArcQuery arc, Study study, Series series, Instance instance) {
    if (instance != null && notBlank(instance.directDownloadFile())) {
      return join(arc == null ? "" : arc.baseUrl(), instance.directDownloadFile());
    }
    QueryMode mode = arc == null ? QueryMode.WADO_URI : arc.queryMode();
    if (mode == QueryMode.DICOM_WEB) {
      if (instance == null) {
        return wadoRsSeries(arc, study, series);
      }
      return wadoRsInstance(arc, study, series, instance);
    }
    return wadoUri(arc, study, series, instance, new ReaderParams());
  }

  public String wadoUri(
      ArcQuery arc, Study study, Series series, Instance instance, ReaderParams params) {
    String base = arc == null ? "" : arc.baseUrl();
    Map<String, String> query = new LinkedHashMap<>();
    query.put("requestType", "WADO");
    boolean onlySop = arc != null && arc.requireOnlySopInstanceUid();
    if (!onlySop && study != null && notBlank(study.studyInstanceUid())) {
      query.put("studyUID", study.studyInstanceUid());
    }
    if (!onlySop && series != null && notBlank(series.seriesInstanceUid())) {
      query.put("seriesUID", series.seriesInstanceUid());
    }
    if (instance != null && notBlank(instance.sopInstanceUid())) {
      query.put("objectUID", instance.sopInstanceUid());
    }
    String contentType =
        params == null || params.contentType() == null || params.contentType().isBlank()
            ? "application/dicom"
            : params.contentType();
    query.put("contentType", contentType);
    String extra = arc == null ? "" : arc.additionalParameters();
    return appendQuery(base, query, extra);
  }

  public String wadoRsInstance(ArcQuery arc, Study study, Series series, Instance instance) {
    String base = stripSlash(arc == null ? "" : arc.baseUrl());
    return base
        + "/studies/"
        + enc(study == null ? "" : study.studyInstanceUid())
        + "/series/"
        + enc(series == null ? "" : series.seriesInstanceUid())
        + "/instances/"
        + enc(instance == null ? "" : instance.sopInstanceUid());
  }

  public String wadoRsSeries(ArcQuery arc, Study study, Series series) {
    String base = stripSlash(arc == null ? "" : arc.baseUrl());
    return base
        + "/studies/"
        + enc(study == null ? "" : study.studyInstanceUid())
        + "/series/"
        + enc(series == null ? "" : series.seriesInstanceUid());
  }

  public String join(String baseUrl, String relative) {
    if (relative == null || relative.isBlank()) {
      return baseUrl == null ? "" : baseUrl;
    }
    if (relative.startsWith("http://") || relative.startsWith("https://")) {
      return relative;
    }
    String base = baseUrl == null ? "" : baseUrl;
    if (relative.startsWith("?") || relative.startsWith("&")) {
      return base + relative;
    }
    if (base.endsWith("/") && relative.startsWith("/")) {
      return base + relative.substring(1);
    }
    if (base.endsWith("/") || relative.startsWith("/")) {
      return base + relative;
    }
    if (base.isEmpty()) {
      return relative;
    }
    return base + "/" + relative;
  }

  static String appendQuery(String base, Map<String, String> query, String extra) {
    String root = base == null ? "" : base;
    StringJoiner joiner = new StringJoiner("&");
    for (Map.Entry<String, String> entry : query.entrySet()) {
      if (entry.getValue() == null || entry.getValue().isBlank()) {
        continue;
      }
      joiner.add(entry.getKey() + "=" + enc(entry.getValue()));
    }
    String q = joiner.toString();
    String extraNorm = extra == null ? "" : extra.trim();
    if (extraNorm.startsWith("?") || extraNorm.startsWith("&")) {
      extraNorm = extraNorm.substring(1);
    }
    if (!extraNorm.isEmpty()) {
      q = q.isEmpty() ? extraNorm : q + "&" + extraNorm;
    }
    if (q.isEmpty()) {
      return root;
    }
    return root + (root.contains("?") ? "&" : "?") + q;
  }

  static String stripSlash(String base) {
    if (base == null || base.isEmpty()) {
      return "";
    }
    return base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
  }

  static String enc(String raw) {
    return URLEncoder.encode(raw == null ? "" : raw, StandardCharsets.UTF_8).replace("+", "%20");
  }

  static boolean notBlank(String s) {
    return s != null && !s.isBlank();
  }
}
