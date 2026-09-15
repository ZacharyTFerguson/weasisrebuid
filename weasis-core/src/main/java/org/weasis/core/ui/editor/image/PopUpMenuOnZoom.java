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

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import org.weasis.core.api.image.AffineTransformOp;

/** Context menu of zoom presets (best fit / real size / numeric). */
public class PopUpMenuOnZoom extends JPopupMenu {

  private double selected = AffineTransformOp.ZOOM_BEST_FIT;

  public PopUpMenuOnZoom() {
    add(item("Best Fit", AffineTransformOp.ZOOM_BEST_FIT));
    add(item("Real Size", AffineTransformOp.ZOOM_REAL_SIZE));
    add(item("1x", 1.0));
    add(item("2x", 2.0));
    add(item("4x", 4.0));
  }

  public double selectedZoom() {
    return selected;
  }

  public void apply(DefaultView2d<?> view) {
    if (view != null) {
      view.setZoom(selected);
    }
  }

  private AbstractAction item(String label, double zoom) {
    return new AbstractAction(label) {
      @Override
      public void actionPerformed(ActionEvent e) {
        selected = zoom;
      }
    };
  }
}
