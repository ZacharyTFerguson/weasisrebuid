/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.base.ui.gui;

import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.api.media.data.MediaSeries;
import org.weasis.core.ui.editor.image.ViewerPlugin;

/**
 * Blank fallback viewer for {@code image/dummy}. The main window must not attach this at startup;
 * File &gt; Import opens a real series viewer instead.
 */
public class DummyViewerPlugin extends ViewerPlugin<MediaElement> {

  public static final String EMPTY_STATUS = "No series";

  private final JLabel status;

  public DummyViewerPlugin() {
    super(DummySeriesViewerFactory.NAME);
    status = new JLabel(EMPTY_STATUS, SwingConstants.CENTER);
    status.setName("dummy-status");
    add(status, BorderLayout.CENTER);
  }

  public String statusText() {
    return status.getText();
  }

  @Override
  public synchronized void addSeries(MediaSeries<MediaElement> sequence) {
    super.addSeries(sequence);
    refreshStatus();
  }

  @Override
  public synchronized void removeSeries(MediaSeries<MediaElement> sequence) {
    super.removeSeries(sequence);
    refreshStatus();
  }

  @Override
  public void close() {
    super.close();
    refreshStatus();
  }

  void refreshStatus() {
    status.setText(labelFor(getOpenSeries().size()));
  }

  static String labelFor(int n) {
    return n <= 0 ? EMPTY_STATUS : n + " series";
  }
}
