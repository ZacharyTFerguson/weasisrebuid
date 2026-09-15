/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.dockable;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import org.weasis.core.api.gui.Insertable;
import org.weasis.core.ui.editor.image.ViewCanvas;
import org.weasis.core.ui.model.graphic.imp.seg.SegRegion;
import org.weasis.dicom.codec.seg.MaskFrames;
import org.weasis.dicom.viewer2d.SegRegionLocator;
import org.weasis.dicom.viewer2d.View2d;

/** Dockable SEG overlay list. Alt+S on the bound view still toggles global visibility. */
public class SegmentationTool extends JPanel implements Insertable {

  public static final String NAME = "Segmentation";

  private final List<SegRegion> regions = new ArrayList<>();
  private final JCheckBox overlayBox = new JCheckBox("Show overlay", true);
  private ViewCanvas view;
  private int position = 110;
  private boolean enabled = true;
  private float opacity = 0.5f;

  public SegmentationTool() {
    super(new BorderLayout());
    overlayBox.setName("segOverlay");
    overlayBox.addActionListener(e -> setOverlayVisible(overlayBox.isSelected()));
    add(overlayBox, BorderLayout.NORTH);
  }

  public void bind(ViewCanvas view) {
    this.view = view;
    if (view != null) {
      overlayBox.setSelected(view.isSegmentationsVisible());
    }
  }

  public void bind(View2d view) {
    bind((ViewCanvas) view);
  }

  public ViewCanvas getView() {
    return view;
  }

  public List<SegRegion> getRegions() {
    return Collections.unmodifiableList(regions);
  }

  public void setRegions(List<SegRegion> regions) {
    this.regions.clear();
    if (regions != null) {
      this.regions.addAll(regions);
    }
  }

  public void addRegion(SegRegion region) {
    if (region != null) {
      regions.add(region);
    }
  }

  public void setRegionVisible(SegRegion region, boolean visible) {
    if (region != null) {
      region.setVisible(visible);
    }
  }

  public boolean isOverlayVisible() {
    return overlayBox.isSelected();
  }

  public void setOverlayVisible(boolean visible) {
    overlayBox.setSelected(visible);
    if (view != null) {
      view.setSegmentationsVisible(visible);
    }
  }

  public void toggleOverlay() {
    setOverlayVisible(!isOverlayVisible());
  }

  public float getOpacity() {
    return opacity;
  }

  public void setOpacity(float opacity) {
    this.opacity = Math.max(0f, Math.min(1f, opacity));
    for (SegRegion region : regions) {
      region.setOpacity(this.opacity);
    }
  }

  public List<SegRegion> visibleRegions() {
    List<SegRegion> out = new ArrayList<>();
    if (!isOverlayVisible()) {
      return out;
    }
    for (SegRegion region : regions) {
      if (region.isVisible()) {
        out.add(region);
      }
    }
    return out;
  }

  public SegRegion locate(MaskFrames frames, int frameIndex, int x, int y) {
    if (!isOverlayVisible()) {
      return null;
    }
    return new SegRegionLocator().locate(frames, frameIndex, x, y, regions);
  }

  public SegRegion locateOnView(int x, int y) {
    if (!isOverlayVisible()) {
      return null;
    }
    return new SegRegionLocator().locateRegion(view, x, y);
  }

  @Override
  public String getComponentName() {
    return NAME;
  }

  @Override
  public Type getType() {
    return Type.TOOL;
  }

  @Override
  public int getComponentPosition() {
    return position;
  }

  @Override
  public void setComponentPosition(int position) {
    this.position = position;
  }

  @Override
  public boolean isComponentEnabled() {
    return enabled;
  }

  @Override
  public void setComponentEnabled(boolean enabled) {
    this.enabled = enabled;
  }
}
