/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.tp.raven.datetime.component;

import java.awt.BorderLayout;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.swing.JPanel;

public class PanelPopupEditor extends JPanel {
  public PanelPopupEditor() {
    super(new BorderLayout());
  }

  public LocalDate getDate() {
    return LocalDate.now();
  }

  public void setDate(LocalDate date) {}

  public LocalTime getTime() {
    return LocalTime.now();
  }

  public void setTime(LocalTime time) {}
}
