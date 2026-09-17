/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.mpr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.junit.jupiter.api.Test;
import org.weasis.core.ui.editor.image.Canvas;
import org.weasis.dicom.codec.seg.MaskFrames;
import org.weasis.dicom.viewer2d.mip.MipView;
import org.weasis.dicom.viewer2d.mpr.pr.MprGeometryModule;

class MprSliceCanvasHaveTest {

  @Test
  void buildContextFeedsVolImageIoAndSliceCanvasPaintsIt() {
    VolumeShort vol = new VolumeShort(3, 2, 1);
    vol.setValue(1, 0, 0, 20000);
    BuildContext context = new BuildContext(vol, MprAxis.AXIAL, 0, MipView.Type.NONE, 1);
    VolImageIO io = context.toVolImageIO();
    assertSame(vol, io.getVolume());
    assertEquals(MprAxis.AXIAL, io.getAxis());
    assertEquals(20000, io.getImage().getRaster().getSample(1, 0, 0));

    SliceCanvas canvas = new SliceCanvas(context);
    assertInstanceOf(Canvas.class, canvas);
    BufferedImage source = canvas.getSourceImage();
    assertNotNull(source);
    assertEquals(20000, source.getRaster().getSample(1, 0, 0));

    canvas.setSize(3, 2);
    BufferedImage painted = new BufferedImage(3, 2, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = painted.createGraphics();
    canvas.paint(g);
    g.dispose();
    assertNotEquals(painted.getRGB(0, 0), painted.getRGB(1, 0));
  }

  @Test
  void buildContextAppliesMipThickness() {
    VolumeShort vol = new VolumeShort(1, 1, 3);
    vol.setValue(0, 0, 0, 1);
    vol.setValue(0, 0, 1, 8);
    vol.setValue(0, 0, 2, 3);
    BuildContext context = new BuildContext(vol, MprAxis.AXIAL, 1, MipView.Type.MAX, 3);
    assertEquals(8.0, context.toVolImageIO().samples()[0][0], 1e-9);
  }

  @Test
  void segVolumeBuilderRasterizesByteAndPackedShortFrames() {
    byte[] axial = new byte[] {0, 3, 0, 0};
    byte[] next = new byte[] {0, 0, 7, 0};
    MaskFrames frames = new MaskFrames(2, 2, new byte[][] {axial, next});
    VolumeByte bytes = new SegVolumeBuilder().rasterizeByte(frames);
    assertEquals(2, bytes.sizeX());
    assertEquals(2, bytes.sizeY());
    assertEquals(2, bytes.sizeZ());
    assertEquals(3.0, bytes.value(1, 0, 0), 1e-9);
    assertEquals(7.0, bytes.value(0, 1, 1), 1e-9);

    VolumeByte labeled = new SegVolumeBuilder().rasterizeBinary(frames, 5);
    assertEquals(5.0, labeled.value(1, 0, 0), 1e-9);
    assertEquals(0.0, labeled.value(0, 0, 0), 1e-9);

    byte[] packed = new byte[] {0x2C, 0x01};
    VolumeShort shorts =
        new SegVolumeBuilder().rasterizeShort(new MaskFrames(1, 1, new byte[][] {packed}));
    assertEquals(300.0, shorts.value(0, 0, 0), 1e-9);

    VolumeShort labels =
        new SegVolumeBuilder().rasterizeShort(1, 2, new short[][] {new short[] {0, 400}});
    assertEquals(400.0, labels.value(1, 0, 0), 1e-9);
  }

  @Test
  void mprGeometryModuleRoundTripsBuildContextAndAxes() {
    VolumeShort vol = new VolumeShort(2, 2, 2);
    BuildContext context = new BuildContext(vol, MprAxis.CORONAL, 2, MipView.Type.MEAN, 3);
    MprGeometryModule module = MprGeometryModule.from(context);
    assertEquals(MprAxis.CORONAL, module.getAxis());
    BuildContext restored = module.toBuildContext(vol);
    assertSame(vol, restored.getVolume());
    assertEquals(2, restored.getIndex());
    assertEquals(MipView.Type.MEAN, restored.getMip());
    assertEquals(3, restored.getThickness());

    AxesControl axes = new AxesControl();
    axes.setOrigin(4, 5, 6);
    axes.setU(new AxisDirection(0, 1, 0));
    axes.setV(new AxisDirection(0, 0, 1));
    MprGeometryModule oblique = MprGeometryModule.from(axes);
    AxesControl back = oblique.toAxes();
    assertEquals(4.0, back.origin()[0], 1e-9);
    assertEquals(1.0, back.u().y(), 1e-9);
    assertEquals(1.0, back.v().z(), 1e-9);
  }
}
