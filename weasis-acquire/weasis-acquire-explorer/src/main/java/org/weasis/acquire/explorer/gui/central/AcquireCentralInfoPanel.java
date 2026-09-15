/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.explorer.gui.central;

import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.weasis.acquire.explorer.gui.central.tumbnail.AcquireCentralThumbnailModel;

/** Series name and still count for the selected album group. */
public class AcquireCentralInfoPanel extends JPanel {

  private final JLabel label = new JLabel();
  private String series;

  public AcquireCentralInfoPanel() {
    super(new BorderLayout());
    label.setName("series-info");
    add(label, BorderLayout.CENTER);
  }

  public void showSeries(String series, AcquireCentralThumbnailModel model) {
    this.series = series;
    int n = series == null || model == null ? 0 : model.itemsInSeries(series).size();
    label.setText(series == null ? "" : series + " (" + n + ")");
  }

  public String series() {
    return series;
  }

  public String text() {
    return label.getText();
  }

  public JLabel label() {
    return label;
  }
}
