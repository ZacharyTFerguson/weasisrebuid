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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class View2dLutPaintTest {

  @Test
  void windowLevelPaintsMonochrome2(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("ct.dcm");
    TestCt.write(file.toFile(), 8, 40, 400);
    View2d view = new View2d();
    view.load(file.toFile());
    BufferedImage img = view.getSourceImage();
    assertEquals(8, img.getWidth());
    int center = img.getRaster().getSample(4, 4, 0);
    int black = img.getRaster().getSample(0, 0, 0);
    assertTrue(center > black, "W/L should map higher stored values brighter on MONOCHROME2");
    view.setWindowLevel(1, 40);
    BufferedImage narrow = view.getSourceImage();
    assertEquals(8, narrow.getWidth());
  }

  @Test
  void sequenceItemPresetPaintsLutTableNotWindowCenter() {
    View2d view = new View2d();
    view.load(windowAndLut());
    assertEquals(2, view.getPresets().size());
    assertFalse(view.getActiveVoi().hasVoiLut());
    int linear0 = view.getSourceImage().getRaster().getSample(0, 0, 0);
    view.applyPreset(2);
    assertTrue(view.getActiveVoi().hasVoiLut());
    int lut0 = view.getSourceImage().getRaster().getSample(0, 0, 0);
    assertEquals(255, lut0);
    assertTrue(linear0 < 80);
    view.applyPreset(1);
    assertFalse(view.getActiveVoi().hasVoiLut());
    assertEquals(7.0, view.getWindow(), 1e-9);
  }

  static Attributes windowAndLut() {
    Attributes dcm = new Attributes();
    dcm.setString(Tag.PhotometricInterpretation, VR.CS, "MONOCHROME2");
    dcm.setInt(Tag.Rows, VR.US, 1);
    dcm.setInt(Tag.Columns, VR.US, 8);
    dcm.setInt(Tag.BitsAllocated, VR.US, 8);
    dcm.setInt(Tag.BitsStored, VR.US, 8);
    dcm.setInt(Tag.PixelRepresentation, VR.US, 0);
    dcm.setInt(Tag.PixelData, VR.OW, 0, 1, 2, 3, 4, 5, 6, 7);
    dcm.setDouble(Tag.WindowWidth, VR.DS, 7);
    dcm.setDouble(Tag.WindowCenter, VR.DS, 3.5);
    Sequence seq = dcm.newSequence(Tag.VOILUTSequence, 1);
    Attributes item = new Attributes();
    item.setInt(Tag.LUTDescriptor, VR.US, 8, 0, 8);
    item.setInt(Tag.LUTData, VR.US, 255, 200, 150, 100, 50, 20, 10, 0);
    seq.add(item);
    return dcm;
  }
}
