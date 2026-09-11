/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.fusion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.weasis.dicom.viewer2d.mpr.MprContainer;
import org.weasis.dicom.viewer2d.mpr.MprController;
import org.weasis.dicom.viewer2d.mpr.MprView;
import org.weasis.dicom.viewer2d.mpr.MprVolume;
import org.weasis.dicom.viewer2d.mpr.Plane;
import org.weasis.dicom.viewer2d.mpr.Volume;

class FusionControllerTest {

  @Test
  void targetViewsBroadcastsToAllMprPanes() {
    MprController mpr = new MprController(new MprVolume(8, 1, 1, 1), new Volume(8));
    MprContainer container = new MprContainer(mpr);
    FusionController fusion = new FusionController();
    MprView selected = container.panes().get(1);
    assertEquals(3, fusion.targetViews(selected, container).size());
    fusion.setLut(selected, container, "hot");
    fusion.setOpacity(selected, container, 0.4);
    fusion.setSeries(selected, container, "2.25.1");
    fusion.setEnabled(selected, container, true);
    for (MprView pane : container.panes()) {
      FusionOp op = (FusionOp) pane.fusionOp();
      assertTrue(op.isEnabled());
      assertEquals("hot", op.getParam(FusionOp.LUT));
      assertEquals("2.25.1", op.getParam(FusionOp.SERIES));
      assertEquals(0.4, op.opacity(), 1e-9);
    }
    assertNotSame(container.panes().get(0).fusionOp(), container.panes().get(1).fusionOp());
    fusion.resetDisplay(selected, container);
    for (MprView pane : container.panes()) {
      assertFalse(((FusionOp) pane.fusionOp()).isEnabled());
    }
  }

  @Test
  void twoDStaysPerView() {
    FusionController fusion = new FusionController();
    MprView only = new MprView(Plane.AXIAL);
    assertEquals(1, fusion.targetViews(only, null).size());
  }

  @Test
  void inheritSnapshotIsNotLiveLinked() {
    MprController mpr = new MprController(new MprVolume(8, 1, 1, 1), new Volume(8));
    MprContainer container = new MprContainer(mpr);
    FusionOp twoD = new FusionOp();
    twoD.setParam(FusionOp.LUT, "pet");
    twoD.setParam(FusionOp.OPACITY, 0.5);
    FusionState snap = new FusionState(twoD, "pet", 0.5, 1.0);
    new FusionController().applyInherited(container, snap);
    twoD.setParam(FusionOp.LUT, "changed");
    FusionOp plane = (FusionOp) container.panes().get(0).fusionOp();
    assertEquals("pet", plane.getParam(FusionOp.LUT));
  }

  @Test
  void eligibilityAndOriginalPetStats() {
    assertTrue(FusionEligibility.enabled("S", "S", 2, "F", "F", false));
    assertTrue(FusionEligibility.enabled("S", "S", 2, "A", "B", true));
    assertFalse(FusionEligibility.enabled("S", "T", 2, "F", "F", true));
    assertFalse(FusionEligibility.enabled("S", "S", 1, "F", "F", true));
    double[] original = {1, 9, 3};
    assertEquals(9.0, PetOverlayStats.maxPt(original), 1e-9);
    assertEquals(1.0, PetOverlayStats.minPt(original), 1e-9);
    assertEquals(13.0 / 3.0, PetOverlayStats.meanPt(original), 1e-9);
  }
}
