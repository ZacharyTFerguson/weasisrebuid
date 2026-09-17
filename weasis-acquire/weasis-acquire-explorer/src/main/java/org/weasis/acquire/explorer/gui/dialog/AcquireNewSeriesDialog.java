/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.explorer.gui.dialog;

import java.util.List;
import javax.swing.JTextField;
import org.weasis.acquire.explorer.AcquireMediaInfo;
import org.weasis.acquire.explorer.core.bean.SeriesGroup;

/** Chrome that creates a {@link SeriesGroup} using NAME grouping. */
public class AcquireNewSeriesDialog {

  private final JTextField nameField = new JTextField(24);

  public AcquireNewSeriesDialog() {
    nameField.setName("seriesName");
  }

  public JTextField nameField() {
    return nameField;
  }

  public void setSeriesName(String name) {
    nameField.setText(name == null ? "" : name);
  }

  public String seriesName() {
    String text = nameField.getText();
    return text == null ? "" : text.trim();
  }

  public SeriesGroup createSeries() {
    return new SeriesGroup(SeriesGroup.Type.NAME, seriesName());
  }

  public SeriesGroup createSeries(List<? extends AcquireMediaInfo> media) {
    SeriesGroup group = createSeries();
    if (media != null) {
      for (AcquireMediaInfo item : media) {
        group.add(item);
      }
    }
    return group;
  }
}
