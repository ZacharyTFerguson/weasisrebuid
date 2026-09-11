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

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.core.api.image.ShutterOp;
import org.weasis.dicom.codec.DicomMediaIO;

class View2dOverlayShutterLossyTest {

  @Test
  void shutterBlacksOutsideAndLossyBadge(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("ct.dcm");
    TestCt.write(file.toFile(), 8, 40, 400);
    DicomMediaIO io = DicomMediaIO.open(file.toFile());
    Attributes dcm = io.getDataset();
    dcm.setString(Tag.ShutterShape, VR.CS, "RECTANGULAR");
    dcm.setInt(Tag.ShutterLeftVerticalEdge, VR.IS, 2);
    dcm.setInt(Tag.ShutterRightVerticalEdge, VR.IS, 5);
    dcm.setInt(Tag.ShutterUpperHorizontalEdge, VR.IS, 2);
    dcm.setInt(Tag.ShutterLowerHorizontalEdge, VR.IS, 5);
    dcm.setString(Tag.LossyImageCompression, VR.CS, "01");
    View2d view = new View2d();
    view.load(dcm);
    assertEquals("LOSSY", view.getLossyLabel());
    assertEquals(
        Boolean.TRUE, view.getDisplayOpManager().getParamValue("op.shutter", ShutterOp.P_ENABLED));
    BufferedImage img = view.getSourceImage();
    assertEquals(0, img.getRaster().getSample(0, 0, 0));
    assertTrue(img.getRaster().getSample(4, 4, 0) >= 0);
  }

  @Test
  void pixelPaddingExcludedFromAutoWindow() {
    int[] px = {-2048, 10, 20, 30};
    var wl = org.weasis.dicom.codec.utils.LutPipeline.autoWindowExcludingPadding(px, -2048);
    assertTrue(wl.getWindow() < 2048);
  }
}
