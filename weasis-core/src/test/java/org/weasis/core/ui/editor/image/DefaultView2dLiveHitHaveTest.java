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

import static org.junit.jupiter.api.Assertions.assertSame;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

class DefaultView2dLiveHitHaveTest {

  @Test
  void liveAtScreenPrefersRasterOverEmptyOverlappingSiblingAfterResize() {
    Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());
    DefaultView2d<?> chest = new DefaultView2d<>();
    DefaultView2d<?> empty = new DefaultView2d<>();
    chest.setSourceImage(new BufferedImage(80, 80, BufferedImage.TYPE_BYTE_GRAY));
    JFrame frame = new JFrame();
    try {
      JPanel panel = new JPanel(null);
      panel.setPreferredSize(new Dimension(500, 400));
      chest.setBounds(0, 0, 160, 160);
      empty.setBounds(200, 0, 160, 160);
      panel.add(chest);
      panel.add(empty);
      frame.setContentPane(panel);
      frame.pack();
      frame.setVisible(true);
      Point onChest = offset(chest, 40, 40);
      assertSame(chest, DefaultView2d.atScreen(onChest, List.of(chest, empty)));
      frame.setSize(800, 600);
      chest.setBounds(20, 20, 180, 180);
      empty.setBounds(0, 0, 700, 500);
      panel.doLayout();
      frame.validate();
      DefaultView2d.dropBoundsCache();
      Point after = offset(chest, 50, 50);
      assertSame(chest, DefaultView2d.atScreen(after, List.of(chest, empty)));
    } finally {
      frame.dispose();
    }
  }

  static Point offset(DefaultView2d<?> view, int dx, int dy) {
    Point p = new Point(view.getLocationOnScreen());
    p.translate(dx, dy);
    return p;
  }
}
