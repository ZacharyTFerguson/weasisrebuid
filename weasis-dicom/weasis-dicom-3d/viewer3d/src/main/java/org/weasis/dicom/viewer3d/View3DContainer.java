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

import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.ui.editor.image.ViewerPlugin;
import org.weasis.dicom.viewer3d.vr.View3d;
import org.weasis.dicom.viewer3d.vr.VolumeCanvas;

public class View3DContainer extends ViewerPlugin<MediaElement> {

  private final View3d view3d;
  private final InfoLayer3d infoLayer;

  public View3DContainer() {
    this(OpenGLInfo.describe(null, null));
  }

  public View3DContainer(OpenGLInfo.Caps caps) {
    super("DICOM 3D Viewer");
    this.view3d = new View3d(caps == null ? OpenGLInfo.describe(null, null) : caps);
    this.infoLayer = new InfoLayer3d(view3d);
    add(view3d);
    EventManager.getInstance().setSelectedView(view3d);
  }

  public View3d getView3d() {
    return view3d;
  }

  public VolumeCanvas getCanvas() {
    return view3d;
  }

  public InfoLayer3d getInfoLayer() {
    return infoLayer;
  }

  public OpenGLInfo.Verdict gpuVerdict() {
    return view3d.gpuCaps().verdict();
  }

  public boolean isVolumeRenderingAvailable() {
    return view3d.isVolumeRenderingAvailable();
  }
}
