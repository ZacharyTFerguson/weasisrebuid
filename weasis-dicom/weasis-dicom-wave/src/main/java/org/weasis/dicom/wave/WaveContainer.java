/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.wave;

import java.awt.BorderLayout;
import java.util.List;
import org.weasis.core.api.gui.Insertable;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.api.media.data.MediaSeries;
import org.weasis.core.ui.editor.image.ViewerPlugin;

public class WaveContainer extends ViewerPlugin<MediaElement> {

  public static final String NAME = "DICOM ECG Viewer";
  private final WaveView waveView = new WaveView();
  private final WaveformToolBar waveformToolBar = new WaveformToolBar();

  public WaveContainer() {
    super(NAME);
    waveformToolBar.bind(waveView);
    add(waveView, BorderLayout.CENTER);
    fillSeriesViewerUi();
  }

  void fillSeriesViewerUi() {
    List<Insertable> bars = getSeriesViewerUI().getToolBar();
    bars.clear();
    bars.add(waveformToolBar);
  }

  public WaveView getWaveView() {
    return waveView;
  }

  public WaveformToolBar getWaveformToolBar() {
    return waveformToolBar;
  }

  @Override
  public synchronized void addSeries(MediaSeries<MediaElement> sequence) {
    super.addSeries(sequence);
    if (sequence == null) {
      return;
    }
    for (MediaElement media : sequence.getMedias()) {
      waveView.display(media);
      break;
    }
  }
}
