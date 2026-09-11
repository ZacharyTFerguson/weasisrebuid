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

import java.util.List;
import org.junit.jupiter.api.Test;
import org.weasis.dicom.viewer3d.gl.GlGate;

class Viewer3dHaveTest {

  @Test
  void refuseLlvmpipeAndRequireGl33() {
    assertFalse(GlGate.usable("llvmpipe", "4.5"));
    assertFalse(GlGate.usable("NVIDIA", "3.2"));
    assertTrue(GlGate.usable("NVIDIA GeForce", "3.3"));
    assertTrue(GlGate.usable("AMD", "OpenGL 4.6"));
    assertTrue(GlGate.probe().isEmpty());
  }

  @Test
  void mx15ActiveAlwaysLocalPeersHonorMap() {
    View3d active = new View3d();
    View3d peer = new View3d();
    View3dSync sync = new View3dSync();
    assertTrue(sync.peerEnabled(View3dSync.Action.ROTATION));
    assertFalse(sync.peerEnabled(View3dSync.Action.WINDOW_LEVEL));
    sync.apply(View3dSync.Action.WINDOW_LEVEL, active, List.of(active, peer));
    assertTrue(active.applied().contains(View3dSync.Action.WINDOW_LEVEL));
    assertFalse(peer.applied().contains(View3dSync.Action.WINDOW_LEVEL));
    sync.apply(View3dSync.Action.ZOOM, active, List.of(active, peer));
    assertTrue(peer.applied().contains(View3dSync.Action.ZOOM));
  }

  @Test
  void renderingPerspectiveCutAndMinImages() {
    View3d v = new View3d();
    assertEquals(RenderingType.COMPOSITE, v.rendering());
    assertTrue(v.perspectiveDefault());
    assertEquals(18, CrosshairCut.directionalCount());
    assertFalse(View3d.toolbarEnabled(4));
    assertTrue(View3d.toolbarEnabled(5));
  }
}
