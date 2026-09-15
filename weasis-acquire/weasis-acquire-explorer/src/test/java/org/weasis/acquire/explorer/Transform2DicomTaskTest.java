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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.acquire.explorer.dicom.Transform2Dicom;

class Transform2DicomTaskTest {

  @Test
  void dicomizeTaskWritesStill(@TempDir Path dir) throws Exception {
    Path png = dir.resolve("frame.png");
    BufferedImage img = new BufferedImage(4, 4, BufferedImage.TYPE_INT_RGB);
    ImageIO.write(img, "png", png.toFile());
    AcquireManager manager = new AcquireManager();
    manager.setDemographics(new PatientDemographics("SYN^TEST", "ID-1", "", "", ""));
    AcquireImageInfo info = new AcquireImageInfo();
    info.setFile(png);
    manager.addImage(info);
    Path out = dir.resolve("dicom-out");
    assertEquals(1, new DicomizeTask(manager, out, new Properties()).run());
    assertTrue(Files.isRegularFile(out.resolve("frame.dcm")));
  }

  @Test
  void publishAndWorklistPrefs() {
    Properties prefs = new Properties();
    prefs.setProperty("weasis.acquire.dest.host", "localhost");
    prefs.setProperty("weasis.acquire.dest.aet", "DCM4CHEE");
    prefs.setProperty("weasis.acquire.dest.port", "11112");
    AcquireDest.Publication pub = new PublishDicomTask(prefs, true, 2).plan("WEASIS");
    assertEquals(AcquireDest.PublishMode.CSTORE, pub.mode());
    assertTrue(pub.destination().contains("DCM4CHEE"));

    Properties wkl = new Properties();
    wkl.setProperty("weasis.acquire.wkl.host", "mwl.local");
    wkl.setProperty("weasis.acquire.wkl.aet", "MWL");
    wkl.setProperty("weasis.acquire.wkl.port", "104");
    WorklistDialog.WorklistEndpoint ep = WorklistDialog.fromPreferences(wkl);
    assertTrue(ep.configured());
    assertEquals(104, ep.port());
    assertFalse(WorklistDialog.fromPreferences(new Properties()).configured());
  }

  @Test
  void managerLoadsPatientXml() throws Exception {
    AcquireManager m = new AcquireManager();
    m.loadPatientContext("<patient><name>A^B</name><id>PID</id></patient>");
    assertEquals("PID", m.getDemographics().patientId());
    assertEquals(0, new Transform2Dicom().dicomize(m, null));
  }
}
