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
import org.weasis.dicom.explorer.wado.ManifestModelBuilder.QueryMode;
import org.weasis.dicom.explorer.wado.ManifestModelBuilder.Series;

/**
 * Partial DICOMweb manifests (4.7.2+): missing Series/Instance levels are filled with QIDO-RS on
 * {@code {baseUrl}/studies/{studyUID}/series}.
 */
public class ManifestCompletion {

  private ManifestCompletion() {}

  public static boolean needsCompletion(QueryMode mode, Series series) {
    return mode == QueryMode.DICOM_WEB && (series == null || !series.instancesListed());
  }

  public static String qidoSeriesUrl(String baseUrl, String studyUid) {
    return strip(baseUrl) + "/studies/" + enc(studyUid) + "/series";
  }

  public static String qidoInstancesUrl(String baseUrl, String studyUid, String seriesUid) {
    return qidoSeriesUrl(baseUrl, studyUid) + "/" + enc(seriesUid) + "/instances";
  }

  static String strip(String base) {
    if (base == null || base.isEmpty()) {
      return "";
    }
    return base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
  }

  static String enc(String raw) {
    return URLEncoder.encode(raw == null ? "" : raw, StandardCharsets.UTF_8).replace("+", "%20");
  }
}
