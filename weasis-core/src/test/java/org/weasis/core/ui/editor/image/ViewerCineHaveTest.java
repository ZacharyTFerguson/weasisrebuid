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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.gui.Insertable;
import org.weasis.core.api.gui.util.ActionW;
import org.weasis.core.api.gui.util.SliderCineListener;
import org.weasis.core.api.media.data.ImageElement;
import org.weasis.core.api.media.data.Series;

class ViewerCineHaveTest {

  @Test
  void viewerToolbarSetsDocumentedMouseLeftActionTokens() {
    ViewerToolBar bar = new ViewerToolBar();
    assertEquals("Viewer", bar.getComponentName());
    assertEquals(Insertable.Type.TOOLBAR, bar.getType());
    assertEquals(ActionW.WINLEVEL.cmd(), bar.getSelected());
    DefaultView2d<?> view = new DefaultView2d<>();
    bar.bind(view);
    assertEquals(MouseActions.WINLEVEL, view.getMouseActions().getLeft());
    bar.setSelected(ActionW.SCROLL_SERIES.cmd());
    bar.apply(view);
    assertEquals(MouseActions.SEQUENCE, view.getMouseActions().getLeft());
    assertEquals(10, ViewerToolBar.ACTIONS.length);
    assertEquals(ActionW.NONE.cmd(), ViewerToolBar.ACTIONS[ViewerToolBar.ACTIONS.length - 1]);
  }

  @Test
  void sequenceDragScrollsAndRotationDragTurns() {
    DefaultView2d<?> view = new DefaultView2d<>();
    view.setFrameIndex(2);
    view.getEventManager().apply(ActionW.SCROLL_SERIES.cmd(), 0, 5);
    assertEquals(3, view.getFrameIndex());
    view.getEventManager().apply(MouseActions.SCROLL, 0, -5);
    assertEquals(2, view.getFrameIndex());
    view.getEventManager().apply(ActionW.ROTATION.cmd(), 15, 0);
    assertEquals(15.0, view.getRotation());
  }

  @Test
  void cineTickAdvancesFramesLoopsAndSwitchesSeriesPixels() {
    ImageElement a = new ImageElement();
    a.setImage(new BufferedImage(4, 2, BufferedImage.TYPE_INT_RGB));
    ImageElement b = new ImageElement();
    b.setImage(new BufferedImage(8, 2, BufferedImage.TYPE_INT_RGB));
    Series<ImageElement> series = new Series<>();
    series.addMedia(a);
    series.addMedia(b);
    DefaultView2d<?> view = new DefaultView2d<>();
    view.setSeries(series);
    view.setFrameIndex(0);
    assertEquals(4, view.getSourceImage().getWidth());
    assertEquals(2, view.getFrameCount());

    int[] last = {0};
    SliderCineListener cine =
        new SliderCineListener(ActionW.CINE, 0, view.getFrameCount() - 1, 0) {
          @Override
          public void stateChanged(int value) {
            last[0] = value;
            view.setFrameIndex(value);
          }
        };
    cine.start();
    cine.tick();
    assertEquals(1, last[0]);
    assertEquals(1, view.getFrameIndex());
    assertEquals(8, view.getSourceImage().getWidth());
    assertNotNull(view.lastCineEvent());
    assertEquals(1, view.lastCineEvent().getFrameIndex());
    cine.tick();
    assertEquals(0, view.getFrameIndex());
    assertEquals(4, view.getSourceImage().getWidth());
    cine.stop();
    cine.tick();
    assertEquals(0, view.getFrameIndex());
    cine.setLoop(false);
    cine.start();
    cine.setSliderValue(1);
    cine.tick();
    assertTrue(!cine.isCineRunning());
    assertEquals(1, view.getFrameIndex());
  }
}
