/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.base.viewer2d;

import java.util.List;
import org.weasis.core.api.media.data.ImageElement;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.api.media.data.MediaSeries;
import org.weasis.core.ui.editor.image.ImageViewerPlugin;

public class View2dContainer extends ImageViewerPlugin<MediaElement> {

  public static final String NAME = "Image 2D";

  private static volatile View2dContainer selected;

  private final View2d view2d = new View2d();

  public View2dContainer() {
    super(NAME);
    add(view2d);
    selected = this;
  }

  public static View2dContainer selected() {
    return selected;
  }

  public View2d getView2d() {
    return view2d;
  }

  @Override
  public synchronized void addSeries(MediaSeries<MediaElement> sequence) {
    super.addSeries(sequence);
    if (sequence == null) {
      return;
    }
    List<MediaElement> medias = sequence.getMedias();
    if (medias.isEmpty()) {
      return;
    }
    MediaElement first = medias.getFirst();
    if (first instanceof ImageElement image && image.getImage() != null) {
      view2d.setSourceImage(image.getImage());
    }
  }
}
