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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.dicom.explorer.DicomCommands;
import org.weasis.dicom.explorer.wado.ManifestModelBuilder.ArcQuery;
import org.weasis.dicom.explorer.wado.ManifestModelBuilder.Manifest;
import org.weasis.dicom.explorer.wado.ManifestModelBuilder.QueryMode;
import org.weasis.dicom.explorer.wado.ManifestModelBuilder.Series;
import org.weasis.dicom.explorer.wado.ManifestModelBuilder.Study;

class WadoManifestHaveTest {

  private static final String XML =
      """
      <?xml version="1.0" encoding="UTF-8" ?>
      <manifest xmlns="http://www.weasis.org/xsd/2.5">
        <arcQuery additionnalParameters="&amp;transferSyntax=*" arcId="1001"
            baseUrl="http://archive.example/wado" requireOnlySOPInstanceUID="false">
          <Patient PatientID="SYN-1" PatientName="ANON" PatientSex="O">
            <Study StudyInstanceUID="2.25.11" StudyDate="20200101">
              <Series Modality="CT" SeriesInstanceUID="2.25.10" SeriesNumber="1">
                <Instance InstanceNumber="1" SOPInstanceUID="2.25.100"/>
                <Instance InstanceNumber="2" SOPInstanceUID="2.25.101"/>
              </Series>
              <Series Modality="SR" SeriesInstanceUID="2.25.20" SeriesNumber="99">
                <Instance InstanceNumber="1" SOPInstanceUID="2.25.200"/>
              </Series>
            </Study>
          </Patient>
        </arcQuery>
      </manifest>
      """;

  private static final String JSON =
      """
      {
        "manifest": {
          "arcQuery": {
            "arcId": "1001",
            "baseUrl": "https://archive.example/dicom-web",
            "queryMode": "DICOM_WEB",
            "Patient": {
              "PatientID": "SYN-1",
              "Study": {
                "StudyInstanceUID": "2.25.11",
                "Series": {
                  "SeriesInstanceUID": "2.25.10",
                  "Modality": "MR",
                  "Instance": [
                    { "SOPInstanceUID": "2.25.100", "InstanceNumber": "1" }
                  ]
                }
              }
            }
          }
        }
      }
      """;

  @Test
  void xmlManifestBuildsWadoUri() throws Exception {
    Manifest manifest = new XmlManifestParser().parse(XML);
    assertEquals(1, manifest.arcQueries().size());
    ArcQuery arc = manifest.arcQueries().getFirst();
    assertEquals("http://archive.example/wado", arc.baseUrl());
    assertEquals("&transferSyntax=*", arc.additionalParameters());
    assertEquals(2, manifest.seriesCount());
    assertEquals(3, manifest.instanceCount());
    List<LoadSeries> jobs = new DownloadManager().plan(manifest);
    assertEquals(2, jobs.size());
    assertEquals("CT", jobs.getFirst().series().modality());
    assertEquals("SR", jobs.getLast().series().modality());
    String uri = jobs.getFirst().instanceUrls().getFirst();
    assertTrue(uri.contains("requestType=WADO"));
    assertTrue(uri.contains("studyUID=2.25.11"));
    assertTrue(uri.contains("objectUID=2.25.100"));
    assertTrue(uri.contains("contentType=application%2Fdicom"));
    assertTrue(uri.contains("transferSyntax=*"));
  }

  @Test
  void jsonObjectOrArrayAndDicomWebWadoRs() throws Exception {
    Manifest manifest = ManifestModelBuilder.parseDocument(JSON);
    assertEquals(QueryMode.DICOM_WEB, manifest.arcQueries().getFirst().queryMode());
    LoadSeries job = new DownloadManager().plan(manifest).getFirst();
    assertFalse(job.bulk());
    assertTrue(
        job.instanceUrls()
            .getFirst()
            .endsWith("/studies/2.25.11/series/2.25.10/instances/2.25.100"));
  }

  @Test
  void sniffsJsonFromBrace() throws Exception {
    Manifest json = ManifestModelBuilder.parseDocument(" {\"manifest\":{\"arcQuery\":[]}} ");
    assertTrue(json.arcQueries().isEmpty());
  }

  @Test
  void gzipXmlRoundTrip() throws Exception {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    try (GZIPOutputStream gzip = new GZIPOutputStream(bos)) {
      gzip.write(XML.getBytes(StandardCharsets.UTF_8));
    }
    byte[] gz = bos.toByteArray();
    assertTrue(LoadRemoteDicomManifest.isGzip(gz));
    Manifest manifest = new LoadRemoteDicomManifest().parseBytes(gz);
    assertEquals(3, manifest.instanceCount());
  }

  @Test
  void directDownloadConcatenatesWadoUrl(@TempDir Path dir) throws Exception {
    String xml =
        """
        <wado_query wadoURL="https://cdn.example/Lumbar/">
          <Patient PatientID="SYN-1">
            <Study StudyInstanceUID="2.25.11">
              <Series SeriesInstanceUID="2.25.10" DirectDownloadThumbnail="thumb.jpg">
                <Instance SOPInstanceUID="2.25.100" DirectDownloadFile="img.dcm"/>
              </Series>
            </Study>
          </Patient>
        </wado_query>
        """;
    Path file = dir.resolve("mf.xml");
    Files.writeString(file, xml);
    Manifest manifest = new LoadRemoteDicomManifest().parseFile(file);
    LoadSeries job = new DownloadManager().plan(manifest).getFirst();
    assertEquals("https://cdn.example/Lumbar/img.dcm", job.instanceUrls().getFirst());
    String thumb =
        new ThumbnailManager()
            .thumbnailUrl(job.arc(), job.study(), job.series());
    assertEquals("https://cdn.example/Lumbar/thumb.jpg", thumb);
    String gogo = new DicomCommands().get("-w", file.toString());
    assertTrue(gogo.contains("series=1"));
    assertTrue(gogo.contains("instances=1"));
  }

  @Test
  void partialDicomWebSeriesBulkAndQidoCompletion() throws Exception {
    String xml =
        """
        <manifest xmlns="http://www.weasis.org/xsd/2.5">
          <arcQuery arcId="1" baseUrl="https://pacs.example/dicom-web" queryMode="DICOM_WEB"
              seriesRetrieve="true">
            <Patient PatientID="SYN-1">
              <Study StudyInstanceUID="2.25.11">
                <Series SeriesInstanceUID="2.25.10" Modality="CT"/>
              </Study>
            </Patient>
          </arcQuery>
        </manifest>
        """;
    LoadSeries bulk = new DownloadManager().plan(new XmlManifestParser().parse(xml)).getFirst();
    assertTrue(bulk.bulk());
    assertEquals(
        "https://pacs.example/dicom-web/studies/2.25.11/series/2.25.10", bulk.bulkUrl());
    String enumerated =
        xml.replace("seriesRetrieve=\"true\"", "seriesRetrieve=\"false\"");
    LoadSeries qido =
        new DownloadManager().plan(new XmlManifestParser().parse(enumerated)).getFirst();
    assertFalse(qido.bulk());
    assertTrue(qido.completionUrl().endsWith("/studies/2.25.11/series/2.25.10/instances"));
    assertTrue(ManifestCompletion.needsCompletion(QueryMode.DICOM_WEB, qido.series()));
  }

  @Test
  void downloadCapsNeverExceedMx10Mx11() throws Exception {
    StringBuilder xml = new StringBuilder();
    xml.append("<manifest xmlns=\"http://www.weasis.org/xsd/2.5\"><arcQuery baseUrl=\"http://w\">");
    xml.append("<Patient PatientID=\"SYN\"><Study StudyInstanceUID=\"2.25.1\">");
    for (int s = 0; s < 8; s++) {
      xml.append("<Series SeriesInstanceUID=\"2.25.s")
          .append(s)
          .append("\" Modality=\"CT\" SeriesNumber=\"")
          .append(s)
          .append("\">");
      for (int i = 0; i < 8; i++) {
        xml.append("<Instance SOPInstanceUID=\"2.25.s")
            .append(s)
            .append('.')
            .append(i)
            .append("\" InstanceNumber=\"")
            .append(i)
            .append("\"/>");
      }
      xml.append("</Series>");
    }
    xml.append("</Study></Patient></arcQuery></manifest>");
    SeriesDownloadManager slots = new SeriesDownloadManager(3, 4);
    DownloadManager mgr = new DownloadManager(slots, new LoadRemoteDicomURL());
    List<LoadSeries> jobs = mgr.plan(new XmlManifestParser().parse(xml.toString()));
    assertEquals(8, jobs.size());
    List<String> urls = mgr.downloadAll(jobs);
    assertEquals(64, urls.size());
    assertTrue(slots.peakConcurrentSeries() <= 3);
    assertTrue(slots.peakConcurrentImages() <= 4);
    assertTrue(slots.peakConcurrentSeries() >= 1);
    assertTrue(slots.peakConcurrentImages() >= 1);
  }

  @Test
  void jsonAttributeSourceReadsDicomJson() throws Exception {
    var node =
        new ObjectMapper()
            .readTree(
                "{\"0020000D\":{\"vr\":\"UI\",\"Value\":[\"2.25.11\"]},\"00100020\":{\"vr\":\"LO\",\"Value\":[\"SYN-1\"]}}");
    assertEquals("2.25.11", JsonAttributeSource.studyUid(node));
    assertEquals("SYN-1", JsonAttributeSource.patientId(node));
  }

  @Test
  void requireOnlySopOmitsStudySeries() throws Exception {
    String xml =
        """
        <arcQuery baseUrl="http://wado" requireOnlySOPInstanceUID="true">
          <Patient PatientID="SYN">
            <Study StudyInstanceUID="2.25.11">
              <Series SeriesInstanceUID="2.25.10">
                <Instance SOPInstanceUID="2.25.100"/>
              </Series>
            </Study>
          </Patient>
        </arcQuery>
        """;
    LoadSeries job = new DownloadManager().plan(new XmlManifestParser().parse(xml)).getFirst();
    String uri = job.instanceUrls().getFirst();
    assertTrue(uri.contains("objectUID=2.25.100"));
    assertFalse(uri.contains("studyUID="));
    assertFalse(uri.contains("seriesUID="));
  }

  @Test
  void progressMonitorAndAcceptHeader() {
    DicomSeriesProgressMonitor monitor = new DicomSeriesProgressMonitor();
    monitor.setTotal(4);
    monitor.increment();
    monitor.increment();
    assertEquals(0.5, monitor.ratio(), 0.01);
    assertEquals("application/xml", DicomManager.manifestAccept());
    Series series = new Series("2.25.10", "CT", "desc", "1", "", List.of());
    Study study = new Study("2.25.11", "", "", "", "", "", List.of(series));
    ArcQuery arc =
        ManifestModelBuilder.arcQuery(
            "1",
            "https://pacs.example/dicom-web",
            "",
            false,
            QueryMode.DICOM_WEB,
            true,
            List.of());
    String thumb = new ThumbnailManager().thumbnailUrl(arc, study, series);
    assertTrue(thumb.endsWith("/series/2.25.10/thumbnail"));
  }
}
