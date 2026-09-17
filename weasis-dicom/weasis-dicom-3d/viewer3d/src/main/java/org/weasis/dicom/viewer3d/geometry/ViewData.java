/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer3d.geometry;

public final class ViewData {

  private CameraView cameraView = CameraView.axial();
  private double zoom = 1.0;

  public CameraView getCameraView() {
    return cameraView;
  }

  public void setCameraView(CameraView cameraView) {
    if (cameraView != null) {
      this.cameraView = cameraView;
    }
  }

  public double getZoom() {
    return zoom;
  }

  public void setZoom(double zoom) {
    this.zoom = Math.max(0.01, zoom);
  }
}
