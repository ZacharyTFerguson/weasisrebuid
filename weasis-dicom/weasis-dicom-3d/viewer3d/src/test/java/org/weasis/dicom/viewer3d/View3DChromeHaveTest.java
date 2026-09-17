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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Hashtable;
import java.util.List;
import javax.swing.AbstractButton;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.service.UICore;
import org.weasis.core.ui.editor.image.ViewerPlugin;
import org.weasis.dicom.viewer3d.dockable.VolumeTool;
import org.weasis.dicom.viewer3d.vr.RenderingType;
import org.weasis.dicom.viewer3d.vr.View3d;
import org.weasis.dicom.viewer3d.vr.lut.VolumePreset;

class View3DChromeHaveTest {

  @AfterEach
  void clearSelectedView() {
    EventManager.getInstance().setSelectedView(null);
    closePlugins(UICore.getInstance());
  }

  @Test
  void toolbarAppliesMipWhenGpuAllows() {
    View3d view = new View3d(OpenGLInfo.describe("NVIDIA GeForce", "4.6.0"));
    EventManager.getInstance().setSelectedView(view);
    View3DToolbar bar = new View3DToolbar();
    assertEquals(4, bar.getComponentCount());
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
    assertEquals(2, bar.getComponentCount());
    bar.setSelected(VolumePreset.ctBone());
    assertEquals("CT Bone", view.getVolumePreset().getName());
  }

  @Test
  void externalToolbarOpens3dViewer() {
    ExternalView3DToolbar bar = new ExternalView3DToolbar();
    Hashtable<String, Object> props = new Hashtable<>();
    props.put("opengl.renderer", "NVIDIA GeForce");
    props.put("opengl.version", "4.6.0");
    UICore core = new UICore();
    View3DContainer opened = bar.open3d(props, core);
    assertTrue(opened.isVolumeRenderingAvailable());
    assertEquals(opened, bar.getLastOpened());
    assertSame(opened, core.getSelectedViewerPlugin());
    assertEquals(ActionVol.RENDERING_TYPE, EventManager.getInstance().getAction());
    AbstractButton open = (AbstractButton) bar.getComponent(0);
    assertEquals("3D", open.getText());
    assertEquals("3d", open.getName());
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

  @Test
  void containerWiresRenderingAndLutChrome() {
    View3DContainer container = new View3DContainer(OpenGLInfo.describe("NVIDIA GeForce", "4.6.0"));
    assertEquals(View3DContainer.NAME, container.getPluginName());
    assertTrue(
        container.getSeriesViewerUI().getToolBar().stream()
            .anyMatch(b -> View3DToolbar.NAME.equals(b.getComponentName())));
    assertTrue(
        container.getSeriesViewerUI().getToolBar().stream()
            .anyMatch(b -> VolLutToolBar.NAME.equals(b.getComponentName())));
    assertTrue(
        container.getSeriesViewerUI().getTools().stream()
            .anyMatch(b -> VolumeTool.NAME.equals(b.getComponentName())));
    AbstractButton composite = (AbstractButton) container.getView3DToolbar().getComponent(0);
    AbstractButton mip = (AbstractButton) container.getView3DToolbar().getComponent(1);
    AbstractButton minip = (AbstractButton) container.getView3DToolbar().getComponent(2);
    AbstractButton iso = (AbstractButton) container.getView3DToolbar().getComponent(3);
    assertEquals(RenderingType.COMPOSITE.name(), composite.getText());
    assertEquals(RenderingType.MIP.name(), mip.getText());
    assertEquals(RenderingType.MINIP.name(), minip.getText());
    assertEquals(RenderingType.ISO.name(), iso.getText());
    assertEquals(RenderingType.COMPOSITE.name(), composite.getName());
    assertEquals(RenderingType.MIP.name(), mip.getName());
    mip.doClick();
    assertEquals(RenderingType.MIP, container.getView3d().getRenderingType());
    AbstractButton bone = (AbstractButton) container.getVolLutToolBar().getComponent(1);
    assertEquals("CT Bone", bone.getText());
    bone.doClick();
    assertEquals("CT Bone", container.getView3d().getVolumePreset().getName());
  }

  static void closePlugins(UICore core) {
    for (ViewerPlugin<?> plugin : List.copyOf(core.getOpenViewerPlugins())) {
      core.closeViewerPlugin(plugin);
    }
  }
}
