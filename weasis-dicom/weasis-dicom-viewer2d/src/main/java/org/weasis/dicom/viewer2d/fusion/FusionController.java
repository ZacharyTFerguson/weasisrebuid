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

import java.util.List;
import org.weasis.dicom.viewer2d.mpr.MprContainer;
import org.weasis.dicom.viewer2d.mpr.MprView;

/**
 * MX-04: each MPR pane has its own {@link FusionOp}; Image Tools {@code targetViews()} broadcasts
 * Enable/LUT/series/opacity to all panes of that container when the selected view is an MprView. 2D
 * fusion stays per-view. {@code RESET_DISPLAY} turns fusion off.
 */
public final class FusionController {

  public List<MprView> targetViews(MprView selected, MprContainer container) {
    if (selected != null && container != null && container.panes().contains(selected)) {
      return container.panes();
    }
    if (selected == null) {
      return List.of();
    }
    return List.of(selected);
  }

  public void setEnabled(MprView selected, MprContainer container, boolean enabled) {
    for (MprView view : targetViews(selected, container)) {
      op(view).setEnabled(enabled);
    }
  }

  public void setLut(MprView selected, MprContainer container, String lut) {
    for (MprView view : targetViews(selected, container)) {
      op(view).setParam(FusionOp.LUT, lut);
    }
  }

  public void setSeries(MprView selected, MprContainer container, String seriesUid) {
    for (MprView view : targetViews(selected, container)) {
      op(view).setParam(FusionOp.SERIES, seriesUid);
    }
  }

  public void setOpacity(MprView selected, MprContainer container, double opacity) {
    for (MprView view : targetViews(selected, container)) {
      op(view).setParam(FusionOp.OPACITY, opacity);
    }
  }

  public void resetDisplay(MprView selected, MprContainer container) {
    setEnabled(selected, container, false);
  }

  public void applyInherited(MprContainer container, FusionState state) {
    if (container == null || state == null) {
      return;
    }
    for (MprView view : container.panes()) {
      view.setFusionOp(state.op());
    }
  }

  static FusionOp op(MprView view) {
    Object existing = view.fusionOp();
    if (existing instanceof FusionOp fusion) {
      return fusion;
    }
    FusionOp created = new FusionOp();
    view.setFusionOp(created);
    return created;
  }
}
