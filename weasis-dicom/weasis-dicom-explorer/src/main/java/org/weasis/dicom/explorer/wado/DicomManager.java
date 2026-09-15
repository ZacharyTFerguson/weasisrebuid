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

import org.weasis.core.api.service.UICore;
import org.weasis.core.api.service.WProperties;
import org.weasis.dicom.explorer.wado.ManifestModelBuilder.ArcQuery;

/**
 * Explorer download prefs: {@code weasis.download.immediately}, {@code weasis.manifest.accept},
 * {@code weasis.dicom.web.series.bulk}.
 */
public class DicomManager {

  public static final String PREF_DOWNLOAD_IMMEDIATELY = "weasis.download.immediately";
  public static final String PREF_MANIFEST_ACCEPT = "weasis.manifest.accept";
  public static final String PREF_SERIES_BULK = "weasis.dicom.web.series.bulk";

  public static WProperties prefs() {
    return UICore.getInstance().getSystemPreferences();
  }

  public static boolean downloadImmediately() {
    return propertyBool(PREF_DOWNLOAD_IMMEDIATELY, true);
  }

  /** Default {@code Accept} for {@code dicom:get -w} is XML; {@code json} requests JSON. */
  public static String manifestAccept() {
    String raw = System.getProperty(PREF_MANIFEST_ACCEPT);
    if (raw == null || raw.isBlank()) {
      raw = prefs().getProperty(PREF_MANIFEST_ACCEPT, "xml");
    }
    String v = raw.trim().toLowerCase();
    if (v.startsWith("json") || "application/json".equals(v)) {
      return "application/json";
    }
    return "application/xml";
  }

  public static boolean seriesBulkDefault() {
    return propertyBool(PREF_SERIES_BULK, false);
  }

  public static boolean seriesBulk(ArcQuery arc) {
    if (arc != null && arc.seriesRetrieve() != null) {
      return arc.seriesRetrieve();
    }
    return seriesBulkDefault();
  }

  static boolean propertyBool(String key, boolean fallback) {
    String sys = System.getProperty(key);
    if (sys != null && !sys.isBlank()) {
      return "true".equalsIgnoreCase(sys.trim()) || "1".equals(sys.trim());
    }
    return prefs().getBooleanProperty(key, fallback);
  }
}
