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
import java.util.List;
import javax.swing.JPanel;

/** Series buttons plus central album thumbnails and info. */
public class AcquireTabPanel extends JPanel {

  private final SeriesButtonList seriesButtons = new SeriesButtonList();
  private final AcquireCentralImagePanel imagePanel = new AcquireCentralImagePanel();
  private final AcquireCentralInfoPanel infoPanel = new AcquireCentralInfoPanel();
  private String selectedSeries;

  public AcquireTabPanel() {
    super(new BorderLayout());
    seriesButtons.setOnSelect(this::selectSeries);
    add(seriesButtons, BorderLayout.NORTH);
    add(imagePanel, BorderLayout.CENTER);
    add(infoPanel, BorderLayout.SOUTH);
  }

  public SeriesButtonList seriesButtons() {
    return seriesButtons;
  }

  public AcquireCentralImagePanel imagePanel() {
    return imagePanel;
  }

  public AcquireCentralInfoPanel infoPanel() {
    return infoPanel;
  }

  public String selectedSeries() {
    return selectedSeries;
  }

  public void refreshSeries() {
    List<String> names = imagePanel.model().seriesNames();
    String keep = selectedSeries;
    seriesButtons.setSeries(names);
    if (keep != null && names.contains(keep)) {
      selectSeries(keep);
    } else if (!names.isEmpty()) {
      selectSeries(names.get(0));
    } else {
      selectedSeries = null;
      imagePanel.showSeries(null);
      infoPanel.showSeries(null, imagePanel.model());
    }
  }

  public void selectSeries(String series) {
    if (series == null) {
      return;
    }
    selectedSeries = series;
    seriesButtons.select(series);
    imagePanel.showSeries(series);
    infoPanel.showSeries(series, imagePanel.model());
  }
}
