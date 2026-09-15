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

import java.util.List;
import org.weasis.core.api.image.OpManager;
import org.weasis.core.api.image.SimpleOpManager;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.api.media.data.MediaSeries;

/** Image viewer with a display op chain. */
public abstract class ImageViewerPlugin<E extends MediaElement> extends ViewerPlugin<E> {

  private final SimpleOpManager displayOp;

  protected ImageViewerPlugin(String pluginName) {
    super(pluginName);
    this.displayOp = SimpleOpManager.view2dChain();
  }

  public OpManager getDisplayOpManager() {
    return displayOp;
  }

  public void setLayoutCount(int n) {}

  public int getLayoutCount() {
    return 1;
  }

  /** Hanging protocol grid: MG 2×2, CR/DX 1×2, CT/MR/PT 1×1. */
  public void applyHanging(int rows, int columns) {
    setLayoutCount(Math.max(1, rows) * Math.max(1, columns));
  }

  public void hangSeries(List<MediaSeries<E>> series) {
    if (series == null || series.isEmpty()) {
      return;
    }
    for (MediaSeries<E> sequence : series) {
      addSeries(sequence);
    }
  }

  /** True when hanging layout still has a clone or empty cell for another series. */
  public boolean hasHangSlot() {
    return false;
  }

  public void resetDisplay() {}

  public void applyPreset(int index) {}

  /** Edit > Select All (graphics). */
  public void selectAllGraphics() {}

  /** Edit > Deselect All (graphics). */
  public void deselectAllGraphics() {}
}
