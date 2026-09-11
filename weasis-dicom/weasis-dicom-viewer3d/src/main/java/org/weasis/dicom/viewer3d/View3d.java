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

import java.util.ArrayList;
import java.util.List;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.ui.editor.image.ViewerPlugin;

public class View3d extends ViewerPlugin<MediaElement> {

  private RenderingType rendering = RenderingType.COMPOSITE;
  private boolean orthographic;
  private CrosshairCut cut = CrosshairCut.NONE;
  private final List<View3dSync.Action> applied = new ArrayList<>();

  public View3d() {
    super("DICOM 3D Viewer");
  }

  public RenderingType rendering() {
    return rendering;
  }

  public void setRendering(RenderingType rendering) {
    this.rendering = rendering == null ? RenderingType.COMPOSITE : rendering;
  }

  public boolean perspectiveDefault() {
    return !orthographic;
  }

  public void setOrthographic(boolean orthographic) {
    this.orthographic = orthographic;
  }

  public CrosshairCut cut() {
    return cut;
  }

  public void setCut(CrosshairCut cut) {
    this.cut = cut == null ? CrosshairCut.NONE : cut;
  }

  public static boolean toolbarEnabled(int imageCount) {
    return imageCount >= 5;
  }

  public void applyLocal(View3dSync.Action action) {
    applied.add(action);
    if (action == View3dSync.Action.ORTHOGRAPHIC) {
      orthographic = true;
    }
    if (action == View3dSync.Action.RENDERING_TYPE) {
      rendering = RenderingType.MIP_MAX;
    }
  }

  public List<View3dSync.Action> applied() {
    return List.copyOf(applied);
  }
}
