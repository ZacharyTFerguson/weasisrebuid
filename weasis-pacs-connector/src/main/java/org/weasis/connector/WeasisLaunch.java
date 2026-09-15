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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.weasis.launcher.Utils;

/**
 * Builds {@code weasis://?} from a ViewerHub display query so the already-installed desktop opens
 * via the documented protocol.
 */
public final class WeasisLaunch {

  private WeasisLaunch() {}

  public static String weasisUri(DisplayQuery query, String defaultCdb) {
    if (query == null) {
      throw new IllegalArgumentException("query");
    }
    String cdb = query.cdb() == null || query.cdb().isBlank() ? defaultCdb : query.cdb();
    if (cdb == null || cdb.isBlank()) {
      throw new IllegalArgumentException(IidMatrix.CDB_REQUIRED);
    }
    StringBuilder inner = new StringBuilder();
    inner.append("$weasis:config cdb=").append(quote(cdb));
    if (query.accessToken() != null && !query.accessToken().isBlank()) {
      inner.append(" auth=").append(quote(query.accessToken()));
    }
    inner.append(" $dicom:rs");
    String archive = query.archive();
    if (archive != null && archive.startsWith("http")) {
      inner.append(" --url ").append(quote(archive));
    }
    appendRs(inner, "patientID", query.patientIds());
    appendRs(inner, "studyUID", query.studyUids());
    appendRs(inner, "seriesUID", query.seriesUids());
    appendRs(inner, "objectUID", query.objectUids());
    appendRs(inner, "accessionNumber", query.accessionNumbers());
    String encoded =
        URLEncoder.encode(inner.toString(), StandardCharsets.UTF_8).replace("+", "%20");
    return Utils.WEASIS_SCHEME + "://?" + encoded;
  }

  static void appendRs(StringBuilder inner, String key, List<String> values) {
    if (values == null) {
      return;
    }
    for (String v : values) {
      if (v == null || v.isBlank()) {
        continue;
      }
      inner.append(" -r ").append(quote(key + "=" + v));
    }
  }

  static String quote(String raw) {
    if (raw == null) {
      return "\"\"";
    }
    if (raw.indexOf(' ') < 0 && raw.indexOf('&') < 0 && raw.indexOf('"') < 0) {
      return raw;
    }
    return "\"" + raw.replace("\"", "\\\"") + "\"";
  }
}
