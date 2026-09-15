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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Hashtable;
import org.junit.jupiter.api.Test;
import org.weasis.dicom.codec.DicomMime;
import org.weasis.dicom.viewer3d.vr.RenderingType;

class View3DFactoryTest {

  @Test
  void readsVolMimeAtStartLevel120() {
    View3DFactory factory = new View3DFactory();
    assertTrue(factory.canReadMimeType(DicomMime.VOL_DICOM));
    assertTrue(factory.canReadMimeType(DicomMime.IMAGE_DICOM));
    assertEquals(120, factory.getLevel());
    Hashtable<String, Object> props = new Hashtable<>();
    props.put("opengl.renderer", "NVIDIA GeForce");
    props.put("opengl.version", "4.6.0");
    View3DContainer viewer = (View3DContainer) factory.createSeriesViewer(props);
    assertInstanceOf(View3DContainer.class, viewer);
    assertTrue(factory.isViewerCreatedByThisFactory(viewer));
    assertTrue(viewer.isVolumeRenderingAvailable());
    assertEquals(OpenGLInfo.Verdict.OK, viewer.gpuVerdict());
  }

  @Test
  void nAWhenNoGpuAndRefusesLlvmpipe() {
    View3DFactory factory = new View3DFactory();
    View3DContainer missing = (View3DContainer) factory.createSeriesViewer(new Hashtable<>());
    assertEquals(OpenGLInfo.Verdict.NA_NO_GPU, missing.gpuVerdict());
    assertFalse(missing.isVolumeRenderingAvailable());

    Hashtable<String, Object> pipe = new Hashtable<>();
    pipe.put("opengl.renderer", "llvmpipe (LLVM 17.0.6)");
    pipe.put("opengl.version", "4.5");
    View3DContainer refused = (View3DContainer) factory.createSeriesViewer(pipe);
    assertEquals(OpenGLInfo.Verdict.REFUSED_LLVMPIPE, refused.gpuVerdict());
    refused.getView3d().setRenderingType(RenderingType.MIP);
    assertEquals(RenderingType.COMPOSITE, refused.getView3d().getRenderingType());
  }
}
