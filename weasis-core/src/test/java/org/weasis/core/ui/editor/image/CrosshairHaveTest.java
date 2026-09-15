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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import org.junit.jupiter.api.Test;
import org.weasis.core.ui.model.layer.LayerAnnotation;
import org.weasis.core.ui.model.layer.LayerItem;
import org.weasis.core.ui.model.layer.LayerType;

class CrosshairHaveTest {

  @Test
  void pixelInfoUsesRasterAndModalityLut() {
    PixelInfo info = PixelInfo.from(gray(new int[][] {{40, 80}}), 1, 0, 1.0, -1024);
    assertEquals(1, info.getX());
    assertEquals(0, info.getY());
    assertEquals(80, info.getPixelValue());
    assertEquals(-944.0, info.getModalityValue(), 1e-9);
    assertTrue(info.getText().contains("1,0"));
  }

  @Test
  void shortcutHSelectsCrosshairAndClickSetsPoint() {
    DefaultView2d<?> view = new DefaultView2d<>();
    view.setSourceImage(gray(new int[][] {{1, 2}, {3, 4}}));
    view.setModalityLut(1.0, -1024);
    view.getEventManager()
        .keyPressed(new KeyEvent(view, KeyEvent.KEY_PRESSED, 0L, 0, KeyEvent.VK_H, 'h'));
    assertEquals(MouseActions.CROSSHAIR, view.getMouseActions().getLeft());
    view.getEventManager().mousePressed(mouse(view, MouseEvent.MOUSE_PRESSED, 1, 0));
    assertTrue(view.hasCrosshair());
    assertEquals(1, view.getCrosshairX());
    assertEquals(0, view.getCrosshairY());
    assertEquals(2, view.getPixelInfo().getPixelValue());
    assertEquals(-1022.0, view.getPixelInfo().getModalityValue(), 1e-9);
    assertTrue(view.isCrosshairPainted());
  }

  @Test
  void manualSynchCopiesCrosshairToManualPeers() {
    DicomLike a = new DicomLike();
    DicomLike b = new DicomLike();
    DicomLike other = new DicomLike();
    a.setFrameOfReferenceUID("1.2.for");
    b.setFrameOfReferenceUID("1.2.for");
    other.setFrameOfReferenceUID("9.9");
    DefaultSynchManager manual = new DefaultSynchManager();
    a.getSynchData().setKind(SynchData.Kind.MANUAL);
    b.getSynchData().setKind(SynchData.Kind.MANUAL);
    other.getSynchData().setKind(SynchData.Kind.MANUAL);
    a.setSynch(SynchView.STACK);
    b.setSynch(SynchView.STACK);
    other.setSynch(SynchView.STACK);
    manual.add(a);
    manual.add(b);
    manual.add(other);
    a.setSynchManager(manual);
    b.setSynchManager(manual);
    other.setSynchManager(manual);
    a.setCrosshair(4, 5);
    assertEquals(4, b.getCrosshairX());
    assertEquals(5, b.getCrosshairY());
    assertEquals(4, other.getCrosshairX());
  }

  @Test
  void displayLayersToggleCrosslinesAndMeasure() {
    DefaultView2d<?> view = new DefaultView2d<>();
    view.setCrosshair(2, 2);
    assertEquals(5, view.displayLayers().size());
    view.setLayerVisible(LayerType.CROSSLINES, false);
    assertFalse(view.isCrosshairPainted());
    assertTrue(view.hasCrosshair());
    LayerItem item = view.displayLayers().get(1);
    assertEquals(LayerType.CROSSLINES, item.getType());
    assertFalse(item.isSelected());
    view.setLayerVisible(LayerType.MEASURE, false);
    assertFalse(view.isLayerVisible(LayerType.MEASURE));
  }

  @Test
  void layerAnnotationHidesPatientItem() {
    LayerAnnotation annotation = new LayerAnnotation();
    assertEquals(LayerType.ANNOTATION, annotation.getType());
    assertTrue(annotation.isItemVisible(LayerAnnotation.PATIENT));
    annotation.setItemVisible(LayerAnnotation.PATIENT, false);
    assertFalse(annotation.isItemVisible(LayerAnnotation.PATIENT));
    DefaultView2d<?> view = new DefaultView2d<>();
    view.getInfoLayer().getLayerAnnotation().setItemVisible(LayerAnnotation.WINDOW_LEVEL, false);
    assertFalse(
        view.getInfoLayer().getLayerAnnotation().isItemVisible(LayerAnnotation.WINDOW_LEVEL));
  }

  static BufferedImage gray(int[][] pixels) {
    BufferedImage image =
        new BufferedImage(pixels[0].length, pixels.length, BufferedImage.TYPE_BYTE_GRAY);
    for (int y = 0; y < pixels.length; y++) {
      for (int x = 0; x < pixels[y].length; x++) {
        image.getRaster().setSample(x, y, 0, pixels[y][x]);
      }
    }
    return image;
  }

  static MouseEvent mouse(DefaultView2d<?> view, int id, int x, int y) {
    return new MouseEvent(
        view, id, 0L, InputEvent.BUTTON1_DOWN_MASK, x, y, 1, false, MouseEvent.BUTTON1);
  }

  static final class DicomLike extends DefaultView2d<org.weasis.core.api.media.data.MediaElement> {}
}
