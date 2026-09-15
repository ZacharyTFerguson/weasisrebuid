/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.fusion;

/** Applies LUT / window / opacity to a {@link FusionState}. */
public class FusionAction {

  public void applyLut(FusionState state, String lut) {
    if (state != null) {
      state.setLut(lut);
    }
  }

  public void applyWindow(FusionState state, FusionWindow window) {
    if (state != null) {
      state.setWindow(window);
    }
  }

  public void applyOpacity(FusionState state, double opacity) {
    if (state != null) {
      state.setOpacity(opacity);
    }
  }
}
