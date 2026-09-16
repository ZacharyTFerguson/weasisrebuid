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

import java.util.ArrayList;
import java.util.List;
import org.dcm4che3.data.Attributes;
import org.weasis.core.ui.editor.image.DefaultView2d;
import org.weasis.dicom.codec.utils.SuvFactor;

/** PET/CT fusion. {@code targetViews} are the views that receive the overlay. */
public class FusionController {

  private final List<DefaultView2d<?>> targetViews = new ArrayList<>();
  private final FusionState state = new FusionState();
  private final FusionColorBar colorBar = new FusionColorBar();
  private final FusionAction action = new FusionAction();
  private double overlayOpacity = 0.4;

  public FusionController() {
    colorBar.bind(this);
    colorBar.setLut(state.getLut());
  }

  public List<DefaultView2d<?>> getTargetViews() {
    return targetViews;
  }

  public FusionState getState() {
    return state;
  }

  public FusionColorBar getColorBar() {
    return colorBar;
  }

  public void addTarget(DefaultView2d<?> view) {
    if (view != null && !targetViews.contains(view)) {
      targetViews.add(view);
    }
  }

  public void removeTarget(DefaultView2d<?> view) {
    targetViews.remove(view);
  }

  public double getOverlayOpacity() {
    return overlayOpacity;
  }

  public void setOverlayOpacity(double overlayOpacity) {
    this.overlayOpacity = Math.max(0, Math.min(1, overlayOpacity));
    action.applyOpacity(state, this.overlayOpacity);
  }

  public void applyLut(String lut) {
    action.applyLut(state, lut);
    colorBar.setLut(state.getLut());
  }

  public void applyWindow(FusionWindow window) {
    action.applyWindow(state, window);
    colorBar.setWindow(state.getWindow());
  }

  /** SUVbw = stored activity × {@link SuvFactor#factor(Attributes)}. */
  public double suvBw(Attributes pet, double stored) {
    return stored * SuvFactor.factor(pet);
  }
}
