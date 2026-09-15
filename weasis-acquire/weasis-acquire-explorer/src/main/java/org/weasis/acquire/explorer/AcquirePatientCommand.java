/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.explorer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import org.osgi.service.component.annotations.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Gogo {@code acquire:patient}. Usage: {@code acquire:patient (-x XML | -i inbound Base64/GZip | -s
 * URL-safe Base64/GZip | -u URL)}.
 */
@Component(
    immediate = true,
    service = AcquirePatientCommand.class,
    property = {"osgi.command.scope=acquire", "osgi.command.function=patient"})
public class AcquirePatientCommand {

  private final AcquirePatientStore store;

  public AcquirePatientCommand() {
    this(new AcquirePatientStore());
  }

  public AcquirePatientCommand(AcquirePatientStore store) {
    this.store = store == null ? new AcquirePatientStore() : store;
  }

  public AcquirePatientStore store() {
    return store;
  }

  public String patient() {
    return help();
  }

  public String patient(String flag) {
    if (flag == null || "-?".equals(flag) || "--help".equals(flag)) {
      return help();
    }
    return help();
  }

  public String patient(String flag, String payload) {
    if (flag == null || payload == null) {
      return help();
    }
    try {
      return switch (flag) {
        case "-x", "--xml" -> applyXml(payload);
        case "-i", "--inbound" -> applyCompressed(payload, false);
        case "-s", "--urlsafe" -> applyCompressed(payload, true);
        case "-u", "--url" -> applyUrl(payload);
        default -> help();
      };
    } catch (Exception e) {
      return "error: " + e.getMessage();
    }
  }

  String applyXml(String xml) throws Exception {
    PatientDemographics demo = parseXml(xml);
    store.set(demo);
    return "ok " + demo.patientId();
  }

  String applyCompressed(String encoded, boolean urlSafe) throws Exception {
    Base64.Decoder decoder = urlSafe ? Base64.getUrlDecoder() : Base64.getDecoder();
    byte[] gzip = decoder.decode(encoded);
    String xml = gunzip(gzip);
    return applyXml(xml);
  }

  String applyUrl(String url) throws Exception {
    URI uri = URI.create(url);
    String xml;
    if ("file".equalsIgnoreCase(uri.getScheme())) {
      xml = Files.readString(Path.of(uri));
    } else {
      throw new IOException("only file: URLs in unit tests / local Have");
    }
    return applyXml(xml);
  }

  static PatientDemographics parseXml(String xml) throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
    factory.setExpandEntityReferences(false);
    Document doc =
        factory
            .newDocumentBuilder()
            .parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    Element root = doc.getDocumentElement();
    return new PatientDemographics(
        first(root, "PatientName", "name"),
        first(root, "PatientID", "id"),
        first(root, "PatientBirthDate", "birthDate"),
        first(root, "PatientSex", "sex"),
        first(root, "AccessionNumber", "accession"),
        first(root, "OperatorsName"),
        first(root, "StudyID"),
        nested(root, "IssuerOfAccessionNumberSequence", "LocalNamespaceEntityID"));
  }

  static String first(Element root, String... tags) {
    if (root == null || tags == null) {
      return "";
    }
    for (String tag : tags) {
      String value = text(root, tag);
      if (!value.isEmpty()) {
        return value;
      }
    }
    return "";
  }

  static String nested(Element root, String sequence, String child) {
    if (root == null || sequence == null || child == null) {
      return "";
    }
    var nodes = root.getElementsByTagName(sequence);
    if (nodes.getLength() == 0) {
      return text(root, child);
    }
    return text((Element) nodes.item(0), child);
  }

  static String text(Element root, String tag) {
    if (root == null || tag == null) {
      return "";
    }
    var nodes = root.getElementsByTagName(tag);
    if (nodes.getLength() == 0) {
      return "";
    }
    String v = nodes.item(0).getTextContent();
    return v == null ? "" : v.trim();
  }

  static String gunzip(byte[] gzip) throws IOException {
    try (GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(gzip));
        ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      in.transferTo(out);
      return out.toString(StandardCharsets.UTF_8);
    }
  }

  static String help() {
    return "Import patient demographics\n"
        + "Usage: acquire:patient (-x XML | -i inbound | -s urlsafe | -u URL)\n"
        + "  -x --xml        XML payload\n"
        + "  -i --inbound    Base64 + GZip XML\n"
        + "  -s --urlsafe    URL-safe Base64 + GZip XML\n"
        + "  -u --url        file: URL of XML\n"
        + "  -? --help       show help";
  }
}
