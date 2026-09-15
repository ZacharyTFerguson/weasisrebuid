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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.imageio.codec.ImageioCodec;

class ImageCommandsTest {

  @Test
  void getWithoutArgsPrintsUsage() {
    ImageCommands commands = new ImageCommands(new ImageioCodec(), () -> null);
    String out = commands.get();
    assertTrue(out.contains("-f"));
    assertTrue(out.contains("-u"));
  }

  @Test
  void closeWithoutArgsPrintsUsage() {
    ImageCommands commands = new ImageCommands(new ImageioCodec(), () -> null);
    assertEquals(ImageCommands.CLOSE_USAGE, commands.close());
  }

  @Test
  void getLocalPngLoadsPixelsIntoViewer(@TempDir Path dir) throws Exception {
    Path png = dir.resolve("tiny.png");
    BufferedImage img = new BufferedImage(4, 3, BufferedImage.TYPE_INT_RGB);
    img.setRGB(0, 0, 0xFF0000);
    ImageIO.write(img, "png", png.toFile());
    View2dContainer container = new View2dContainer();
    ImageCommands commands = new ImageCommands(new ImageioCodec(), () -> container);

    String out = commands.get("-f", png.toString());

    assertTrue(out.contains("file " + png));
    assertTrue(out.contains("pixels=4x3"));
    assertTrue(out.contains("series="));
    assertTrue(out.contains("group="));
    assertEquals(1, commands.opened().size());
    assertNotNull(container.getView2d().getSourceImage());
    assertEquals(4, container.getView2d().getSourceImage().getWidth());
    assertEquals(1, container.getOpenSeries().size());
  }

  @Test
  void getRemoteUrlDoesNotFetch() {
    View2dContainer container = new View2dContainer();
    ImageCommands commands = new ImageCommands(new ImageioCodec(), () -> container);

    String out = commands.get("-u", "https://example.invalid/stills.jpg");

    assertTrue(out.contains("url https://example.invalid/stills.jpg"));
    assertTrue(out.contains("group=example.invalid"));
    assertEquals(1, commands.opened().size());
    assertNull(commands.opened().getFirst().element().getImage());
    assertNull(container.getView2d().getSourceImage());
  }

  @Test
  void closeAllAndSeriesAndGroup(@TempDir Path dir) throws Exception {
    Path png = dir.resolve("a.png");
    BufferedImage img = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
    ImageIO.write(img, "png", png.toFile());
    ImageCommands commands = new ImageCommands(new ImageioCodec(), () -> null);
    String loaded = commands.get("-f", png.toString(), "-u", "https://example.invalid/b.jpg");
    assertEquals(2, commands.opened().size());

    String seriesUid = commands.opened().getFirst().seriesUid();
    String closedSeries = commands.close("-s", seriesUid);
    assertTrue(closedSeries.contains("close-series " + seriesUid));
    assertEquals(1, commands.opened().size());

    String closedGroup = commands.close("-g", "example.invalid");
    assertTrue(closedGroup.contains("close-group example.invalid"));
    assertEquals(0, commands.opened().size());

    commands.get("-f", png.toString());
    assertEquals("close-all", commands.close("-a"));
    assertEquals(0, commands.opened().size());
    assertTrue(loaded.contains("pixels=2x2"));
  }

  @Test
  void missingLocalFileIsReported() {
    ImageCommands commands = new ImageCommands(new ImageioCodec(), () -> null);
    String out = commands.get("-f", "/no/such/weasis-still.png");
    assertTrue(out.contains("error=missing"));
  }

  @Test
  void view2dLoadReadsPng(@TempDir Path dir) throws Exception {
    Path png = dir.resolve("load.png");
    BufferedImage img = new BufferedImage(6, 1, BufferedImage.TYPE_INT_RGB);
    ImageIO.write(img, "png", png.toFile());
    View2d view = new View2d();
    view.load(png.toFile());
    assertNotNull(view.getSourceImage());
    assertEquals(6, view.getSourceImage().getWidth());
  }
}
