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

import org.weasis.dicom.viewer3d.vr.View3d;

/** Overlay text for the 3D canvas (GPU verdict, rendering type). */
public class InfoLayer3d {

  private final View3d view;

  public InfoLayer3d(View3d view) {
    this.view = view;
  }

  public String overlayText() {
    if (view == null) {
      return OpenGLInfo.Verdict.NA_NO_GPU.name();
    }
    return view.gpuCaps().verdict().name() + " " + view.getRenderingType();
  }

  public View3d getView() {
    return view;
  }
}
