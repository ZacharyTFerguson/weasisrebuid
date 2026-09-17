/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.pref;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import org.weasis.core.api.gui.util.AbstractItemDialogPage;
import org.weasis.core.api.service.WProperties;

/** SEG overlay preferences: fill, contour, default visibility, and opacity. */
public class SegPrefView extends AbstractItemDialogPage {

  public static final String TITLE = "Segmentation";
  public static final String PREF_OPACITY = "weasis.seg.overlay.opacity";
  public static final String PREF_FILL = "weasis.seg.overlay.fill";
  public static final String PREF_CONTOUR = "weasis.seg.overlay.contour";
  public static final String PREF_VISIBLE = "weasis.seg.overlay.visible";

  public static final int DEFAULT_OPACITY_PERCENT = 50;

  private final WProperties prefs;
  private final JSlider opacitySlider;
  private final JCheckBox fillBox;
  private final JCheckBox contourBox;
  private final JCheckBox visibleBox;

  public SegPrefView() {
    this(new WProperties());
  }

  public SegPrefView(WProperties prefs) {
    super(TITLE, 430);
    this.prefs = prefs == null ? new WProperties() : prefs;
    int opacityPercent =
        Math.round(
            this.prefs.getFloatProperty(PREF_OPACITY, DEFAULT_OPACITY_PERCENT / 100f) * 100f);
    opacityPercent = Math.max(0, Math.min(100, opacityPercent));
    opacitySlider = new JSlider(0, 100, opacityPercent);
    opacitySlider.setName("segOpacity");
    fillBox = new JCheckBox("Fill", this.prefs.getBooleanProperty(PREF_FILL, true));
    fillBox.setName("segFill");
    contourBox = new JCheckBox("Contour", this.prefs.getBooleanProperty(PREF_CONTOUR, true));
    contourBox.setName("segContour");
    visibleBox = new JCheckBox("Show overlay", this.prefs.getBooleanProperty(PREF_VISIBLE, true));
    visibleBox.setName("segVisible");
    JPanel form = new JPanel();
    form.add(new JLabel("Opacity"));
    form.add(opacitySlider);
    form.add(fillBox);
    form.add(contourBox);
    form.add(visibleBox);
    add(form);
  }

  public int opacityPercent() {
    return opacitySlider.getValue();
  }

  public float opacity() {
    return opacityPercent() / 100f;
  }

  public void setOpacityPercent(int value) {
    opacitySlider.setValue(Math.max(0, Math.min(100, value)));
  }

  public boolean fill() {
    return fillBox.isSelected();
  }

  public void setFill(boolean fill) {
    fillBox.setSelected(fill);
  }

  public boolean contour() {
    return contourBox.isSelected();
  }

  public void setContour(boolean contour) {
    contourBox.setSelected(contour);
  }

  public boolean overlayVisible() {
    return visibleBox.isSelected();
  }

  public void setOverlayVisible(boolean visible) {
    visibleBox.setSelected(visible);
  }

  @Override
  public void closeAdditionalWindow() {
    prefs.putFloatProperty(PREF_OPACITY, opacity());
    prefs.putBooleanProperty(PREF_FILL, fill());
    prefs.putBooleanProperty(PREF_CONTOUR, contour());
    prefs.putBooleanProperty(PREF_VISIBLE, overlayVisible());
  }

  @Override
  public void resetToDefaultValues() {
    opacitySlider.setValue(DEFAULT_OPACITY_PERCENT);
    fillBox.setSelected(true);
    contourBox.setSelected(true);
    visibleBox.setSelected(true);
  }
}
