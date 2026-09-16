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
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.core.ui.model.graphic.Graphic;
import org.weasis.core.ui.model.graphic.imp.line.LineGraphic;
import org.weasis.core.ui.model.graphic.imp.line.PolylineGraphic;

/**
 * Interactive polyline on {@link View2d}: image-space vertices create a {@link PolylineGraphic}
 * whose visible label is {@link View2d#formatPolylineMeasureLabel} — not a recomputed mm in the
 * mouse path.
 *
 * <p><b>Why (0028,0030) via landed APIs:</b> spacing comes from {@link
 * org.weasis.dicom.codec.utils.InstanceSpacing#resolve} on the loaded instance; millimetres on the
 * label use {@link PolylineGraphic#getLengthMm} only through {@link
 * MeasurementLabel#formatPolyline}. The draw handler must not sum {@code hypot(Δ·pitch)} itself or
 * use end-to-end chord length.
 *
 * <p><b>Why segment sum, not chord:</b> path (0,0)→(10,0)→(10,10) is 20 px and 10.0 mm on isotropic
 * 0.50 spacing — not the 14.14 px diagonal between endpoints. Labels must match {@link
 * View2dPolylineMeasureLabelTest#polylineLabelSumsSegmentsNotEndToEndHypot}.
 *
 * <p><b>Why fail-closed px:</b> when spacing is missing, vertices still store in image space but
 * the label is summed {@code N.N px} from {@link PolylineGraphic#getLength}, same as headless label
 * tests — no silent mm guess.
 *
 * <p><b>Why not copy Weasis:</b> upstream {@code MeasureTool} bundles prefs, GSPS, and
 * painted-buffer spacing; this slice adds image-space vertices + {@link
 * View2d#formatPolylineMeasureLabel} beside the existing line caliper — no measure-tool port,
 * angle, Cobb, polygon draw, or spline.
 */
class View2dPolylineDrawTest {

  @Test
  void polylineCaliperUsesFormatPolylineLabelOnCt(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtIso050(dir.resolve("ct.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    view.addPolylineCaliper(
        List.of(new Point2D.Double(0, 0), new Point2D.Double(10, 0), new Point2D.Double(10, 10)));
    assertEquals(1, view.getGraphicList().size());
    Graphic g = view.getGraphicList().getFirst();
    assertInstanceOf(PolylineGraphic.class, g);
    PolylineGraphic poly = (PolylineGraphic) g;
    assertEquals(20.0, poly.getLength(), 1e-9);
    assertEquals("10.0 mm", view.formatPolylineMeasureLabel(poly));
    assertEquals("10.0 mm", poly.getLabel()[0]);
  }

  @Test
  void polylineCaliperFailClosedPixelsWithoutSpacing(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtNoSpacing(dir.resolve("nosp.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    view.addPolylineCaliper(
        List.of(new Point2D.Double(0, 0), new Point2D.Double(10, 0), new Point2D.Double(10, 10)));
    PolylineGraphic poly = (PolylineGraphic) view.getGraphicList().getFirst();
    assertEquals("20.0 px", view.formatPolylineMeasureLabel(poly));
    assertEquals("20.0 px", poly.getLabel()[0]);
  }

  @Test
  void clickDrawPolylineMatchesImageSpaceApi(@TempDir Path dir) throws Exception {
    File ct = MeasureLabelFixtures.writeCtIso050(dir.resolve("ct.dcm").toFile());
    View2d view = new View2d();
    view.load(ct);
    view.setSize(64, 64);
    view.setZoom(1.0);
    view.setPan(0, 0);
    view.setRotation(0);
    view.getMouseActions().setLeft(org.weasis.core.ui.editor.image.MouseActions.POLYLINE);
    // 16×16 image, zoom 1, view 64×64 → image (0,0) at view (24,24); +10 px along X and Y.
    view.simulatePolylineDrawClick(24, 24, 1);
    view.simulatePolylineDrawClick(34, 24, 1);
    view.simulatePolylineDrawClick(34, 34, 2);
    assertEquals(1, view.getGraphicList().size());
    PolylineGraphic poly = (PolylineGraphic) view.getGraphicList().getFirst();
    assertEquals("10.0 mm", poly.getLabel()[0]);
    assertEquals(3, poly.getPts().size());
  }

  @Test
  void lineDrawUnchangedWhenLeftActionIsDraw(@TempDir Path dir) throws Exception {
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
  }
}
