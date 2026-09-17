/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.codec.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.image.util.WindLevelParameters;

class LutPipelineTest {

  @Test
  void modalityRescaleThenVoiLinear() {
    Attributes dcm = new Attributes();
    dcm.setDouble(Tag.RescaleSlope, VR.DS, 1.0);
    dcm.setDouble(Tag.RescaleIntercept, VR.DS, -1024);
    double hu = LutPipeline.modalityValue(dcm, 1024);
    assertEquals(0.0, hu, 1e-9);
    int mid = LutPipeline.applyVoiLinear(40, 400, 40);
    assertEquals(128, mid, 2);
  }

  @Test
  void autoWindowSkipsPadding() {
    int[] px = new int[] {0, 10, 20, -2048, 30};
    WindLevelParameters wl = LutPipeline.autoWindowExcludingPadding(px, -2048);
    assertEquals(30.0, wl.getWindow(), 1e-9);
    assertEquals(15.0, wl.getLevel(), 1e-9);
  }

  @Test
  void sigmoidAtCenterIsMidGreyUnlikeLinearEdges() {
    int center = LutPipeline.applyVoiSigmoid(40, 400, 40);
    int linearEdge = LutPipeline.applyVoiLinear(40 - 200, 400, 40);
    int sigmoidEdge = LutPipeline.applyVoiSigmoid(40 - 200, 400, 40);
    assertEquals(128, center, 2);
    assertEquals(0, linearEdge);
    assertTrue(sigmoidEdge > 20);
    assertTrue(sigmoidEdge < 80);
  }

  @Test
  void voiLutTableMapsFirstStoredToLutZero() {
    int[] lut = new int[] {255, 128, 0};
    assertEquals(255, LutPipeline.applyVoiLut(10, 10, lut));
    assertEquals(128, LutPipeline.applyVoiLut(11, 10, lut));
    assertEquals(0, LutPipeline.applyVoiLut(12, 10, lut));
    assertEquals(255, LutPipeline.applyVoiLut(0, 10, lut));
    assertEquals(0, LutPipeline.applyVoiLut(99, 10, lut));
  }

  @Test
  void applyVoiPrefersSequenceTableOverLinear() {
    WindLevelParameters p = new WindLevelParameters(400, 40);
    p.setVoiLut(new int[] {10, 20, 30}, 0);
    assertEquals(10, LutPipeline.applyVoi(0, p));
    assertEquals(30, LutPipeline.applyVoi(2, p));
    WindLevelParameters sigmoid = new WindLevelParameters(400, 40);
    sigmoid.setLutShape(LutPipeline.SHAPE_SIGMOID);
    assertEquals(128, LutPipeline.applyVoi(40, sigmoid), 2);
  }
}
