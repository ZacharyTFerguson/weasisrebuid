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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Point2D;
import java.nio.file.Path;
import java.util.List;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.core.ui.model.graphic.Graphic;
import org.weasis.core.ui.model.graphic.imp.line.LineGraphic;

class GspsDrawingsOnlyTest {

  @Test
  void exportedPrOmitsWindowLevelAndLut(@TempDir Path dir) throws Exception {
    java.io.File ct = dir.resolve("ct.dcm").toFile();
    TestCt.write(ct, 8, 40, 400);
    View2d view = new View2d();
    view.load(ct);
    LineGraphic line = new LineGraphic();
    line.setHandlePoint(0, new Point2D.Double(0, 0));
    line.setHandlePoint(1, new Point2D.Double(3, 4));
    view.addGraphic(line);
    Attributes pr = PresentationStateWriter.create(view.getDataset(), view.getGraphicList());
    assertEquals(UID.GrayscaleSoftcopyPresentationStateStorage, pr.getString(Tag.SOPClassUID));
    assertFalse(pr.contains(Tag.WindowCenter));
    assertFalse(pr.contains(Tag.WindowWidth));
    assertFalse(pr.contains(Tag.VOILUTSequence));
    assertFalse(pr.contains(Tag.PresentationLUTSequence));
    assertFalse(pr.contains(Tag.DisplayedAreaSelectionSequence));
    assertTrue(pr.contains(Tag.GraphicAnnotationSequence));
    java.io.File out = dir.resolve("draw.pr").toFile();
    PresentationStateWriter.write(out, view.getDataset(), view.getGraphicList());
    List<Graphic> loaded = PresentationStateReader.readFile(out);
    assertEquals(1, loaded.size());
    assertTrue(loaded.get(0) instanceof LineGraphic);
  }
}
