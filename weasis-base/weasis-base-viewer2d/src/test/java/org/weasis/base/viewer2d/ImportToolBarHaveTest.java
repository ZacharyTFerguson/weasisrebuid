/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.base.viewer2d;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.core.api.gui.Insertable;
import org.weasis.imageio.codec.ImageioCodec;

class ImportToolBarHaveTest {

  @Test
  void importFilesLoadsStillsIntoViewer(@TempDir Path dir) throws Exception {
    Path png = dir.resolve("still.png");
    BufferedImage img = new BufferedImage(5, 3, BufferedImage.TYPE_INT_RGB);
    img.setRGB(0, 0, 0x00FF00);
    ImageIO.write(img, "png", png.toFile());
    View2dContainer container = new View2dContainer();
    ImageCommands commands = new ImageCommands(new ImageioCodec(), () -> container);
    ImportToolBar bar = new ImportToolBar(commands);
    assertEquals(ImportToolBar.NAME, bar.getComponentName());
    assertEquals(Insertable.Type.TOOLBAR, bar.getType());
    assertEquals(ImageCommands.GET_USAGE, bar.importFiles());

    String out = bar.importFiles(png.toFile());

    assertTrue(out.contains("pixels=5x3"));
    assertNotNull(container.getView2d().getSourceImage());
    assertEquals(5, container.getView2d().getSourceImage().getWidth());
    assertEquals(1, container.getOpenSeries().size());
    assertEquals(1, container.getView2d().getFrameCount());
  }
}
