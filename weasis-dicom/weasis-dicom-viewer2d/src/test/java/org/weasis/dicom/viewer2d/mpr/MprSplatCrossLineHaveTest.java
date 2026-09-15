/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.mpr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import org.junit.jupiter.api.Test;
import org.weasis.core.ui.model.graphic.imp.line.LineGraphic;
import org.weasis.core.ui.model.layer.LayerType;
import org.weasis.dicom.viewer2d.mip.MipView;

class MprSplatCrossLineHaveTest {

  @Test
  void splatContextMatchesOrthogonalMipAndObliqueNormal() {
    VolumeShort vol = new VolumeShort(1, 1, 3);
    vol.setValue(0, 0, 0, 1);
    vol.setValue(0, 0, 1, 8);
    vol.setValue(0, 0, 2, 3);
    BuildContext context = new BuildContext(vol, MprAxis.AXIAL, 1, MipView.Type.MAX, 3);
    SplatContext splat = SplatContext.from(context);
    assertEquals(8.0, splat.samples()[0][0], 1e-9);
    assertEquals(8.0, splat.toBuildContext().toVolImageIO().samples()[0][0], 1e-9);

    VolumeShort slab = new VolumeShort(1, 1, 3);
    slab.setValue(0, 0, 0, 2);
    slab.setValue(0, 0, 1, 9);
    slab.setValue(0, 0, 2, 4);
    AxesControl axes = new AxesControl();
    axes.setOrigin(0, 0, 1);
    axes.setU(new AxisDirection(1, 0, 0));
    axes.setV(new AxisDirection(0, 1, 0));
    SplatContext oblique = new SplatContext(slab, axes, 1, 1, MipView.Type.MAX, 3);
    assertTrue(oblique.isOblique());
    assertEquals(9.0, oblique.samples()[0][0], 1e-9);
    oblique.setMip(MipView.Type.MIN);
    assertEquals(2.0, oblique.samples()[0][0], 1e-9);
    oblique.setMip(MipView.Type.MEAN);
    assertEquals(5.0, oblique.samples()[0][0], 1e-9);
  }

  @Test
  void crossLineGraphicBuildsLineThenSlabAndColorsAxes() {
    CrossLineGraphic[] lines = CrossLineGraphic.forView(MprAxis.AXIAL, 4, 3, 8, 6, 1);
    assertEquals(2, lines.length);
    assertEquals(MprAxis.CORONAL, lines[0].getAxis());
    assertTrue(lines[0].isHorizontal());
    assertEquals(Color.GREEN, lines[0].getColorPaint());
    assertEquals(MprAxis.SAGITTAL, lines[1].getAxis());
    assertEquals(Color.RED, lines[1].getColorPaint());
    assertEquals(LayerType.CROSSLINES, lines[0].getLayerType());
    lines[0].buildShape();
    assertInstanceOf(Line2D.class, lines[0].getShape());
    assertNotNull(lines[0].getShape());
    assertEquals(3.0, ((Line2D) lines[0].getShape()).getY1(), 1e-9);

    lines[0].setThickness(5);
    lines[0].buildShape();
    assertInstanceOf(Path2D.class, lines[0].getShape());
    assertEquals(2.5, lines[0].slabWidth(), 1e-9);
    assertInstanceOf(LineGraphic.class, lines[0]);
    assertInstanceOf(CrossLineGraphic.class, lines[0].copy());
  }

  @Test
  void mprViewBuildsCrossLinesFromSplatThickness() {
    MprView view = new MprView();
    view.setAxis(MprAxis.CORONAL);
    view.getMip().setThickness(3);
    view.setSourceImage(
        new java.awt.image.BufferedImage(10, 8, java.awt.image.BufferedImage.TYPE_BYTE_GRAY));
    view.setCrosshair(2, 5);
    CrossLineGraphic[] lines = view.buildCrossLines();
    assertEquals(MprAxis.AXIAL, lines[0].getAxis());
    assertEquals(Color.BLUE, lines[0].getColorPaint());
    assertEquals(3, lines[0].getThickness());
    assertEquals(2.0, lines[1].getHandlePoint(0).x, 1e-9);
    VolumeShort vol = new VolumeShort(1, 1, 1);
    vol.setValue(0, 0, 0, 7);
    assertEquals(7.0, view.splatContext(vol).samples()[0][0], 1e-9);
  }
}
