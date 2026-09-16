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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Point2D;
import java.io.File;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.core.ui.model.graphic.Graphic;
import org.weasis.core.ui.model.graphic.imp.line.LineGraphic;

/**
 * Interactive line caliper on {@link View2d}: two image-space endpoints create a {@link
 * LineGraphic} whose visible label is {@link View2d#formatLineMeasureLabel} — not a recomputed mm
 * in the mouse path.
 *
 * <p><b>Why (0028,0030) via landed APIs:</b> spacing comes from {@link
 * org.weasis.dicom.codec.utils.InstanceSpacing#resolve} on the loaded instance; millimetres on the
 * label use {@link LineGraphic#getLengthMm} only through {@link MeasurementLabel#formatLine}. The
 * draw handler must not call {@code hypot(Δ·pitch)} itself or read monitor/manual calibration
 * doubles.
 *
 * <p><b>Why fail-closed px:</b> when spacing is missing, the caliper still stores pixel endpoints
 * but the label is {@code N.N px} from {@link LineGraphic#getLength}, same as the headless label
 * tests — no silent mm guess.
 *
 * <p><b>Why manual/monitor doubles stay inert:</b> {@link
 * org.weasis.core.ui.editor.image.DefaultView2d#getMonitorCalibrationMmPerPixel} and session manual
 * calibration are unused; this slice only proves DICOM spacing on the drawn line label.
 *
 * <p><b>Why not copy Weasis:</b> upstream {@code MeasureTool} / {@code MeasurementsAdapter} bundle
 * prefs, GSPS, and painted-buffer spacing; this slice adds image-space endpoints + {@link
 * View2d#formatLineMeasureLabel} beside the existing oracle paint path — no measure-tool port,
 * polyline, angle, Cobb, or polygon draw.
 */
class View2dLineDrawTest {

  @Test
  void lineCaliperUsesFormatLineLabelOnCt(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtIso050(dir.resolve("ct.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    view.addLineCaliper(new Point2D.Double(0, 0), new Point2D.Double(10, 0));
    assertEquals(1, view.getGraphicList().size());
    Graphic g = view.getGraphicList().getFirst();
    assertInstanceOf(LineGraphic.class, g);
    LineGraphic line = (LineGraphic) g;
    assertEquals(10.0, line.getLength(), 1e-9);
    assertEquals("5.0 mm", view.formatLineMeasureLabel(line));
    assertEquals("5.0 mm", line.getLabel()[0]);
  }

  @Test
  void lineCaliperFailClosedPixelsWithoutSpacing(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtNoSpacing(dir.resolve("nosp.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    view.addLineCaliper(new Point2D.Double(0, 0), new Point2D.Double(10, 0));
    LineGraphic line = (LineGraphic) view.getGraphicList().getFirst();
    assertEquals("10.0 px", view.formatLineMeasureLabel(line));
    assertEquals("10.0 px", line.getLabel()[0]);
  }

  @Test
  void lineCaliperIgnoresMonitorAndManualCalibration(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtIso050(dir.resolve("ct.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    view.setMonitorCalibrationMmPerPixel(99.0);
    view.setSessionManualCalibrationMmPerPixel(88.0);
    view.addLineCaliper(new Point2D.Double(0, 0), new Point2D.Double(10, 0));
    LineGraphic line = (LineGraphic) view.getGraphicList().getFirst();
    assertEquals("5.0 mm", line.getLabel()[0]);
  }

  @Test
  void twoClickMouseDrawMatchesImageSpaceApi(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtIso050(dir.resolve("ct.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    view.setSize(64, 64);
    view.setZoom(1.0);
    view.setPan(0, 0);
    view.setRotation(0);
    view.getMouseActions().setLeft(org.weasis.core.ui.editor.image.MouseActions.DRAW);
    // 16×16 image, zoom 1, view 64×64 → image (0,0) at view (24,24).
    view.simulateLineDrawTwoClick(24, 24, 34, 24);
    assertEquals(1, view.getGraphicList().size());
    LineGraphic line = (LineGraphic) view.getGraphicList().getFirst();
    assertEquals("5.0 mm", line.getLabel()[0]);
    assertTrue(line.getLength() >= 9.0 && line.getLength() <= 11.0);
  }
}
