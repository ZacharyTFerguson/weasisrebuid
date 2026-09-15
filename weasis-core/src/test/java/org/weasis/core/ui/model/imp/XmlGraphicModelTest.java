/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.imp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Point2D;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.core.ui.model.graphic.Graphic;
import org.weasis.core.ui.model.graphic.imp.line.LineGraphic;

class XmlGraphicModelTest {

  @Test
  void roundTripPreservesLineHandles(@TempDir Path dir) throws Exception {
    LineGraphic line = new LineGraphic();
    line.setPts(List.of(new Point2D.Double(1.5, 2.5), new Point2D.Double(10, 20)));
    line.buildShape();
    XmlGraphicModel model = new XmlGraphicModel();
    model.addGraphic(line);
    String xml = model.toXml();
    assertTrue(xml.contains("LineGraphic"));
    XmlGraphicModel loaded = XmlGraphicModel.fromXml(xml);
    assertEquals(1, loaded.getModels().size());
    Graphic restored = loaded.getModels().get(0);
    assertEquals("LineGraphic", restored.getClass().getSimpleName());
    assertEquals(1.5, restored.getPts().get(0).getX());
    assertEquals(20, restored.getPts().get(1).getY());
    Path file = dir.resolve("g.xml");
    model.write(file);
    assertEquals(1, XmlGraphicModel.read(file).getModels().size());
  }
}
