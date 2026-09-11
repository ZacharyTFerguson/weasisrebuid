/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.mpr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MprArchitectureTest {

  @Test
  void isotropicCenterAndCanvasAreDistinct() {
    MprVolume v = new MprVolume(9, 1.0, 1.0, 1.0);
    assertTrue(v.isotropic());
    assertEquals(4.0, v.volumeCenter()[0], 1e-9);
    v.setCanvasProjection(1, 2, 3);
    assertNotEquals(v.volumeCenter()[0], v.canvasProjection()[0]);
    MprVolume aniso = new MprVolume(5, 1.0, 1.0, 2.5);
    assertFalse(aniso.isotropic());
  }

  @Test
  void requiredTypesExist() {
    Volume vol = new Volume(4);
    MprVolume geom = new MprVolume(4, 1, 1, 1);
    MprController controller = new MprController(geom, vol);
    assertEquals(3, controller.views().size());
    assertTrue(controller.couplingAlwaysOn());
    assertTrue(controller.scrollSync());
    assertTrue(controller.zoomSync());
    assertTrue(controller.wlSync());
    assertFalse(controller.panSync());
    assertEquals(Plane.AXIAL, controller.view(Plane.AXIAL).plane());
    assertEquals("blue", controller.view(Plane.AXIAL).axis().color());
    assertEquals("red", new MprAxis(Plane.CORONAL).color());
    assertEquals("green", new MprAxis(Plane.SAGITTAL).color());
    assertNotNull(new MprContainer(controller));
    assertEquals("axial", new MprView(Plane.AXIAL).label());
  }

  @Test
  void axesControlAndCprAndMip() {
    MprVolume v = new MprVolume(8, 1, 1, 1);
    AxesControl axes = new AxesControl(v);
    assertTrue(axes.couplingAlwaysOn());
    axes.moveCenter(1, 0, 0);
    assertEquals(AxesControl.Mode.MOVE_CENTER, axes.mode());
    assertEquals(4.5, v.volumeCenter()[0], 1e-9);
    axes.moveAxis(0.5);
    assertEquals(AxesControl.Mode.MOVE_AXIS, axes.mode());
    axes.rotate(15);
    assertEquals(15, axes.rotationDeg(), 1e-9);
    axes.scrollDepth(2);
    assertEquals(2.5, axes.depth(), 1e-9);
    CprPath path = new CprPath();
    path.add(0, 0, 0);
    path.add(3, 4, 0);
    assertEquals(5.0, path.length(), 1e-9);
    double[] mid = path.sample(0.5);
    assertEquals(1.5, mid[0], 1e-9);
    assertTrue(path.axialFixedZ());
    assertTrue(path.splineSmoothed().size() > path.size());
    MipProjector mip = new MipProjector();
    assertEquals(1, mip.slabSize());
    mip.setMode(MipProjector.Mode.MAX);
    assertEquals(2, mip.thickness());
    assertEquals(5, mip.slabSize());
    mip.setThickness(3);
    assertEquals(7, mip.slabSize());
    assertEquals(9, mip.project(new double[] {1, 9, 3}), 1e-9);
    mip.setMode(MipProjector.Mode.MIN);
    assertEquals(1, mip.project(new double[] {1, 9, 3}), 1e-9);
    mip.setMode(MipProjector.Mode.MEAN);
    assertEquals(2.0, mip.project(new double[] {1, 3}), 1e-9);
    mip.cycle();
    assertEquals(MipProjector.Mode.MAX, mip.mode());
  }

  @Test
  void mprDisabledUnderFiveImages() {
    assertFalse(MprGate.enabled(4));
    assertTrue(MprGate.enabled(5));
  }

  @Test
  void autoCenterDefaultIsWhenCenterHidden() {
    MprPrefView prefs = new MprPrefView();
    assertEquals(MprPrefView.AutoCenter.WHEN_CENTER_HIDDEN, prefs.autoCenter());
    assertEquals(1, prefs.autoCenterCode());
    prefs.setAutoCenterCode(0);
    assertEquals(MprPrefView.AutoCenter.NEVER, prefs.autoCenter());
    prefs.setAutoCenterCode(2);
    assertEquals(MprPrefView.AutoCenter.ALWAYS, prefs.autoCenter());
  }

  @Test
  void gantryTiltTrilinear() {
    Volume vol = new Volume(2);
    vol.set(0, 0, 0, 0);
    vol.set(1, 0, 0, 10);
    vol.set(0, 1, 0, 0);
    vol.set(1, 1, 0, 10);
    vol.set(0, 0, 1, 0);
    vol.set(1, 0, 1, 10);
    vol.set(0, 1, 1, 0);
    vol.set(1, 1, 1, 10);
    assertEquals(5.0, GantryTilt.trilinear(vol, 0.5, 0, 0), 1e-9);
    double[] mapped = GantryTilt.backwardMap(1, 0, 0, 90);
    assertEquals(1.0, mapped[0], 1e-9);
  }

  @Test
  void cprAxialOnlyAndResetDefaults() {
    CprBuilder builder = new CprBuilder(1.0);
    assertEquals(40.0, builder.heightMm(), 1e-9);
    builder.setHeightMm(12);
    builder.resetToDefaults(2.0);
    assertEquals(40.0, builder.heightMm(), 1e-9);
    assertEquals(2.0, builder.stepMm(), 1e-9);
    assertFalse(builder.writesMillimetrePixelSpacingOnPanoramic());
    assertFalse(builder.panoramicCalibrated());
    assertTrue(builder.crossSectionCalibrated());
    CprPath axial = new CprPath();
    axial.add(0, 0, 3);
    axial.add(4, 0, 3);
    assertTrue(builder.canReconstruct(axial));
    assertNotNull(builder.buildPanoramic(axial));
    CprPath coronal = new CprPath();
    coronal.add(0, 0, 0);
    coronal.add(0, 4, 2);
    assertFalse(builder.canReconstruct(coronal));
    assertNull(builder.buildPanoramic(coronal));
  }

  @Test
  void mip2dLeavingNoneInitializesThicknessTwoAndScrollClearsGraphics() {
    Mip2d mip = new Mip2d();
    assertFalse(mip.indicatorVisible());
    mip.setMode(MipProjector.Mode.MEAN);
    assertEquals(2, mip.thickness());
    assertEquals(5, mip.slabSize());
    mip.setLiveGraphics(true);
    mip.scrollTo(4);
    assertFalse(mip.liveGraphics());
    assertTrue(mip.indicatorVisible());
  }

  @Test
  void derivedSeriesLandInStudy() {
    MprController controller = new MprController(new MprVolume(8, 1, 1, 1), new Volume(8));
    MprContainer container = new MprContainer(controller);
    DerivedSeriesBuilder builder = new DerivedSeriesBuilder();
    assertEquals(3, builder.buildThreePlanes(container).size());
    assertEquals(3, builder.studySeries().size());
    assertTrue(builder.studySeries().get(0).startsWith("2.25."));
  }
}
