/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.awt.geom.Point2D;
import java.io.File;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.core.ui.model.graphic.Graphic;
import org.weasis.core.ui.model.graphic.imp.angle.AngleToolGraphic;
import org.weasis.core.ui.model.graphic.imp.line.LineGraphic;
import org.weasis.core.ui.model.graphic.imp.line.PolylineGraphic;

/**
 * Interactive angle on {@link View2d}: three image-space handles (arm end, vertex, arm end) create
 * an {@link AngleToolGraphic} whose visible label is {@link View2d#formatAngleMeasureLabel} — not
 * degrees or arm mm recomputed in the mouse path.
 *
 * <p><b>Why (0028,0030) via landed APIs:</b> spacing comes from {@link
 * org.weasis.dicom.codec.utils.InstanceSpacing#resolve} on the loaded instance; degrees and arm
 * lengths reach the label only through {@link AngleToolGraphic#getAngleDegrees}, {@link
 * AngleToolGraphic#getArmLengthMm}, and {@link MeasurementLabel#formatAngle}. The draw handler must
 * not apply spacing in view coordinates or format its own unit string.
 *
 * <p><b>Why three handles in fixed order:</b> landed {@link AngleToolGraphic} uses index 0 and 2 as
 * arm endpoints and 1 as the vertex; click order must match that contract so labels agree with
 * {@link View2dAngleMeasureLabelTest} on the same geometry.
 *
 * <p><b>Why fail-closed px:</b> when spacing is missing, handles still store in image space but the
 * label uses pixel arm lengths from the graphic API — same as headless angle label tests, no silent
 * mm guess.
 *
 * <p><b>Why not copy Weasis:</b> upstream {@code MeasureTool} bundles angle paint, prefs, and GSPS;
 * this slice adds image-space handles + {@link View2d#formatAngleMeasureLabel} beside line and
 * polyline calipers — no measure-tool port, Cobb, polygon draw, or spline.
 */
class View2dAngleDrawTest {

  @Test
  void angleCaliperUsesFormatAngleLabelOnCt(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtIso050(dir.resolve("ct.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    view.addAngleCaliper(
        new Point2D.Double(10, 0), new Point2D.Double(0, 0), new Point2D.Double(0, 10));
    assertEquals(1, view.getGraphicList().size());
    Graphic g = view.getGraphicList().getFirst();
    assertInstanceOf(AngleToolGraphic.class, g);
    AngleToolGraphic angle = (AngleToolGraphic) g;
    assertEquals("90.0°  5.0 mm / 5.0 mm", view.formatAngleMeasureLabel(angle));
    assertEquals("90.0°  5.0 mm / 5.0 mm", angle.getLabel()[0]);
  }

  @Test
  void angleCaliperFailClosedPixelsWithoutSpacing(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtNoSpacing(dir.resolve("nosp.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    view.addAngleCaliper(
        new Point2D.Double(10, 0), new Point2D.Double(0, 0), new Point2D.Double(0, 10));
    AngleToolGraphic angle = (AngleToolGraphic) view.getGraphicList().getFirst();
    assertEquals("90.0°  10.0 px / 10.0 px", view.formatAngleMeasureLabel(angle));
    assertEquals("90.0°  10.0 px / 10.0 px", angle.getLabel()[0]);
  }

  @Test
  void clickDrawAngleMatchesImageSpaceApi(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtIso050(dir.resolve("ct.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    view.setSize(64, 64);
    view.setZoom(1.0);
    view.setPan(0, 0);
    view.setRotation(0);
    view.getMouseActions().setLeft(org.weasis.core.ui.editor.image.MouseActions.ANGLE);
    // 16×16 image, zoom 1, view 64×64 → image (0,0) at view (24,24).
    view.simulateAngleDrawClick(34, 24); // handle 0 at image (10,0)
    view.simulateAngleDrawClick(24, 24); // vertex at image (0,0)
    view.simulateAngleDrawClick(24, 34); // handle 2 at image (0,10)
    assertEquals(1, view.getGraphicList().size());
    AngleToolGraphic angle = (AngleToolGraphic) view.getGraphicList().getFirst();
    assertEquals("90.0°  5.0 mm / 5.0 mm", angle.getLabel()[0]);
  }

  @Test
  void lineAndPolylineDrawUnchangedWhenLeftActionIsNotAngle(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtIso050(dir.resolve("ct.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    view.setSize(64, 64);
    view.setZoom(1.0);
    view.setPan(0, 0);
    view.setRotation(0);
    view.getMouseActions().setLeft(org.weasis.core.ui.editor.image.MouseActions.DRAW);
    view.simulateLineDrawTwoClick(24, 24, 34, 24);
    assertEquals(1, view.getGraphicList().size());
    assertInstanceOf(LineGraphic.class, view.getGraphicList().getFirst());

    view.getGraphicList().clear();
    view.getMouseActions().setLeft(org.weasis.core.ui.editor.image.MouseActions.POLYLINE);
    view.simulatePolylineDrawClick(24, 24, 1);
    view.simulatePolylineDrawClick(34, 24, 1);
    view.simulatePolylineDrawClick(34, 34, 2);
    assertInstanceOf(PolylineGraphic.class, view.getGraphicList().getFirst());
  }
}
