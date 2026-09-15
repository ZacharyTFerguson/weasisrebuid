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

public class ChannelHistogramPanel extends JPanel {

  private final HistogramPanel red = new HistogramPanel();
  private final HistogramPanel green = new HistogramPanel();
  private final HistogramPanel blue = new HistogramPanel();

  public HistogramPanel getRed() {
    return red;
  }

  public HistogramPanel getGreen() {
    return green;
  }

  public HistogramPanel getBlue() {
    return blue;
  }
}
