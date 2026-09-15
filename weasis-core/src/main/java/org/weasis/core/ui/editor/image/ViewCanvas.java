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

import java.awt.Graphics2D;
import java.util.List;
import org.weasis.core.api.image.OpManager;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.api.media.data.MediaSeries;
import org.weasis.core.ui.model.graphic.Graphic;

/**
 * Series-bound 2D canvas used by DICOM {@code View2d} and MPR planes. Graphics and SEG overlay
 * visibility live here so tools can attach without knowing the Swing subclass.
 */
public interface ViewCanvas extends Canvas {

  MediaSeries<? extends MediaElement> getSeries();

  ImageViewerEventManager getEventManager();

  OpManager getDisplayOpManager();

  List<Graphic> getGraphicList();

  void addGraphic(Graphic graphic);

  void removeGraphic(Graphic graphic);

  boolean isSegmentationsVisible();

  void setSegmentationsVisible(boolean visible);

  void paintView(Graphics2D g, boolean overlays);
}
