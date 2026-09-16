/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.wave;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import org.weasis.core.ui.util.WtoolBar;

/** ECG 2/4/12-lead format shortcuts. */
public class WaveformToolBar extends WtoolBar {

  public static final String NAME = "ECG";
  private WaveView view;

  public WaveformToolBar() {
    this(null);
  }

  public WaveformToolBar(WaveView view) {
    super(NAME, 10);
    this.view = view;
    add(formatButton("2", Format.TWO));
    add(formatButton("4", Format.FOUR));
    add(formatButton("12", Format.DEFAULT));
  }

  public void bind(WaveView view) {
    this.view = view;
  }

  public WaveView boundView() {
    return view;
  }

  JButton formatButton(String name, Format format) {
    JButton button =
        new JButton(
            new AbstractAction(name) {
              @Override
              public void actionPerformed(ActionEvent e) {
                if (view != null) {
                  view.setFormat(format);
                }
              }
            });
    button.setName(name);
    return button;
  }
}
