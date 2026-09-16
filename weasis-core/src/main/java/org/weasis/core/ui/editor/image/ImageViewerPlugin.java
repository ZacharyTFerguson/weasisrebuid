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

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.util.List;
import javax.swing.JComponent;
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

  /**
   * Explorer series drop onto a view. Default fills the next hang slot via {@link #addSeries};
   * viewers override to fill the dropped cell without opening a tab.
   */
  public void dropSeries(MediaSeries<E> sequence, JComponent onto) {
    addSeries(sequence);
  }

  /** View cell under a drop point in this plugin's coordinates; default is the plugin itself. */
  public JComponent dropCellAt(Point p) {
    return this;
  }

  /**
   * View cell whose on-screen bounds contain {@code screen}. Default is null so docking glass drops
   * fall through to {@link #dropCellAt}.
   */
  public JComponent dropCellAtScreen(Point screen) {
    return null;
  }

  /** True when hanging layout still has a clone or empty cell for another series. */
  public boolean hasHangSlot() {
    return false;
  }

  /**
   * SEG / RT / PR / KO overlay. Default is a no-op so overlays never occupy hang slots via {@code
   * addSeries}.
   */
  public void applyOverlay(MediaSeries<E> sequence) {
    // overlays do not create a tab or fill hanging cells
  }

  public void resetDisplay() {}

  public void applyPreset(int index) {}

  /** Nested or self {@link ImageViewerPlugin} under a tab / docking wrapper. */
  public static ImageViewerPlugin<?> pluginIn(Component c) {
    if (c instanceof ImageViewerPlugin<?> image) {
      return image;
    }
    return nestedPlugin(c);
  }

  static ImageViewerPlugin<?> nestedPlugin(Component c) {
    if (!(c instanceof Container box)) {
      return null;
    }
    for (Component child : box.getComponents()) {
      ImageViewerPlugin<?> found = pluginIn(child);
      if (found != null) {
        return found;
      }
    }
    return null;
  }

  public static ImageViewerPlugin<?> pluginAbove(Component c) {
    while (c != null) {
      if (c instanceof ImageViewerPlugin<?> image) {
        return image;
      }
      c = c.getParent();
    }
    return null;
  }

  /** Edit > Select All (graphics). */
  public void selectAllGraphics() {}

  /** Edit > Deselect All (graphics). */
  public void deselectAllGraphics() {}

  /** Explorer Delete: every canvas in this plugin, including empty hang cells. */
  public void deleteAllGraphics() {}

  /** Live canvas under {@code screen}. Default is every showing {@link DefaultView2d}. */
  public DefaultView2d<?> canvasAt(Point screen) {
    return DefaultView2d.atScreen(screen);
  }
}
