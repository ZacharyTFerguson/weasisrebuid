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

public final class CameraView {

  private final View view;
  private final Camera camera;

  public CameraView(View view, Camera camera) {
    this.view = view == null ? View.AXIAL : view;
    this.camera = camera == null ? Camera.axial() : camera;
  }

  public View getView() {
    return view;
  }

  public Camera getCamera() {
    return camera;
  }

  public static CameraView axial() {
    return new CameraView(View.AXIAL, Camera.axial());
  }

  public static CameraView coronal() {
    return new CameraView(View.CORONAL, Camera.coronal());
  }

  public static CameraView sagittal() {
    return new CameraView(View.SAGITTAL, Camera.sagittal());
  }
}
