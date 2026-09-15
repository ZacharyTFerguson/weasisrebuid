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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.JTabbedPane;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.media.data.ImageElement;
import org.weasis.core.api.media.data.Series;

class ViewButtonHaveTest {

  @Test
  void areaIconPaintsFillAndPlayTogglesCine() {
    AreaIcon icon = new AreaIcon(Color.GREEN, 8);
    assertInstanceOf(Icon.class, icon);
    BufferedImage painted = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
    icon.paintIcon(null, painted.getGraphics(), 0, 0);
    assertNotEquals(0, painted.getRGB(0, 0));

    ImageElement a = new ImageElement();
    a.setImage(new BufferedImage(4, 2, BufferedImage.TYPE_INT_RGB));
    ImageElement b = new ImageElement();
    b.setImage(new BufferedImage(8, 2, BufferedImage.TYPE_INT_RGB));
    Series<ImageElement> series = new Series<>();
    series.addMedia(a);
    series.addMedia(b);
    DefaultView2d<?> view = new DefaultView2d<>();
    view.setSeries(series);
    PlayViewButton play = view.getPlayButton();
    assertTrue(view.getViewButtons().contains(play));
    assertEquals(PlayViewButton.PLAY, play.getText());
    assertTrue(play.hit(4, 4));
    assertFalse(play.hit(0, 0));
    assertTrue(view.clickViewButton(4, 4));
    assertTrue(view.cineListener().isCineRunning());
    assertEquals(PlayViewButton.STOP, play.getText());
    assertTrue(play.isSelected());
    assertTrue(view.clickViewButton(4, 4));
    assertFalse(view.cineListener().isCineRunning());
    assertEquals(PlayViewButton.PLAY, play.getText());
    view.toggleCine();
    assertTrue(play.isSelected());
  }

  @Test
  void tabPlacementMovesViewerTabs() {
    JTabbedPane tabs = new JTabbedPane();
    TabPlacement.apply(tabs, TabPlacement.TOP);
    assertEquals(JTabbedPane.TOP, TabPlacement.of(tabs));
    TabPlacement.apply(tabs, TabPlacement.BOTTOM);
    assertEquals(JTabbedPane.BOTTOM, tabs.getTabPlacement());
    TabPlacement.apply(tabs, 99);
    assertEquals(JTabbedPane.TOP, TabPlacement.of(tabs));
    TabPlacement.apply(null, TabPlacement.LEFT);
    assertEquals(TabPlacement.LEFT, TabPlacement.normalize(TabPlacement.LEFT));
  }
}
