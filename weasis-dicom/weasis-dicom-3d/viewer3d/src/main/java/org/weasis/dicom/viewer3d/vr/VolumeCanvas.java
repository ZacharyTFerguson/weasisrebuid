/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer3d.vr;

import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.weasis.dicom.viewer3d.OpenGLInfo;
import org.weasis.dicom.viewer3d.geometry.ArcballMouseListener;
import org.weasis.dicom.viewer3d.geometry.Camera;

/**
 * Headless-safe VR canvas. GPU capability is decided by {@link OpenGLInfo}; JOGL is not required to
 * record MX-15 refuse / N/A.
 */
public class VolumeCanvas extends JPanel implements ArcballMouseListener {

  private final OpenGLInfo.Caps gpuCaps;
  private final VolumeViewModel model = new VolumeViewModel();
  private final Camera camera = Camera.axial();

  public VolumeCanvas() {
    this(OpenGLInfo.describe(null, null));
  }

  public VolumeCanvas(OpenGLInfo.Caps gpuCaps) {
    super(new BorderLayout());
    this.gpuCaps = gpuCaps == null ? OpenGLInfo.describe(null, null) : gpuCaps;
    add(new JLabel(statusText()), BorderLayout.CENTER);
  }

  public OpenGLInfo.Caps gpuCaps() {
    return gpuCaps;
  }

  public boolean isVolumeRenderingAvailable() {
    return OpenglUtils.allowVolumeRendering(gpuCaps);
  }

  public String statusText() {
    return switch (gpuCaps.verdict()) {
      case OK -> "OpenGL " + OpenGLInfo.MIN_VERSION + "+ ready";
      case REFUSED_LLVMPIPE -> "REFUSED llvmpipe";
      case BELOW_MIN_VERSION -> "OpenGL " + OpenGLInfo.MIN_VERSION + "+ required";
      case NA_NO_GPU -> "N/A no GPU";
    };
  }

  public VolumeViewModel getModel() {
    return model;
  }

  public Camera getCamera() {
    return camera;
  }

  public RenderingType getRenderingType() {
    return model.getLayer().getType();
  }

  public void setRenderingType(RenderingType type) {
    if (isVolumeRenderingAvailable()) {
      model.getLayer().setType(type);
    }
  }

  @Override
  public void onRotate(double yawRadians, double pitchRadians) {
    if (isVolumeRenderingAvailable()) {
      camera.orbit(yawRadians, pitchRadians);
    }
  }

  @Override
  public void onZoom(double factor) {
    if (isVolumeRenderingAvailable()) {
      model.getViewData().setZoom(model.getViewData().getZoom() * factor);
    }
  }
}
