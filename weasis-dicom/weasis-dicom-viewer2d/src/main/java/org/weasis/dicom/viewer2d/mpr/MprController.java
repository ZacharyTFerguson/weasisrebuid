/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.mpr;

import org.weasis.core.ui.model.layer.LayerType;
import org.weasis.dicom.viewer2d.mip.MipView;

public class MprController implements VolumeProvider {

  private Volume volume;
  private final MprView axial = new MprView();
  private final MprView coronal = new MprView();
  private final MprView sagittal = new MprView();
  private MprView selected;

  public MprController() {
    axial.setAxis(MprAxis.AXIAL);
    coronal.setAxis(MprAxis.CORONAL);
    sagittal.setAxis(MprAxis.SAGITTAL);
    bind(axial);
    bind(coronal);
    bind(sagittal);
    selected = axial;
  }

  private void bind(MprView view) {
    view.setController(this);
  }

  public void setVolume(Volume volume) {
    this.volume = volume;
    if (volume != null) {
      axial.setSliceIndex(volume.sizeZ() / 2);
      coronal.setSliceIndex(volume.sizeY() / 2);
      sagittal.setSliceIndex(volume.sizeX() / 2);
    }
  }

  @Override
  public Volume getVolume() {
    return volume;
  }

  public MprView getAxial() {
    return axial;
  }

  public MprView getCoronal() {
    return coronal;
  }

  public MprView getSagittal() {
    return sagittal;
  }

  public MprView getSelectedView() {
    return selected;
  }

  public void setSelectedView(MprView view) {
    if (view != null) {
      this.selected = view;
    }
  }

  public MprView[] views() {
    return new MprView[] {axial, coronal, sagittal};
  }

  public void setMip(MipView.Type type, int thickness) {
    for (MprView view : views()) {
      view.getMip().setType(type);
      view.getMip().setThickness(thickness);
    }
  }

  public void cycleMipType() {
    MprView src = selected == null ? axial : selected;
    MipView.Type current = src.getMip().getType();
    MipView.Type next =
        switch (current) {
          case NONE -> MipView.Type.MIN;
          case MIN -> MipView.Type.MEAN;
          case MEAN -> MipView.Type.MAX;
          case MAX -> MipView.Type.NONE;
        };
    setMip(next, src.getMip().getThickness());
  }

  public void centerSelected() {
    viewOrAxial().centerCrosshair();
  }

  public void centerAll() {
    for (MprView view : views()) {
      view.centerCrosshair();
    }
  }

  public void toggleCenterSelected() {
    viewOrAxial().toggleCrosshairCenter();
  }

  public void toggleCenterAll() {
    boolean next = selected == null || !selected.isCrosshairCenterVisible();
    for (MprView view : views()) {
      view.setCrosshairCenterVisible(next);
    }
  }

  public void toggleCrosshairSelected() {
    viewOrAxial().toggleCrosshair();
  }

  public void toggleCrosshairAll() {
    boolean next = selected == null || !selected.isLayerVisible(LayerType.CROSSLINES);
    for (MprView view : views()) {
      view.setLayerVisible(LayerType.CROSSLINES, next);
    }
  }

  public void addSelectedThickness(int delta) {
    MprView view = viewOrAxial();
    view.getMip().setThickness(view.getMip().getThickness() + delta);
  }

  private MprView viewOrAxial() {
    return selected == null ? axial : selected;
  }

  public double[][] axialSlice() {
    return axial.rebuild(volume);
  }

  public double[][] coronalSlice() {
    return coronal.rebuild(volume);
  }

  public double[][] sagittalSlice() {
    return sagittal.rebuild(volume);
  }
}
