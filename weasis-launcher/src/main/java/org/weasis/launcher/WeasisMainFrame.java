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
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class WeasisMainFrame extends JFrame implements WeasisMainFrameMBean {
  public WeasisMainFrame() {
    super(System.getProperty("weasis.name", "Weasis"));
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    setLayout(new BorderLayout());
    setSize(960, 640);
  }

  @Override
  public String getWeasisVersion() {
    return System.getProperty("weasis.version", "4.7.3");
  }

  @Override
  public void toFrontWindow() {
    toFront();
    setAlwaysOnTop(true);
    setAlwaysOnTop(false);
  }
}
