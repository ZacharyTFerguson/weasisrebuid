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

import java.awt.BorderLayout;
import java.util.List;
import org.weasis.core.api.gui.Insertable;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.ui.editor.image.ViewerPlugin;
import org.weasis.dicom.viewer3d.dockable.VolumeTool;
import org.weasis.dicom.viewer3d.vr.View3d;
import org.weasis.dicom.viewer3d.vr.VolumeCanvas;

public class View3DContainer extends ViewerPlugin<MediaElement> {

  public static final String NAME = "DICOM 3D Viewer";
  private final View3d view3d;
  private final InfoLayer3d infoLayer;
  private final View3DToolbar view3DToolbar = new View3DToolbar();
  private final VolLutToolBar volLutToolBar = new VolLutToolBar();
  private final VolumeTool volumeTool = new VolumeTool();

  public View3DContainer() {
    this(OpenGLInfo.describe(null, null));
  }

  public View3DContainer(OpenGLInfo.Caps caps) {
    super(NAME);
    this.view3d = new View3d(caps == null ? OpenGLInfo.describe(null, null) : caps);
    this.infoLayer = new InfoLayer3d(view3d);
    add(view3d, BorderLayout.CENTER);
    EventManager.getInstance().setSelectedView(view3d);
    fillSeriesViewerUi();
  }

  void fillSeriesViewerUi() {
    List<Insertable> bars = getSeriesViewerUI().getToolBar();
    bars.clear();
    bars.add(view3DToolbar);
    bars.add(volLutToolBar);
    List<Insertable> tools = getSeriesViewerUI().getTools();
    tools.clear();
    tools.add(volumeTool);
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

  public View3DToolbar getView3DToolbar() {
    return view3DToolbar;
  }

  public VolLutToolBar getVolLutToolBar() {
    return volLutToolBar;
  }

  public VolumeTool getVolumeTool() {
    return volumeTool;
  }

  public OpenGLInfo.Verdict gpuVerdict() {
    return view3d.gpuCaps().verdict();
  }

  public boolean isVolumeRenderingAvailable() {
    return view3d.isVolumeRenderingAvailable();
  }
}
