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
import org.weasis.core.api.command.WeasisUri;

/**
 * Builds {@code weasis://} for an already-installed desktop. {@code cdb} is required on IID /
 * ViewerHub launches (native zip URL). {@code cdb} without a value is the installed build — that
 * path is desktop-only, not the PACS view button.
 */
public final class WeasisLaunch {

  public static final String CDB_REQUIRED = IidMatrix.CDB_REQUIRED;

  private WeasisLaunch() {}

  public static String weasisUri(DisplayQuery query, String nativeZipUrl) {
    if (query == null) {
      throw new IllegalArgumentException("query");
    }
    String cdb = query.cdb() == null || query.cdb().isBlank() ? nativeZipUrl : query.cdb();
    if (cdb == null || cdb.isBlank()) {
      throw new IllegalArgumentException(CDB_REQUIRED);
    }
    StringBuilder inner = new StringBuilder();
    inner.append("$weasis:config cdb=").append(cdb);
    if (query.accessToken() != null && !query.accessToken().isBlank()) {
      inner.append(" auth=").append(query.accessToken());
    }
    String archive = query.archive();
    if (archive != null && archive.startsWith("http")) {
      inner.append(" $dicom:rs -u ").append(archive);
    } else {
      inner.append(" $dicom:get -w ").append(cdb);
      if (!cdb.contains("manifest")) {
        inner.append("/manifest");
      }
    }
    String encoded =
        URLEncoder.encode(inner.toString(), StandardCharsets.UTF_8).replace("+", "%20");
    return WeasisUri.SCHEME + "://?" + encoded;
  }
}
