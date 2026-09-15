/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer3d;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Hashtable;
import javax.swing.JToolBar;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.weasis.dicom.viewer3d.dockable.VolumeTool;
import org.weasis.dicom.viewer3d.vr.RenderingType;
import org.weasis.dicom.viewer3d.vr.View3d;
import org.weasis.dicom.viewer3d.vr.lut.VolumePreset;

class View3DChromeHaveTest {

  @AfterEach
  void clearSelectedView() {
    EventManager.getInstance().setSelectedView(null);
  }

  @Test
  void toolbarAppliesMipWhenGpuAllows() {
    View3d view = new View3d(OpenGLInfo.describe("NVIDIA GeForce", "4.6.0"));
    EventManager.getInstance().setSelectedView(view);
    View3DToolbar bar = new View3DToolbar();
    assertEquals(4, ((JToolBar) bar.getComponent()).getComponentCount());
    bar.select(RenderingType.MIP);
    assertEquals(RenderingType.MIP, view.getRenderingType());
    bar.select(RenderingType.MINIP);
    assertEquals(RenderingType.MINIP, view.getRenderingType());
    bar.select(RenderingType.ISO);
    assertEquals(RenderingType.ISO, view.getRenderingType());
  }

  @Test
  void toolbarDoesNotChangeTypeOnLlvmpipe() {
    View3d view = new View3d(OpenGLInfo.describe("llvmpipe (LLVM 17.0.6)", "4.5"));
    EventManager.getInstance().setSelectedView(view);
    View3DToolbar bar = new View3DToolbar();
    bar.select(RenderingType.MIP);
    assertEquals(RenderingType.COMPOSITE, view.getRenderingType());
  }

  @Test
  void volumeToolWiresSelectedView() {
    View3d view = new View3d(OpenGLInfo.describe("AMD Radeon", "4.6"));
    EventManager.getInstance().setSelectedView(view);
    VolumeTool tool = new VolumeTool();
    tool.setRenderingType(RenderingType.MIP);
    assertEquals(RenderingType.MIP, tool.getRenderingType());
    assertEquals(RenderingType.MIP, view.getRenderingType());
  }

  @Test
  void volumeLutToolbarAppliesBonePreset() {
    View3d view = new View3d(OpenGLInfo.describe("NVIDIA GeForce", "4.6.0"));
    EventManager.getInstance().setSelectedView(view);
    VolLutToolBar bar = new VolLutToolBar();
    assertEquals(2, ((JToolBar) bar.getComponent()).getComponentCount());
    bar.setSelected(VolumePreset.ctBone());
    assertEquals("CT Bone", view.getVolumePreset().getName());
  }

  @Test
  void externalToolbarOpens3dViewer() {
    ExternalView3DToolbar bar = new ExternalView3DToolbar();
    Hashtable<String, Object> props = new Hashtable<>();
    props.put("opengl.renderer", "NVIDIA GeForce");
    props.put("opengl.version", "4.6.0");
    View3DContainer opened = bar.open3d(props);
    assertTrue(opened.isVolumeRenderingAvailable());
    assertEquals(opened, bar.getLastOpened());
    assertEquals(ActionVol.RENDERING_TYPE, EventManager.getInstance().getAction());
  }

  @Test
  void volumeLutIgnoredWithoutGpu() {
    View3d view = new View3d();
    EventManager.getInstance().setSelectedView(view);
    assertFalse(view.isVolumeRenderingAvailable());
    String before = view.getVolumePreset().getName();
    VolLutToolBar bar = new VolLutToolBar();
    bar.setSelected(VolumePreset.ctBone());
    assertEquals(before, view.getVolumePreset().getName());
  }
}
