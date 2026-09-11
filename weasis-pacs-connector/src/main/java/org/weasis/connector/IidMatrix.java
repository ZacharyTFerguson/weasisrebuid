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

/**
 * dcm4chee-arc-light embedding: connector 7.x for arc ≤5.30 ({@code ../../}); 8.x Jakarta for 5.31+
 * ({@code ../../../}). Placeholders evolve {@code {}} → named → {@code {{patientID}}}.
 */
public final class IidMatrix {

  public static final String CDB_REQUIRED = "IID URLs must include cdb";
  public static final String TARGET_SELF = "_self";

  public enum ConnectorLine {
    V7,
    V8
  }

  public enum Placeholder {
    BRACES,
    NAMED,
    MUSTACHE
  }

  private IidMatrix() {}

  public static ConnectorLine connectorForArc(String arcVersion) {
    int[] parts = parse(arcVersion);
    if (parts[0] > 5 || (parts[0] == 5 && parts[1] >= 31)) {
      return ConnectorLine.V8;
    }
    return ConnectorLine.V7;
  }

  public static String iidContextPath(ConnectorLine line) {
    return line == ConnectorLine.V8 ? "../../../" : "../../";
  }

  public static Placeholder placeholderForArc(String arcVersion) {
    int[] parts = parse(arcVersion);
    if (compare(parts, 5, 22, 2) >= 0) {
      return Placeholder.MUSTACHE;
    }
    if (compare(parts, 5, 19, 1) >= 0) {
      return Placeholder.NAMED;
    }
    return Placeholder.BRACES;
  }

  static int compare(int[] parts, int major, int minor, int patch) {
    if (parts[0] != major) {
      return Integer.compare(parts[0], major);
    }
    if (parts[1] != minor) {
      return Integer.compare(parts[1], minor);
    }
    return Integer.compare(parts[2], patch);
  }

  public static String patientPlaceholder(Placeholder kind) {
    return switch (kind) {
      case BRACES -> "{}";
      case NAMED -> "IID_PATIENT_URL";
      case MUSTACHE -> "{{patientID}}";
    };
  }

  public static String studyPlaceholder(Placeholder kind) {
    return switch (kind) {
      case BRACES -> "{}";
      case NAMED -> "IID_STUDY_URL";
      case MUSTACHE -> "{{studyUID}}";
    };
  }

  /**
   * dcm4chee view-button href (relative). Always includes {@code cdb}, {@code access_token} when
   * secure, and {@code target=_self}.
   */
  public static String viewButtonHref(
      String arcVersion, String cdb, String accessToken, boolean patient) {
    if (cdb == null || cdb.isBlank()) {
      throw new IllegalArgumentException(CDB_REQUIRED);
    }
    ConnectorLine line = connectorForArc(arcVersion);
    Placeholder ph = placeholderForArc(arcVersion);
    StringBuilder href = new StringBuilder(iidContextPath(line));
    href.append("weasis-pacs-connector/IHEInvokeImageDisplay?");
    href.append("requestType=").append(patient ? "PATIENT" : "STUDY");
    href.append(patient ? "&patientID=" : "&studyUID=");
    href.append(patient ? patientPlaceholder(ph) : studyPlaceholder(ph));
    href.append("&cdb=")
        .append(java.net.URLEncoder.encode(cdb, java.nio.charset.StandardCharsets.UTF_8));
    if (accessToken != null && !accessToken.isBlank()) {
      href.append("&access_token=")
          .append(java.net.URLEncoder.encode(accessToken, java.nio.charset.StandardCharsets.UTF_8));
    }
    href.append("&target=").append(TARGET_SELF);
    return href.toString();
  }

  static int[] parse(String version) {
    if (version == null || version.isBlank()) {
      return new int[] {0, 0, 0};
    }
    String[] bits = version.split("[^0-9]+");
    int[] out = new int[3];
    for (int i = 0; i < 3 && i < bits.length; i++) {
      if (!bits[i].isBlank()) {
        out[i] = Integer.parseInt(bits[i]);
      }
    }
    return out;
  }
}
