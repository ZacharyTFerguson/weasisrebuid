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

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * ViewerHub {@code GET /display} and {@code GET /display/IHEInvokeImageDisplay} query. Repeatable
 * IDs are lists. {@code viewer} optional → selection rules.
 */
public final class DisplayQuery {

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

  private final Viewer viewer;
  private final String archive;
  private final List<String> patientIds;
  private final List<String> studyUids;
  private final List<String> seriesUids;
  private final List<String> objectUids;
  private final List<String> accessionNumbers;
  private final Integer mostRecentResults;
  private final List<String> modalitiesInStudy;
  private final String containsInDescription;
  private final Instant lowerDateTime;
  private final Instant upperDateTime;
  private final RequestType requestType;
  private final String accessToken;
  private final String cdb;

  DisplayQuery(
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
    this.viewer = viewer;
    this.archive = archive;
    this.patientIds = List.copyOf(patientIds);
    this.studyUids = List.copyOf(studyUids);
    this.seriesUids = List.copyOf(seriesUids);
    this.objectUids = List.copyOf(objectUids);
    this.accessionNumbers = List.copyOf(accessionNumbers);
    this.mostRecentResults = mostRecentResults;
    this.modalitiesInStudy = List.copyOf(modalitiesInStudy);
    this.containsInDescription = containsInDescription;
    this.lowerDateTime = lowerDateTime;
    this.upperDateTime = upperDateTime;
    this.requestType = requestType == null ? RequestType.NONE : requestType;
    this.accessToken = accessToken;
    this.cdb = cdb;
  }

  public Viewer viewer() {
    return viewer;
  }

  public String archive() {
    return archive;
  }

  public List<String> patientIds() {
    return patientIds;
  }

  public List<String> studyUids() {
    return studyUids;
  }

  public List<String> seriesUids() {
    return seriesUids;
  }

  public List<String> objectUids() {
    return objectUids;
  }

  public List<String> accessionNumbers() {
    return accessionNumbers;
  }

  public Integer mostRecentResults() {
    return mostRecentResults;
  }

  public List<String> modalitiesInStudy() {
    return modalitiesInStudy;
  }

  public String containsInDescription() {
    return containsInDescription;
  }

  public Instant lowerDateTime() {
    return lowerDateTime;
  }

  public Instant upperDateTime() {
    return upperDateTime;
  }

  public RequestType requestType() {
    return requestType;
  }

  public String accessToken() {
    return accessToken;
  }

  public String cdb() {
    return cdb;
  }

  public static DisplayQuery parse(Map<String, List<String>> query) {
    Map<String, List<String>> q = query == null ? Map.of() : query;
    Viewer viewer = parseViewer(first(q, "viewer"));
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
        viewer,
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
    for (String pair : q.split("&")) {
      if (pair.isBlank()) {
        continue;
      }
      int eq = pair.indexOf('=');
      String key = eq < 0 ? pair : pair.substring(0, eq);
      String val =
          eq < 0
              ? ""
              : java.net.URLDecoder.decode(
                  pair.substring(eq + 1), java.nio.charset.StandardCharsets.UTF_8);
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
    return Instant.parse(raw.trim());
  }
}
