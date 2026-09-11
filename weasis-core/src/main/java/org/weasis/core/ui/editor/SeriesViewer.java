/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.editor;

import java.util.List;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.api.media.data.MediaSeries;

public interface SeriesViewer<E extends MediaElement> {

  void addSeries(MediaSeries<E> sequence);

  void removeSeries(MediaSeries<E> sequence);

  List<MediaSeries<E>> getOpenSeries();

  MediaSeries<E> getSelectedSeries();

  void close();

  String getPluginName();

  String getDockableUID();

  void setSelected(boolean selected);

  boolean isSelected();
}
