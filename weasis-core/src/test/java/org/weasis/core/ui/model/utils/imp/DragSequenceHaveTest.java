/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.utils.imp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.weasis.core.ui.editor.image.DefaultView2d;
import org.weasis.core.ui.editor.image.Panner;
import org.weasis.core.ui.model.graphic.imp.line.LineGraphic;
import org.weasis.core.ui.model.utils.bean.PanPoint;

class DragSequenceHaveTest {

  @Test
  void handleDragMovesLineEndpoint() {
    LineGraphic line = new LineGraphic();
    line.setHandlePoint(0, new Point2D.Double(0, 0));
    line.setHandlePoint(1, new Point2D.Double(3, 0));
    DefaultDragSequence drag = line.dragHandle(1);
    assertTrue(drag.start(new Point2D.Double(3, 0)));
    assertTrue(drag.drag(new Point2D.Double(3, 4)));
    assertTrue(drag.complete());
    assertEquals(5.0, line.getLength(), 1e-9);
  }

  @Test
  void selectedDragTranslatesEveryHandle() {
    LineGraphic a = new LineGraphic();
    a.setHandlePoint(0, new Point2D.Double(0, 0));
    a.setHandlePoint(1, new Point2D.Double(3, 4));
    LineGraphic b = new LineGraphic();
    b.setHandlePoint(0, new Point2D.Double(10, 0));
    b.setHandlePoint(1, new Point2D.Double(13, 0));
    SelectedDragSequence selected = new SelectedDragSequence(List.of(a, b));
    assertTrue(selected.start(new Point2D.Double(0, 0)));
    assertTrue(selected.drag(new Point2D.Double(5, 0)));
    assertEquals(5.0, a.getHandlePoint(0).getX(), 1e-9);
    assertEquals(8.0, a.getHandlePoint(1).getX(), 1e-9);
    assertEquals(15.0, b.getHandlePoint(0).getX(), 1e-9);
  }

  @Test
  void labelDragAndPannerRecordImageSpace() {
    DefaultGraphicLabel label = new DefaultGraphicLabel();
    label.setOffset(1, 2);
    DragLabelSequence seq = new DragLabelSequence(label);
    assertTrue(seq.start(new Point2D.Double(0, 0)));
    assertTrue(seq.drag(new Point2D.Double(4, 6)));
    assertEquals(5.0, label.getOffsetX(), 1e-9);
    assertEquals(8.0, label.getOffsetY(), 1e-9);

    DefaultView2d<?> view = new DefaultView2d<>();
    view.setSourceImage(new BufferedImage(50, 50, BufferedImage.TYPE_BYTE_GRAY));
    Panner panner = new Panner();
    panner.setSize(100, 100);
    panner.bind(view);
    panner.panAt(50, 50);
    PanPoint last = panner.lastPan();
    assertEquals(PanPoint.State.CENTER, last.getState());
    assertEquals(25.0, last.getX(), 1e-9);
    assertEquals(25.0, last.getY(), 1e-9);
  }
}
