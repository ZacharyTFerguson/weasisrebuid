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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.weasis.dicom.explorer.wado.ManifestModelBuilder.ArcQuery;
import org.weasis.dicom.explorer.wado.ManifestModelBuilder.Instance;
import org.weasis.dicom.explorer.wado.ManifestModelBuilder.Manifest;
import org.weasis.dicom.explorer.wado.ManifestModelBuilder.Patient;
import org.weasis.dicom.explorer.wado.ManifestModelBuilder.QueryMode;
import org.weasis.dicom.explorer.wado.ManifestModelBuilder.Series;
import org.weasis.dicom.explorer.wado.ManifestModelBuilder.Study;

/** DOM parser for the documented {@code <manifest><arcQuery>} XML (and older {@code wado_query}). */
public class XmlManifestParser {

  public Manifest parse(String xml) throws DownloadException {
    Document document = parseDocument(xml);
    Element root = document.getDocumentElement();
    if (root == null) {
      throw new DownloadException("manifest has no root");
    }
    String rootName = localName(root);
    List<ArcQuery> arcs = new ArrayList<>();
    if ("manifest".equals(rootName)) {
      for (Element arc : children(root, "arcQuery")) {
        arcs.add(readArc(arc));
      }
    } else if ("arcQuery".equals(rootName)
        || "wado_query".equals(rootName)
        || "wado".equals(rootName)
        || "wadoQuery".equals(rootName)) {
      arcs.add(readArc(root));
    } else {
      for (Element arc : children(root, "arcQuery")) {
        arcs.add(readArc(arc));
      }
      if (arcs.isEmpty()) {
        arcs.add(readArc(root));
      }
    }
    return new Manifest(arcs);
  }

  public Document parseDocument(String xml) throws DownloadException {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      factory.setExpandEntityReferences(false);
      factory.setXIncludeAware(false);
      try {
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
      } catch (Exception ignored) {
        // JDK parsers that reject a feature still parse the documented subset.
      }
      return factory
          .newDocumentBuilder()
          .parse(new InputSource(new StringReader(xml == null ? "" : xml)));
    } catch (Exception e) {
      throw new DownloadException("xml manifest", e);
    }
  }

  private static ArcQuery readArc(Element arc) {
    String base =
        ManifestModelBuilder.firstNonBlank(attr(arc, "baseUrl"), attr(arc, "wadoURL"), attr(arc, "wadoUrl"));
    QueryMode mode = ManifestModelBuilder.queryMode(attr(arc, "queryMode"));
    Boolean bulk = ManifestModelBuilder.seriesRetrieveFlag(attr(arc, "seriesRetrieve"));
    boolean onlySop =
        ManifestModelBuilder.truthy(
            ManifestModelBuilder.firstNonBlank(
                attr(arc, "requireOnlySOPInstanceUID"), attr(arc, "requireOnlySopInstanceUID")));
    String extra =
        ManifestModelBuilder.firstNonBlank(
            attr(arc, "additionnalParameters"), attr(arc, "additionalParameters"));
    List<Patient> patients = new ArrayList<>();
    for (Element patient : children(arc, "Patient")) {
      patients.add(readPatient(patient));
    }
    return ManifestModelBuilder.arcQuery(
        attr(arc, "arcId"), base, extra, onlySop, mode, bulk, patients);
  }

  private static Patient readPatient(Element patient) {
    List<Study> studies = new ArrayList<>();
    for (Element study : children(patient, "Study")) {
      studies.add(readStudy(study));
    }
    return new Patient(
        ManifestModelBuilder.firstNonBlank(attr(patient, "PatientID"), attr(patient, "patientID")),
        attr(patient, "PatientName"),
        attr(patient, "PatientBirthDate"),
        attr(patient, "PatientSex"),
        studies);
  }

  private static Study readStudy(Element study) {
    List<Series> series = new ArrayList<>();
    for (Element s : children(study, "Series")) {
      series.add(readSeries(s));
    }
    return new Study(
        ManifestModelBuilder.firstNonBlank(
            attr(study, "StudyInstanceUID"), attr(study, "studyUID")),
        attr(study, "StudyDate"),
        attr(study, "StudyTime"),
        attr(study, "StudyDescription"),
        attr(study, "AccessionNumber"),
        attr(study, "StudyID"),
        series);
  }

  private static Series readSeries(Element series) {
    List<Instance> instances = new ArrayList<>();
    for (Element instance : children(series, "Instance")) {
      instances.add(readInstance(instance));
    }
    return new Series(
        ManifestModelBuilder.firstNonBlank(
            attr(series, "SeriesInstanceUID"), attr(series, "seriesUID")),
        attr(series, "Modality"),
        attr(series, "SeriesDescription"),
        attr(series, "SeriesNumber"),
        ManifestModelBuilder.firstNonBlank(
            attr(series, "DirectDownloadThumbnail"), attr(series, "directDownloadThumbnail")),
        instances);
  }

  private static Instance readInstance(Element instance) {
    return new Instance(
        ManifestModelBuilder.firstNonBlank(
            attr(instance, "SOPInstanceUID"),
            attr(instance, "objectUID"),
            attr(instance, "sopUID")),
        attr(instance, "InstanceNumber"),
        ManifestModelBuilder.firstNonBlank(
            attr(instance, "DirectDownloadFile"), attr(instance, "directDownloadFile")));
  }

  static String attr(Element element, String name) {
    if (element == null || name == null) {
      return "";
    }
    if (element.hasAttribute(name)) {
      return element.getAttribute(name);
    }
    String ns = element.getNamespaceURI();
    if (ns != null && element.hasAttributeNS(ns, name)) {
      return element.getAttributeNS(ns, name);
    }
    return "";
  }

  static String localName(Element element) {
    String local = element.getLocalName();
    if (local != null && !local.isBlank()) {
      return local;
    }
    String tag = element.getTagName();
    int colon = tag.indexOf(':');
    return colon >= 0 ? tag.substring(colon + 1) : tag;
  }

  static List<Element> children(Element parent, String local) {
    List<Element> out = new ArrayList<>();
    if (parent == null) {
      return out;
    }
    NodeList nodes = parent.getChildNodes();
    for (int i = 0; i < nodes.getLength(); i++) {
      Node node = nodes.item(i);
      if (node instanceof Element child && local.equals(localName(child))) {
        out.add(child);
      }
    }
    return out;
  }
}
