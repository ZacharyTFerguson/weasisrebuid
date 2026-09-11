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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * ViewerHub launch API: {@code GET /display} and {@code GET /display/IHEInvokeImageDisplay}.
 * Desktop Weasis works without this sibling; hospital launch needs IID + {@code cdb} + token +
 * {@code _self}.
 */
public final class ViewerHub {

  public static final String DISPLAY = "/display";
  public static final String IID = "/display/IHEInvokeImageDisplay";
  public static final long MANIFEST_TTL_SECONDS = 180L;

  public enum Connector {
    DB,
    DICOM,
    DICOM_WEB
  }

  public record LaunchResult(int status, String location, String error) {}

  private final ViewerSelectionRules rules;
  private final String nativeZipUrl;

  public ViewerHub(ViewerSelectionRules rules, String nativeZipUrl) {
    this.rules = rules == null ? ViewerSelectionRules.defaults() : rules;
    this.nativeZipUrl = nativeZipUrl;
  }

  public LaunchResult handle(String path, Map<String, List<String>> query) {
    String p = path == null ? "" : path;
    if (!DISPLAY.equals(p) && !IID.equals(p)) {
      return new LaunchResult(404, null, "not found");
    }
    DisplayQuery parsed = DisplayQuery.parse(query);
    if (IID.equals(p) && parsed.requestType() == DisplayQuery.RequestType.NONE) {
      return new LaunchResult(400, null, "requestType=PATIENT|STUDY required");
    }
    DisplayQuery.Viewer viewer = parsed.viewer();
    if (viewer == null) {
      viewer = rules.pick(parsed);
    }
    if (viewer != DisplayQuery.Viewer.WEASIS) {
      return new LaunchResult(200, null, "viewer=" + viewer + " (not weasis://)");
    }
    try {
      String uri = WeasisLaunch.weasisUri(parsed, nativeZipUrl);
      return new LaunchResult(302, uri, null);
    } catch (IllegalArgumentException e) {
      return new LaunchResult(400, null, e.getMessage());
    }
  }

  public static boolean containsInsensitive(String haystack, String needle) {
    if (haystack == null || needle == null) {
      return false;
    }
    String h =
        java.text.Normalizer.normalize(haystack, java.text.Normalizer.Form.NFD)
            .replaceAll("\\p{M}+", "")
            .toLowerCase(Locale.ROOT);
    String n =
        java.text.Normalizer.normalize(needle, java.text.Normalizer.Form.NFD)
            .replaceAll("\\p{M}+", "")
            .toLowerCase(Locale.ROOT);
    return h.contains(n);
  }

  public static List<Connector> connectors() {
    List<Connector> list = new ArrayList<>();
    list.add(Connector.DB);
    list.add(Connector.DICOM);
    list.add(Connector.DICOM_WEB);
    return List.copyOf(list);
  }
}
