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

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Locale;

/** DICOM JSON (PS3.18) tag source used when completing a partial DICOMweb manifest. */
public class JsonAttributeSource {

  public static String firstValue(JsonNode dicomJson, String tag) {
    if (dicomJson == null || tag == null || tag.isBlank()) {
      return "";
    }
    JsonNode attr = dicomJson.get(normalizeTag(tag));
    if (attr == null || attr.isNull()) {
      return "";
    }
    JsonNode value = attr.get("Value");
    if (value != null && value.isArray() && value.size() > 0) {
      JsonNode first = value.get(0);
      if (first != null && first.isObject()) {
        JsonNode alphabetic = first.get("Alphabetic");
        if (alphabetic != null && !alphabetic.isNull()) {
          return alphabetic.asText("");
        }
      }
      return first.asText("");
    }
    if (attr.has("InlineBinary")) {
      return attr.get("InlineBinary").asText("");
    }
    return "";
  }

  public static String studyUid(JsonNode dicomJson) {
    return firstValue(dicomJson, "0020000D");
  }

  public static String seriesUid(JsonNode dicomJson) {
    return firstValue(dicomJson, "0020000E");
  }

  public static String sopUid(JsonNode dicomJson) {
    return firstValue(dicomJson, "00080018");
  }

  public static String patientId(JsonNode dicomJson) {
    return firstValue(dicomJson, "00100020");
  }

  static String normalizeTag(String tag) {
    String hex = tag.replaceFirst("^\\(", "").replace(")", "").replace(",", "").replace(" ", "");
    if (hex.startsWith("0x") || hex.startsWith("0X")) {
      hex = hex.substring(2);
    }
    return hex.toUpperCase(Locale.ROOT);
  }
}
