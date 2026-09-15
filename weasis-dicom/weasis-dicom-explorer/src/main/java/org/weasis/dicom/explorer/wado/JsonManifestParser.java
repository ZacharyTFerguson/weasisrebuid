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
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.weasis.dicom.explorer.wado.ManifestModelBuilder.ArcQuery;
import org.weasis.dicom.explorer.wado.ManifestModelBuilder.Instance;
import org.weasis.dicom.explorer.wado.ManifestModelBuilder.Manifest;
import org.weasis.dicom.explorer.wado.ManifestModelBuilder.Patient;
import org.weasis.dicom.explorer.wado.ManifestModelBuilder.QueryMode;
import org.weasis.dicom.explorer.wado.ManifestModelBuilder.Series;
import org.weasis.dicom.explorer.wado.ManifestModelBuilder.Study;

/**
 * JSON twin of the 2.5 XML manifest (Weasis 4.7.2+). A node with a single child may be an object or
 * an array.
 */
public class JsonManifestParser {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  public Manifest parse(String json) throws DownloadException {
    try {
      JsonNode root = MAPPER.readTree(json);
      JsonNode manifest = root.has("manifest") ? root.get("manifest") : root;
      List<ArcQuery> arcs = new ArrayList<>();
      JsonNode arcNode = manifest.get("arcQuery");
      if (arcNode == null || arcNode.isNull() || arcNode.isMissingNode()) {
        if (manifest.has("Patient") || manifest.has("baseUrl") || manifest.has("wadoURL")) {
          arcs.add(readArc(manifest));
        }
      } else {
        for (JsonNode arc : asArray(arcNode)) {
          arcs.add(readArc(arc));
        }
      }
      return new Manifest(arcs);
    } catch (DownloadException e) {
      throw e;
    } catch (Exception e) {
      throw new DownloadException("json manifest", e);
    }
  }

  private static ArcQuery readArc(JsonNode arc) {
    String base = text(arc, "baseUrl", "wadoURL", "wadoUrl");
    QueryMode mode = ManifestModelBuilder.queryMode(text(arc, "queryMode"));
    Boolean bulk = ManifestModelBuilder.seriesRetrieveFlag(text(arc, "seriesRetrieve"));
    boolean onlySop =
        ManifestModelBuilder.truthy(text(arc, "requireOnlySOPInstanceUID", "requireOnlySopInstanceUID"));
    String extra = text(arc, "additionnalParameters", "additionalParameters");
    List<Patient> patients = new ArrayList<>();
    for (JsonNode patient : asArray(arc.get("Patient"))) {
      patients.add(readPatient(patient));
    }
    return ManifestModelBuilder.arcQuery(
        text(arc, "arcId"), base, extra, onlySop, mode, bulk, patients);
  }

  private static Patient readPatient(JsonNode patient) {
    List<Study> studies = new ArrayList<>();
    for (JsonNode study : asArray(patient.get("Study"))) {
      studies.add(readStudy(study));
    }
    return new Patient(
        text(patient, "PatientID", "patientID"),
        text(patient, "PatientName"),
        text(patient, "PatientBirthDate"),
        text(patient, "PatientSex"),
        studies);
  }

  private static Study readStudy(JsonNode study) {
    List<Series> series = new ArrayList<>();
    for (JsonNode s : asArray(study.get("Series"))) {
      series.add(readSeries(s));
    }
    return new Study(
        text(study, "StudyInstanceUID", "studyUID"),
        text(study, "StudyDate"),
        text(study, "StudyTime"),
        text(study, "StudyDescription"),
        text(study, "AccessionNumber"),
        text(study, "StudyID"),
        series);
  }

  private static Series readSeries(JsonNode series) {
    List<Instance> instances = new ArrayList<>();
    for (JsonNode instance : asArray(series.get("Instance"))) {
      instances.add(readInstance(instance));
    }
    return new Series(
        text(series, "SeriesInstanceUID", "seriesUID"),
        text(series, "Modality"),
        text(series, "SeriesDescription"),
        text(series, "SeriesNumber"),
        text(series, "DirectDownloadThumbnail", "directDownloadThumbnail"),
        instances);
  }

  private static Instance readInstance(JsonNode instance) {
    return new Instance(
        text(instance, "SOPInstanceUID", "objectUID", "sopUID"),
        text(instance, "InstanceNumber"),
        text(instance, "DirectDownloadFile", "directDownloadFile"));
  }

  static List<JsonNode> asArray(JsonNode node) {
    List<JsonNode> out = new ArrayList<>();
    if (node == null || node.isNull() || node.isMissingNode()) {
      return out;
    }
    if (node.isArray()) {
      node.forEach(out::add);
    } else {
      out.add(node);
    }
    return out;
  }

  static String text(JsonNode node, String... names) {
    if (node == null || names == null) {
      return "";
    }
    for (String name : names) {
      JsonNode value = node.get(name);
      if (value != null && !value.isNull() && !value.isMissingNode() && !value.isContainerNode()) {
        String text = value.asText();
        if (text != null && !text.isBlank()) {
          return text;
        }
      }
    }
    return "";
  }
}
