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
import java.time.LocalDate;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class DatePicker extends JPanel implements DateSelectionAble {
  private final JTextField field = new JTextField(10);
  private LocalDate date = LocalDate.now();

  public DatePicker() {
    super(new BorderLayout());
    field.setText(date.toString());
    JButton pick = new JButton("...");
    pick.addActionListener(e -> setSelectedDate(LocalDate.parse(field.getText())));
    add(field, BorderLayout.CENTER);
    add(pick, BorderLayout.EAST);
  }

  @Override
  public LocalDate getSelectedDate() {
    return date;
  }

  @Override
  public void setSelectedDate(LocalDate date) {
    this.date = date == null ? LocalDate.now() : date;
    field.setText(this.date.toString());
  }
}
