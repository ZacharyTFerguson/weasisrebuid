/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.au;

import java.awt.BorderLayout;
import java.util.List;
import org.weasis.core.api.gui.Insertable;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.api.media.data.MediaSeries;
import org.weasis.core.ui.editor.image.ViewerPlugin;

public class AuContainer extends ViewerPlugin<MediaElement> {

  public static final String NAME = "DICOM Audio Player";
  private final AuView auView = new AuView();
  private final AuToolBar toolBar = new AuToolBar(auView);

  public AuContainer() {
    super(NAME);
    add(auView, BorderLayout.CENTER);
    fillSeriesViewerUi();
  }

  void fillSeriesViewerUi() {
    List<Insertable> bars = getSeriesViewerUI().getToolBar();
    bars.clear();
    bars.add(toolBar);
  }

  public AuView getAuView() {
    return auView;
  }

  public AuToolBar getToolBar() {
    return toolBar;
  }

  @Override
  public synchronized void addSeries(MediaSeries<MediaElement> sequence) {
    super.addSeries(sequence);
    if (sequence == null) {
      return;
    }
    for (MediaElement media : sequence.getMedias()) {
      auView.display(media);
      break;
    }
  }
}
