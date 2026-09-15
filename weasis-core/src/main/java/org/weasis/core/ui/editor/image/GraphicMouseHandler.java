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

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import org.weasis.core.ui.model.graphic.Graphic;
import org.weasis.core.ui.model.graphic.imp.area.SelectGraphic;

/**
 * Click-select, Shift+click toggle, and drag select-area (SHORTCUTS.md Graphics) when not drawing.
 */
public class GraphicMouseHandler {

  private SelectGraphic rubber;
  private boolean selecting;

  public boolean isSelecting() {
    return selecting;
  }

  public boolean mousePressed(MouseEvent e, DefaultView2d<?> view) {
    if (e == null || view == null) {
      return false;
    }
    Graphic hit = view.graphicAt(e.getX(), e.getY());
    if (hit != null) {
      view.selectGraphic(hit, e.isShiftDown());
      selecting = false;
      rubber = null;
      return true;
    }
    if (ImageViewerEventManager.drawingAction(view.getMouseActions().getLeft())) {
      return false;
    }
    if (!e.isShiftDown()) {
      view.deselectAllGraphics();
    }
    rubber = new SelectGraphic();
    Point2D.Double p = new Point2D.Double(e.getX(), e.getY());
    rubber.setHandlePoint(0, p);
    rubber.setHandlePoint(1, p);
    view.addGraphic(rubber);
    selecting = true;
    return true;
  }

  public boolean mouseDragged(MouseEvent e, DefaultView2d<?> view) {
    if (!selecting || rubber == null || e == null) {
      return false;
    }
    rubber.setHandlePoint(1, new Point2D.Double(e.getX(), e.getY()));
    if (view != null) {
      view.repaint();
    }
    return true;
  }

  public boolean mouseReleased(MouseEvent e, DefaultView2d<?> view) {
    if (!selecting || rubber == null || view == null) {
      selecting = false;
      rubber = null;
      return false;
    }
    if (e != null) {
      rubber.setHandlePoint(1, new Point2D.Double(e.getX(), e.getY()));
    }
    view.selectIntersecting(rubber);
    view.removeGraphic(rubber);
    selecting = false;
    rubber = null;
    return true;
  }
}
