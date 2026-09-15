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
import org.weasis.core.ui.model.layer.AbstractInfoLayer;
import org.weasis.core.ui.model.layer.LayerAnnotation;

/**
 * Pixel overlay: patient/study/image annotations on {@link View2d}. Space/I cycles three states.
 */
public class InfoLayer extends AbstractInfoLayer {

  public String overlayText(String patient, String modality, double window, double level) {
    if (!isVisible()) {
      return "";
    }
    String wl = "W:" + (int) window + " L:" + (int) level;
    if (isMinimal()) {
      return wl;
    }
    String name =
        getLayerAnnotation().isItemVisible(LayerAnnotation.PATIENT)
            ? (patient == null ? "" : patient)
            : "";
    String mod = modality == null ? "" : modality;
    return (name + "  " + mod + "  " + wl).trim();
  }

  public String overlayText(View2d view) {
    if (view == null) {
      return overlayText("", "", 0, 0);
    }
    if (view.getDataset() == null) {
      return overlayText("", "", view.getWindow(), view.getLevel());
    }
    Attributes dcm = view.getDataset();
    return overlayText(
        dcm.getString(Tag.PatientName, ""),
        dcm.getString(Tag.Modality, ""),
        view.getWindow(),
        view.getLevel());
  }

  public void paint(Graphics2D g, View2d view) {
    if (!isVisible() || g == null || view == null) {
      return;
    }
    String line = overlayText(view);
    if (line.isBlank()) {
      return;
    }
    g.setColor(Color.YELLOW);
    int y = isMinimal() ? 16 : Math.max(16, view.getHeight() - 12);
    g.drawString(line, 8, y);
  }
}
