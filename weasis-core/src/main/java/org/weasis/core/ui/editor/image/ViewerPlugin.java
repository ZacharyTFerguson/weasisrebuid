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

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import javax.swing.JPanel;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.api.media.data.MediaSeries;
import org.weasis.core.ui.editor.SeriesViewer;

/** Central-panel viewer. Factories create instances on demand. */
public abstract class ViewerPlugin<E extends MediaElement> extends JPanel
    implements SeriesViewer<E> {

  private final String dockableUID;
  private final String pluginName;
  private final List<MediaSeries<E>> openSeries = new ArrayList<>();
  private MediaSeries<E> selectedSeries;
  private boolean selected;

  protected ViewerPlugin(String pluginName) {
    super(new BorderLayout());
    this.pluginName = pluginName == null ? "Viewer" : pluginName;
    this.dockableUID = UUID.randomUUID().toString();
  }

  @Override
  public synchronized void addSeries(MediaSeries<E> sequence) {
    if (sequence != null && !openSeries.contains(sequence)) {
      openSeries.add(sequence);
      selectedSeries = sequence;
    }
  }

  @Override
  public synchronized void removeSeries(MediaSeries<E> sequence) {
    openSeries.remove(sequence);
    if (selectedSeries == sequence) {
      selectedSeries = openSeries.isEmpty() ? null : openSeries.getLast();
    }
  }

  @Override
  public synchronized List<MediaSeries<E>> getOpenSeries() {
    return Collections.unmodifiableList(new ArrayList<>(openSeries));
  }

  @Override
  public synchronized MediaSeries<E> getSelectedSeries() {
    return selectedSeries;
  }

  @Override
  public void close() {
    openSeries.clear();
    selectedSeries = null;
  }

  @Override
  public String getPluginName() {
    return pluginName;
  }

  @Override
  public String getDockableUID() {
    return dockableUID;
  }

  @Override
  public void setSelected(boolean selected) {
    this.selected = selected;
  }

  @Override
  public boolean isSelected() {
    return selected;
  }
}
