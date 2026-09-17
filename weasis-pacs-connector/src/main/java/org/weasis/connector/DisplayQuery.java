/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.connector;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * ViewerHub Launch APIs query (weasis.org). Repeatable {@code patientID} / {@code studyUID} /
 * {@code seriesUID} / {@code objectUID}.
 */
public record DisplayQuery(
    Viewer viewer,
    String archive,
    List<String> patientIds,
    List<String> studyUids,
    List<String> seriesUids,
    List<String> objectUids,
    List<String> accessionNumbers,
    Integer mostRecentResults,
    List<String> modalitiesInStudy,
    String containsInDescription,
    Instant lowerDateTime,
    Instant upperDateTime,
    RequestType requestType,
    String accessToken,
    String cdb) {

  public enum RequestType {
    PATIENT,
    STUDY,
    NONE
  }

  public enum Viewer {
    WEASIS,
    OHIF,
    SLICER,
    MICRODICOM
  }

  public DisplayQuery {
    patientIds = patientIds == null ? List.of() : List.copyOf(patientIds);
    studyUids = studyUids == null ? List.of() : List.copyOf(studyUids);
    seriesUids = seriesUids == null ? List.of() : List.copyOf(seriesUids);
    objectUids = objectUids == null ? List.of() : List.copyOf(objectUids);
    accessionNumbers = accessionNumbers == null ? List.of() : List.copyOf(accessionNumbers);
    modalitiesInStudy = modalitiesInStudy == null ? List.of() : List.copyOf(modalitiesInStudy);
    requestType = requestType == null ? RequestType.NONE : requestType;
  }

  public static DisplayQuery parse(Map<String, List<String>> query) {
    Map<String, List<String>> q = query == null ? Map.of() : query;
    Integer mostRecent = null;
    String mr = first(q, "mostRecentResults");
    if (mr != null && !mr.isBlank()) {
      mostRecent = Integer.parseInt(mr.trim());
    }
    RequestType rt = RequestType.NONE;
    String requestType = first(q, "requestType");
    if (requestType != null && !requestType.isBlank()) {
      rt = RequestType.valueOf(requestType.trim().toUpperCase(Locale.ROOT));
    }
    return new DisplayQuery(
        parseViewer(first(q, "viewer")),
        first(q, "archive"),
        all(q, "patientID"),
        all(q, "studyUID"),
        all(q, "seriesUID"),
        all(q, "objectUID"),
        all(q, "accessionNumber"),
        mostRecent,
        splitCsv(first(q, "modalitiesInStudy")),
        first(q, "containsInDescription"),
        parseInstant(first(q, "lowerDateTime")),
        parseInstant(first(q, "upperDateTime")),
        rt,
        first(q, "access_token"),
        first(q, "cdb"));
  }

  public static Map<String, List<String>> fromQueryString(String raw) {
    Map<String, List<String>> map = new LinkedHashMap<>();
    if (raw == null || raw.isBlank()) {
      return map;
    }
    String q = raw.startsWith("?") ? raw.substring(1) : raw;
    int hash = q.indexOf('#');
    if (hash >= 0) {
      q = q.substring(0, hash);
    }
    for (String pair : q.split("&")) {
      if (pair.isBlank()) {
        continue;
      }
      int eq = pair.indexOf('=');
      String key = eq < 0 ? pair : pair.substring(0, eq);
      String val = eq < 0 ? "" : URLDecoder.decode(pair.substring(eq + 1), StandardCharsets.UTF_8);
      map.computeIfAbsent(key, k -> new ArrayList<>()).add(val);
    }
    return map;
  }

  static Viewer parseViewer(String raw) {
    if (raw == null || raw.isBlank()) {
      return null;
    }
    return Viewer.valueOf(raw.trim().toUpperCase(Locale.ROOT));
  }

  static String first(Map<String, List<String>> q, String key) {
    List<String> vals = q.get(key);
    if (vals == null || vals.isEmpty()) {
      return null;
    }
    return vals.getFirst();
  }

  static List<String> all(Map<String, List<String>> q, String key) {
    List<String> vals = q.get(key);
    return vals == null ? List.of() : List.copyOf(vals);
  }

  static List<String> splitCsv(String raw) {
    if (raw == null || raw.isBlank()) {
      return List.of();
    }
    List<String> out = new ArrayList<>();
    for (String t : raw.split("[,|]")) {
      if (!t.isBlank()) {
        out.add(t.trim());
      }
    }
    return out;
  }

  static Instant parseInstant(String raw) {
    if (raw == null || raw.isBlank()) {
      return null;
    }
    String t = raw.trim();
    if (t.length() == 19 && t.charAt(10) == 'T' && !t.endsWith("Z")) {
      t = t + "Z";
    }
    return Instant.parse(t);
  }
}
