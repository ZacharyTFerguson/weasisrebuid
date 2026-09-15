/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d;

import java.awt.Color;
import java.awt.Graphics2D;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;

/** Pixel overlay: patient/study/image annotations on {@link View2d}. */
public class InfoLayer {

  private boolean visible = true;

  public boolean isVisible() {
    return visible;
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  public void paint(Graphics2D g, View2d view) {
    if (!visible || g == null || view == null || view.getDataset() == null) {
      return;
    }
    Attributes dcm = view.getDataset();
    g.setColor(Color.YELLOW);
    String line =
        dcm.getString(Tag.PatientName, "")
            + "  "
            + dcm.getString(Tag.Modality, "")
            + "  W:"
            + (int) view.getWindow()
            + " L:"
            + (int) view.getLevel();
    g.drawString(line.trim(), 8, view.getHeight() - 12);
  }
}
