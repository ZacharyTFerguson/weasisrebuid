/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.editor.image;

import javax.swing.JPanel;

public class HistogramPanel extends JPanel {

  private HistogramData data = new HistogramData(256);

  public HistogramData getData() {
    return data;
  }

  public void setData(HistogramData data) {
    this.data = data == null ? new HistogramData(256) : data;
    repaint();
  }
}
