/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package org.weasis.core.ui.tp.raven.datetime;

import java.awt.BorderLayout;
import java.time.LocalTime;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class TimePicker extends JPanel implements TimeSelectionAble {
  private final JTextField field = new JTextField(8);
  private LocalTime time = LocalTime.now().withSecond(0).withNano(0);

  public TimePicker() {
    super(new BorderLayout());
    field.setText(time.toString());
    add(field, BorderLayout.CENTER);
  }

  @Override
  public LocalTime getSelectedTime() {
    return time;
  }

  @Override
  public void setSelectedTime(LocalTime time) {
    this.time = time == null ? LocalTime.NOON : time;
    field.setText(this.time.toString());
  }
}
