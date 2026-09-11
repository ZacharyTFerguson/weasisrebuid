/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.mf;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Tagged-equivalent of Weasis XML/JSON launch manifests (subset). */
public final class ManifestParser {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private ManifestParser() {}

  public static List<ManifestSeries> parseJson(String json) throws IOException {
    JsonNode root = MAPPER.readTree(json);
    List<ManifestSeries> out = new ArrayList<>();
    JsonNode series = root.path("series");
    if (series.isArray()) {
      for (JsonNode n : series) {
        out.add(
            new ManifestSeries(
                text(n, "seriesUID"),
                text(n, "studyUID"),
                text(n, "patientID"),
                n.path("instances").isArray() ? n.path("instances").size() : 0));
      }
    }
    return out;
  }

  public static List<ManifestSeries> parseXml(String xml) {
    List<ManifestSeries> out = new ArrayList<>();
    for (String block : xml.split("<Series")) {
      if (!block.contains("seriesUID") && !block.contains("SeriesInstanceUID")) {
        continue;
      }
      String suid = attr(block, "seriesUID");
      if (suid.isEmpty()) {
        suid = attr(block, "SeriesInstanceUID");
      }
      String study = attr(block, "studyUID");
      if (study.isEmpty()) {
        study = attr(block, "StudyInstanceUID");
      }
      out.add(new ManifestSeries(suid, study, attr(block, "patientID"), 0));
    }
    return out;
  }

  static String text(JsonNode n, String field) {
    JsonNode v = n.get(field);
    return v == null || v.isNull() ? "" : v.asText();
  }

  static String attr(String xml, String name) {
    String key = name + "=\"";
    int i = xml.indexOf(key);
    if (i < 0) {
      return "";
    }
    int start = i + key.length();
    int end = xml.indexOf('"', start);
    return end < 0 ? "" : xml.substring(start, end);
  }

  public record ManifestSeries(
      String seriesUid, String studyUid, String patientId, int instanceCount) {}
}
