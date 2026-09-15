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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.awt.datatransfer.DataFlavor;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import javax.swing.JPanel;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.image.op.ByteLutCollection;

class DisplayLutTransferHaveTest {

  @Test
  void imageAndViewTransferAndExportProgress() throws Exception {
    DefaultView2d<?> view = new DefaultView2d<>();
    view.setSourceImage(new BufferedImage(16, 8, BufferedImage.TYPE_INT_RGB));
    view.getSourceImage().setRGB(0, 0, 0x112233);
    JPanel host = new JPanel();
    ImageTransferHandler images = new ImageTransferHandler();
    assertTrue(images.canImport(host, new DataFlavor[] {DataFlavor.imageFlavor}));
    assertFalse(images.canImport(host, new DataFlavor[] {DataFlavor.stringFlavor}));
    BufferedImage exported = view.exportImage();
    assertEquals(16, exported.getWidth());
    assertEquals(8, exported.getHeight());
    assertEquals(0xFF112233, exported.getRGB(0, 0));
    view.getSourceImage().setRGB(0, 0, 0x445566);
    assertEquals(0xFF112233, exported.getRGB(0, 0));
    Object transferred = images.createTransferable(view).getTransferData(DataFlavor.imageFlavor);
    assertEquals(16, ((BufferedImage) transferred).getWidth());

    ViewTransferHandler views = new ViewTransferHandler();
    assertTrue(views.canImport(host, new DataFlavor[] {DataFlavor.javaFileListFlavor}));
    assertFalse(views.canImport(host, new DataFlavor[] {DataFlavor.imageFlavor}));
    assertEquals(1, views.importFiles(List.of(new File("a.dcm"))));
    assertEquals("a.dcm", views.lastFiles().getFirst().getName());

    view.getViewProgress().setPercent(50);
    assertEquals(50, view.getViewProgress().getPercent());
    assertFalse(view.getViewProgress().isComplete());
    view.getViewProgress().setPercent(150);
    assertEquals(100, view.getViewProgress().getPercent());
    assertTrue(view.getViewProgress().isComplete());
  }

  @Test
  void inverseLutAndFrameOfReferenceColors() {
    DisplayByteLut inverse = new DisplayByteLut(ByteLutCollection.INVERSE);
    assertEquals(255, inverse.grayAt(0));
    assertEquals(0, inverse.grayAt(255));
    DisplayByteLut gray = new DisplayByteLut(null);
    assertEquals(ByteLutCollection.GRAY, gray.getName());
    assertEquals(0, gray.grayAt(0));
    DefaultView2d<?> view = new DefaultView2d<>();
    view.setDisplayByteLut(inverse);
    assertEquals(ByteLutCollection.INVERSE, view.getDisplayByteLut().getName());

    view.setFrameOfReferenceUID("1.2.for-a");
    Color a = view.colorForFrameOfReference();
    assertEquals(a, view.getFrameOfReferenceColor().colorFor("1.2.for-a"));
    assertNotEquals(a, view.getFrameOfReferenceColor().colorFor("1.2.for-b"));
    assertEquals(Color.DARK_GRAY, view.getFrameOfReferenceColor().colorFor(""));
  }
}
