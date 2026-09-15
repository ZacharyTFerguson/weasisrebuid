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

import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * In-memory Weasis 4.7.2+ launch manifest ({@code xmlns=http://www.weasis.org/xsd/2.5}). Node and
 * attribute names follow the public integration document, not a copied XSD.
 */
public class ManifestModelBuilder {

  public enum QueryMode {
    WADO_URI,
    DICOM_WEB
  }

  public record Manifest(List<ArcQuery> arcQueries) {
    public Manifest {
      arcQueries = List.copyOf(arcQueries == null ? List.of() : arcQueries);
    }

    public int seriesCount() {
      int n = 0;
      for (ArcQuery arc : arcQueries) {
        n += arc.seriesCount();
      }
      return n;
    }

    public int instanceCount() {
      int n = 0;
      for (ArcQuery arc : arcQueries) {
        n += arc.instanceCount();
      }
      return n;
    }
  }

  public record ArcQuery(
      String arcId,
      String baseUrl,
      String additionalParameters,
      boolean requireOnlySopInstanceUid,
      QueryMode queryMode,
      Boolean seriesRetrieve,
      List<Patient> patients) {
    public ArcQuery {
      patients = List.copyOf(patients == null ? List.of() : patients);
      queryMode = queryMode == null ? QueryMode.WADO_URI : queryMode;
    }

    public int seriesCount() {
      int n = 0;
      for (Patient patient : patients) {
        n += patient.seriesCount();
      }
      return n;
    }

    public int instanceCount() {
      int n = 0;
      for (Patient patient : patients) {
        n += patient.instanceCount();
      }
      return n;
    }
  }

  public record Patient(
      String patientId,
      String patientName,
      String patientBirthDate,
      String patientSex,
      List<Study> studies) {
    public Patient {
      studies = List.copyOf(studies == null ? List.of() : studies);
    }

    public int seriesCount() {
      int n = 0;
      for (Study study : studies) {
        n += study.series().size();
      }
      return n;
    }

    public int instanceCount() {
      int n = 0;
      for (Study study : studies) {
        n += study.instanceCount();
      }
      return n;
    }
  }

  public record Study(
      String studyInstanceUid,
      String studyDate,
      String studyTime,
      String studyDescription,
      String accessionNumber,
      String studyId,
      List<Series> series) {
    public Study {
      series = List.copyOf(series == null ? List.of() : series);
    }

    public int instanceCount() {
      int n = 0;
      for (Series s : series) {
        n += s.instances().size();
      }
      return n;
    }
  }

  public record Series(
      String seriesInstanceUid,
      String modality,
      String seriesDescription,
      String seriesNumber,
      String directDownloadThumbnail,
      List<Instance> instances) {
    public Series {
      instances = List.copyOf(instances == null ? List.of() : instances);
    }

    public boolean instancesListed() {
      return !instances.isEmpty();
    }
  }

  public record Instance(String sopInstanceUid, String instanceNumber, String directDownloadFile) {}

  private ManifestModelBuilder() {}

  /** Docs: a document starting with {@code {} is JSON, otherwise XML. */
  public static Manifest parseDocument(String text) throws DownloadException {
    if (text == null) {
      throw new DownloadException("empty manifest");
    }
    String trimmed = stripBom(text).trim();
    if (trimmed.isEmpty()) {
      throw new DownloadException("empty manifest");
    }
    if (trimmed.charAt(0) == '{') {
      return new JsonManifestParser().parse(trimmed);
    }
    return new XmlManifestParser().parse(trimmed);
  }

  static String stripBom(String text) {
    if (text != null && !text.isEmpty() && text.charAt(0) == '\uFEFF') {
      return text.substring(1);
    }
    return text;
  }

  static QueryMode queryMode(String raw) {
    if (raw == null || raw.isBlank()) {
      return QueryMode.WADO_URI;
    }
    String n = raw.trim().toUpperCase(Locale.ROOT).replace('-', '_');
    if (n.contains("DICOM_WEB")
        || n.equals("DICOMWEB")
        || n.equals("QIDO")
        || n.equals("WADO_RS")) {
      return QueryMode.DICOM_WEB;
    }
    return QueryMode.WADO_URI;
  }

  static Boolean seriesRetrieveFlag(String raw) {
    if (raw == null || raw.isBlank()) {
      return null;
    }
    if ("true".equalsIgnoreCase(raw.trim()) || "1".equals(raw.trim())) {
      return Boolean.TRUE;
    }
    if ("false".equalsIgnoreCase(raw.trim()) || "0".equals(raw.trim())) {
      return Boolean.FALSE;
    }
    return null;
  }

  static boolean truthy(String raw) {
    return raw != null && ("true".equalsIgnoreCase(raw.trim()) || "1".equals(raw.trim()));
  }

  static String firstNonBlank(String... values) {
    if (values == null) {
      return "";
    }
    for (String v : values) {
      if (v != null && !v.isBlank()) {
        return v;
      }
    }
    return "";
  }

  static ArcQuery arcQuery(
      String arcId,
      String baseUrl,
      String additionalParameters,
      boolean requireOnlySop,
      QueryMode mode,
      Boolean seriesRetrieve,
      List<Patient> patients) {
    return new ArcQuery(
        Objects.toString(arcId, ""),
        Objects.toString(baseUrl, ""),
        Objects.toString(additionalParameters, ""),
        requireOnlySop,
        mode,
        seriesRetrieve,
        patients);
  }
}
