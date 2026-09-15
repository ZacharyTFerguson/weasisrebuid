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
import javax.swing.JButton;
import org.weasis.core.api.image.AffineTransformOp;
import org.weasis.core.ui.util.WtoolBar;

/**
 * 2D zoom chrome. Documented magic values: {@code -200} best fit (default), {@code -100} real size;
 * otherwise 0.0–12.0.
 */
public class ZoomToolBar extends WtoolBar {

  public static final String NAME = "Zoom";

  private double selected = AffineTransformOp.ZOOM_BEST_FIT;

  public ZoomToolBar() {
    super(NAME, 20);
    add(button("Best Fit", AffineTransformOp.ZOOM_BEST_FIT));
    add(button("Real Size", AffineTransformOp.ZOOM_REAL_SIZE));
    add(button("1x", 1.0));
    add(button("2x", 2.0));
    add(button("4x", 4.0));
  }

  public double selectedZoom() {
    return selected;
  }

  public void setSelectedZoom(double zoom) {
    this.selected = zoom;
  }

  public void apply(DefaultView2d<?> view) {
    if (view != null) {
      view.setZoom(selected);
    }
  }

  public static boolean isBestFit(double zoom) {
    return zoom == AffineTransformOp.ZOOM_BEST_FIT;
  }

  public static boolean isRealSize(double zoom) {
    return zoom == AffineTransformOp.ZOOM_REAL_SIZE;
  }

  private JButton button(String label, double zoom) {
    JButton button =
        new JButton(
            new AbstractAction(label) {
              @Override
              public void actionPerformed(ActionEvent e) {
                setSelectedZoom(zoom);
              }
            });
    button.setToolTipText(label + " (" + zoom + ")");
    return button;
  }
}
