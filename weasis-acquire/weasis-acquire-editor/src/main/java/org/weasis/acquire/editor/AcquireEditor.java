/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.editor;

import javax.swing.JLabel;
import org.weasis.core.ui.docking.PluginTool;

/** Photo Editor: crop, rotate, contrast, drawings, measurements, calibration. */
public class AcquireEditor extends PluginTool {

  public static final String NAME = "Photo Editor";

  public AcquireEditor() {
    super(NAME, 0);
    add(new JLabel(NAME));
  }

  public void dispose() {
    closeDockable();
  }
}
