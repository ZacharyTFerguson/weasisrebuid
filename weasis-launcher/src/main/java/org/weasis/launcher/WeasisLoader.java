/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.launcher;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

public class WeasisLoader {
  private final JFrame frame = new JFrame("Weasis");
  private final JProgressBar bar = new JProgressBar(0, 100);
  private final JLabel label = new JLabel("Loading…", SwingConstants.CENTER);

  public WeasisLoader() {
    frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    frame.setSize(360, 120);
    frame.setLayout(new BorderLayout());
    frame.getContentPane().setBackground(Color.DARK_GRAY);
    label.setForeground(Color.WHITE);
    frame.add(label, BorderLayout.CENTER);
    frame.add(bar, BorderLayout.SOUTH);
    frame.setLocationRelativeTo(null);
  }

  public void open() {
    frame.setVisible(true);
  }

  public void close() {
    frame.dispose();
  }

  public void setMax(int max) {
    bar.setMaximum(Math.max(1, max));
  }

  public void setValue(int value) {
    bar.setValue(value);
  }

  public void writeLabel(String text) {
    label.setText(text == null ? "" : text);
  }
}
