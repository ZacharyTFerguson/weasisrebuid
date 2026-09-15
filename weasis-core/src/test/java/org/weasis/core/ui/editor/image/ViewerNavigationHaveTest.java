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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.media.data.ImageElement;
import org.weasis.core.api.media.data.Series;
import org.weasis.core.ui.model.graphic.imp.line.LineGraphic;

class ViewerNavigationHaveTest {

  @Test
  void imageKeysMoveWithinSeries() {
    DefaultView2d<?> view = new DefaultView2d<>();
    view.setSeries(frames(20));
    view.setFrameIndex(5);
    view.getEventManager().keyPressed(key(view, KeyEvent.VK_DOWN, 0));
    assertEquals(6, view.getFrameIndex());
    view.getEventManager().keyPressed(key(view, KeyEvent.VK_UP, 0));
    assertEquals(5, view.getFrameIndex());
    view.getEventManager().keyPressed(key(view, KeyEvent.VK_DOWN, InputEvent.SHIFT_DOWN_MASK));
    assertEquals(15, view.getFrameIndex());
    view.getEventManager().keyPressed(key(view, KeyEvent.VK_END, 0));
    assertEquals(19, view.getFrameIndex());
    view.getEventManager().keyPressed(key(view, KeyEvent.VK_HOME, 0));
    assertEquals(0, view.getFrameIndex());
  }

  @Test
  void seriesKeysStayInStudyAndCtrlMovesStudyAndPatient() {
    Series<ImageElement> a = frames(2);
    Series<ImageElement> b = frames(3);
    Series<ImageElement> c = frames(1);
    Series<ImageElement> d = frames(1);
    DefaultView2d<?> view = new DefaultView2d<>();
    view.setSeriesStack(List.of(a, b, c, d), new int[] {0, 0, 1, 2}, new int[] {0, 0, 0, 1});
    assertSame(a, view.getSeries());
    view.getEventManager().keyPressed(key(view, KeyEvent.VK_RIGHT, 0));
    assertSame(b, view.getSeries());
    assertEquals(0, view.getFrameIndex());
    view.getEventManager().keyPressed(key(view, KeyEvent.VK_RIGHT, 0));
    assertSame(b, view.getSeries());
    view.getEventManager().keyPressed(key(view, KeyEvent.VK_PAGE_UP, 0));
    assertSame(a, view.getSeries());
    view.getEventManager().keyPressed(key(view, KeyEvent.VK_PAGE_DOWN, 0));
    assertSame(b, view.getSeries());
    view.getEventManager().keyPressed(key(view, KeyEvent.VK_RIGHT, InputEvent.CTRL_DOWN_MASK));
    assertSame(c, view.getSeries());
    view.getEventManager().keyPressed(key(view, KeyEvent.VK_DOWN, InputEvent.CTRL_DOWN_MASK));
    assertSame(d, view.getSeries());
    view.getEventManager().keyPressed(key(view, KeyEvent.VK_HOME, InputEvent.CTRL_DOWN_MASK));
    assertSame(a, view.getSeries());
  }

  @Test
  void altArrowsPanAndF11AndAltS() {
    DefaultView2d<?> view = new DefaultView2d<>();
    view.getEventManager().keyPressed(key(view, KeyEvent.VK_RIGHT, InputEvent.ALT_DOWN_MASK));
    assertEquals(5.0, view.getPanX(), 1e-9);
    view.getEventManager()
        .keyPressed(
            key(view, KeyEvent.VK_DOWN, InputEvent.ALT_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
    assertEquals(10.0, view.getPanY(), 1e-9);
    assertFalse(view.isFullScreen());
    view.getEventManager().keyPressed(key(view, KeyEvent.VK_F11, 0));
    assertTrue(view.isFullScreen());
    assertTrue(view.isSegmentationsVisible());
    view.getEventManager().keyPressed(key(view, KeyEvent.VK_S, InputEvent.ALT_DOWN_MASK));
    assertFalse(view.isSegmentationsVisible());
  }

  @Test
  void graphicsPanePaintsBoundDrawings() {
    DefaultView2d<?> view = new DefaultView2d<>();
    LineGraphic line = new LineGraphic();
    line.setHandlePoint(0, new Point2D.Double(0, 0));
    line.setHandlePoint(1, new Point2D.Double(4, 0));
    view.addGraphic(line);
    GraphicsPane pane = new GraphicsPane();
    pane.bind(view);
    assertSame(view, pane.boundView());
    assertEquals(1, pane.getGraphicList().size());
  }

  static Series<ImageElement> frames(int n) {
    Series<ImageElement> series = new Series<>();
    for (int i = 0; i < n; i++) {
      ImageElement media = new ImageElement();
      media.setImage(new BufferedImage(2, 2, BufferedImage.TYPE_BYTE_GRAY));
      series.addMedia(media);
    }
    return series;
  }

  static KeyEvent key(DefaultView2d<?> view, int code, int mods) {
    return new KeyEvent(view, KeyEvent.KEY_PRESSED, 0L, mods, code, (char) code);
  }
}
