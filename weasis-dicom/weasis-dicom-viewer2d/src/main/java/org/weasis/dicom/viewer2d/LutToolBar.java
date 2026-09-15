/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d;

import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import org.weasis.core.api.image.PseudoColorOp;
import org.weasis.core.ui.editor.image.DefaultView2d;
import org.weasis.core.ui.util.WtoolBar;

/** 2D pseudo-color LUT chrome (Gray / Inverse). */
public class LutToolBar extends WtoolBar {

  public static final String NAME = "LUT";
  public static final List<String> LUTS = List.of(PseudoColorOp.GRAY, "Sine", "HotIron");

  private DefaultView2d<?> view;
  private final JToggleButton invert = new JToggleButton("Inverse");

  public LutToolBar() {
    super(NAME, 15);
    for (String lut : LUTS) {
      add(lutButton(lut));
    }
    invert.setName("inverseLut");
    invert.addActionListener(e -> setInverted(invert.isSelected()));
    add(invert);
  }

  public void bind(DefaultView2d<?> view) {
    this.view = view;
    if (view != null) {
      invert.setSelected(view.isInverseLut());
    }
  }

  public DefaultView2d<?> boundView() {
    return view;
  }

  public void setLut(String lut) {
    if (view != null) {
      view.setLut(lut);
    }
  }

  public String getLut() {
    return view == null ? PseudoColorOp.GRAY : view.getLut();
  }

  public void setInverted(boolean inverted) {
    invert.setSelected(inverted);
    if (view != null) {
      view.setInverseLut(inverted);
    }
  }

  public boolean isInverted() {
    return view != null && view.isInverseLut();
  }

  public void toggleInvert() {
    setInverted(!isInverted());
  }

  private JButton lutButton(String lut) {
    JButton button =
        new JButton(
            new AbstractAction(lut) {
              @Override
              public void actionPerformed(ActionEvent e) {
                setLut(lut);
              }
            });
    button.setName(lut);
    return button;
  }
}
