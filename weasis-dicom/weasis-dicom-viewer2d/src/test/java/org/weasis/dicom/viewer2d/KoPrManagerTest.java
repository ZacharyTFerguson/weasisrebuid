/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Point2D;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.core.ui.model.graphic.imp.line.LineGraphic;
import org.weasis.dicom.codec.DicomMime;

class KoPrManagerTest {

  @Test
  void koTogglesAndWritesKeyObject(@TempDir Path dir) throws Exception {
    KOManager ko = new KOManager();
    assertTrue(ko.toggleKeyImage("1.2.3"));
    assertTrue(ko.isKeyImage("1.2.3"));
    Attributes src = new Attributes();
    src.setString(Tag.SOPClassUID, VR.UI, UID.CTImageStorage);
    src.setString(Tag.SOPInstanceUID, VR.UI, "1.2.3");
    src.setString(Tag.StudyInstanceUID, VR.UI, "2.25.1");
    src.setString(Tag.SeriesInstanceUID, VR.UI, "2.25.2");
    Path out = dir.resolve("ko.dcm");
    ko.writeKo(out.toFile(), src);
    assertTrue(Files.size(out) > 0);
    Attributes built = ko.buildKoDocument(src);
    assertEquals(UID.KeyObjectSelectionDocumentStorage, built.getString(Tag.SOPClassUID));
    assertEquals("KO", built.getString(Tag.Modality));
    assertTrue(built.getString(Tag.SOPInstanceUID).startsWith("2.25"));
    assertEquals(DicomMime.KO_DICOM, DicomMime.fromSopClass(UID.KeyObjectSelectionDocumentStorage));
  }

  @Test
  void prWritesGspsFromLine(@TempDir Path dir) throws Exception {
    PRManager pr = new PRManager();
    LineGraphic line = new LineGraphic();
    line.setHandlePoint(0, new Point2D.Double(0, 0));
    line.setHandlePoint(1, new Point2D.Double(10, 0));
    Attributes src = new Attributes();
    src.setString(Tag.SOPClassUID, VR.UI, UID.CTImageStorage);
    src.setString(Tag.SOPInstanceUID, VR.UI, "1.2.3");
    src.setString(Tag.StudyInstanceUID, VR.UI, "2.25.1");
    src.setString(Tag.SeriesInstanceUID, VR.UI, "2.25.2");
    Attributes ps = pr.buildPresentationState(src, List.of(line));
    assertEquals(UID.GrayscaleSoftcopyPresentationStateStorage, ps.getString(Tag.SOPClassUID));
    assertEquals("PR", ps.getString(Tag.Modality));
    Path out = dir.resolve("pr.dcm");
    pr.writePr(out.toFile(), src, List.of(line));
    assertTrue(Files.size(out) > 0);
  }
}
