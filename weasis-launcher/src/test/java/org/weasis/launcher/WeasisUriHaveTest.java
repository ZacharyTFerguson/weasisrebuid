/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.launcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;
import org.dcm4che3.io.DicomOutputStream;
import org.dcm4che3.util.UIDUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.dicom.explorer.DicomCommands;
import org.weasis.dicom.explorer.LocalPersistence;

/**
 * WP-13 Have: OS argv {@code weasis://?commands} is the same Gogo list as the console ({@code
 * dicom:get} / {@code dicom:close} / {@code image:get}), then those lines run {@link
 * DicomCommands}.
 */
class WeasisUriHaveTest {

  @AfterEach
  void resetSharedModel() {
    LocalPersistence.reset();
  }

  @Test
  void encodedRemoteGetUriIsTheSameGogoLineAsConsole() throws Exception {
    String command = "$dicom:get -r \"https://nroduit.github.io/demo-archive/us-palette.dcm\"";
    List<String> ran = new ArrayList<>();
    List<String> results = new ArrayList<>();
    Utils.dispatch(
        Utils.parseLaunch(new String[] {weasisUri(command)}),
        line -> {
          ran.add(line);
          results.add(runDicom(line));
        });
    assertEquals(
        List.of("dicom:get -r \"https://nroduit.github.io/demo-archive/us-palette.dcm\""), ran);
    assertTrue(
        results
            .getFirst()
            .contains("remote https://nroduit.github.io/demo-archive/us-palette.dcm"));
  }

  @Test
  void closeThenGetAndImageGetAreTheSameGogoLinesAsConsole() throws Exception {
    String command =
        "$dicom:close --all $dicom:get -r \"https://example.invalid/a.dcm?foo=$bar\" $image:get -f /tmp/a.png";
    List<String> ran = new ArrayList<>();
    List<String> results = new ArrayList<>();
    Utils.dispatch(
        Utils.parseLaunch(new String[] {weasisUri(command)}),
        line -> {
          ran.add(line);
          if (line.startsWith("dicom:")) {
            results.add(runDicom(line));
          }
        });
    assertEquals(
        List.of(
            "dicom:close --all",
            "dicom:get -r \"https://example.invalid/a.dcm?foo=$bar\"",
            "image:get -f /tmp/a.png"),
        ran);
    assertEquals("close-all", results.get(0));
    assertTrue(results.get(1).contains("remote https://example.invalid/a.dcm?foo=$bar"));
  }

  @Test
  void weasisUriLocalGetLoadsSharedDicomModel(@TempDir Path dir) throws Exception {
    LocalPersistence.reset();
    File ct = dir.resolve("ct.dcm").toFile();
    writeCt(ct);
    String command = "$dicom:get -l \"" + ct.getAbsolutePath() + "\"";
    List<String> ran = new ArrayList<>();
    Utils.dispatch(
        Utils.parseLaunch(new String[] {weasisUri(command)}),
        line -> {
          ran.add(line);
          new DicomCommands().get(Utils.commandArgs(line));
        });
    assertEquals(1, ran.size());
    assertTrue(ran.getFirst().startsWith("dicom:get -l "));
    assertEquals(1, LocalPersistence.getDicomModel().getInstances().size());
  }

  private static String runDicom(String gogoLine) {
    DicomCommands dicom = new DicomCommands();
    String[] args = Utils.commandArgs(gogoLine);
    return switch (Utils.commandFunction(gogoLine)) {
      case "close" -> dicom.close(args);
      case "rs" -> dicom.rs(args);
      default -> dicom.get(args);
    };
  }

  private static String weasisUri(String dollarCommands) {
    String encoded = URLEncoder.encode(dollarCommands, StandardCharsets.UTF_8).replace("+", "%20");
    return "weasis://?" + encoded;
  }

  private static void writeCt(File dest) throws Exception {
    String sop = UIDUtils.createUID("2.25");
    Attributes fmi = new Attributes();
    fmi.setBytes(Tag.FileMetaInformationVersion, VR.OB, new byte[] {0, 1});
    fmi.setString(Tag.MediaStorageSOPClassUID, VR.UI, UID.CTImageStorage);
    fmi.setString(Tag.MediaStorageSOPInstanceUID, VR.UI, sop);
    fmi.setString(Tag.TransferSyntaxUID, VR.UI, UID.ExplicitVRLittleEndian);
    fmi.setString(Tag.ImplementationClassUID, VR.UI, "2.25.1918");
    Attributes dcm = new Attributes();
    dcm.setString(Tag.SOPClassUID, VR.UI, UID.CTImageStorage);
    dcm.setString(Tag.SOPInstanceUID, VR.UI, sop);
    dcm.setString(Tag.StudyInstanceUID, VR.UI, UIDUtils.createUID("2.25"));
    dcm.setString(Tag.SeriesInstanceUID, VR.UI, UIDUtils.createUID("2.25"));
    dcm.setString(Tag.Modality, VR.CS, "CT");
    dcm.setString(Tag.PhotometricInterpretation, VR.CS, "MONOCHROME2");
    dcm.setInt(Tag.SamplesPerPixel, VR.US, 1);
    dcm.setInt(Tag.Rows, VR.US, 8);
    dcm.setInt(Tag.Columns, VR.US, 8);
    dcm.setInt(Tag.BitsAllocated, VR.US, 16);
    dcm.setInt(Tag.BitsStored, VR.US, 16);
    dcm.setInt(Tag.HighBit, VR.US, 15);
    dcm.setInt(Tag.PixelRepresentation, VR.US, 1);
    dcm.setString(Tag.PatientName, VR.PN, "SYNTHETIC^CT");
    dcm.setString(Tag.PatientID, VR.LO, "SYN-CT-0001");
    dcm.setInt(Tag.PixelData, VR.OW, new int[64]);
    try (DicomOutputStream out = new DicomOutputStream(dest)) {
      out.writeDataset(fmi, dcm);
    }
  }
}
