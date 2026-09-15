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

import javax.swing.JButton;
import javax.swing.JToolBar;

/** ECG format shortcuts. */
public class WaveformToolBar extends JToolBar {

  public WaveformToolBar(WaveView view) {
    JButton two = new JButton("2");
    two.addActionListener(e -> view.setFormat(Format.TWO));
    add(two);
    JButton four = new JButton("4");
    four.addActionListener(e -> view.setFormat(Format.FOUR));
    add(four);
    JButton twelve = new JButton("12");
    twelve.addActionListener(e -> view.setFormat(Format.DEFAULT));
    add(twelve);
  }
}
