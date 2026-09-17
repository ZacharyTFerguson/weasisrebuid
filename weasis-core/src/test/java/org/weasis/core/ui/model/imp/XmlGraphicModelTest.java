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

import jakarta.xml.bind.annotation.XmlRootElement;
import java.awt.geom.Point2D;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.core.ui.model.graphic.Graphic;
import org.weasis.core.ui.model.graphic.imp.area.RectangleGraphic;
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

  @Test
  void jaxbMarshalsTypeElementAndUuid() {
    LineGraphic line = new LineGraphic();
    line.setUuid("line-1");
    line.setPts(List.of(new Point2D.Double(1.5, 2.5), new Point2D.Double(10, 20)));
    line.setLineThickness(2.5f);
    XmlGraphicModel model = new XmlGraphicModel();
    model.addGraphic(line);
    String xml = model.toXml();
    assertTrue(xml.contains("<LineGraphic"));
    assertTrue(xml.contains("uuid=\"line-1\""));
    assertTrue(xml.contains("lineThickness=\"2.5\""));
    assertEquals("LineGraphic", LineGraphic.class.getAnnotation(XmlRootElement.class).name());
    XmlGraphicModel loaded = XmlGraphicModel.fromXml(xml);
    Graphic restored = loaded.getModels().getFirst();
    assertEquals("line-1", restored.getUuid());
    assertEquals(2.5f, restored.getLineThickness());
    assertEquals(1.5, restored.getPts().getFirst().getX());
  }

  @Test
  void jaxbRoundTripsRectangleAndLegacyDom() {
    RectangleGraphic rect = new RectangleGraphic();
    rect.setPts(List.of(new Point2D.Double(0, 0), new Point2D.Double(8, 4)));
    XmlGraphicModel model = new XmlGraphicModel();
    model.addGraphic(rect);
    XmlGraphicModel loaded = XmlGraphicModel.fromXml(model.toXml());
    assertEquals("RectangleGraphic", loaded.getModels().getFirst().getClass().getSimpleName());
    assertEquals(8.0, loaded.getModels().getFirst().getPts().get(1).getX());

    String legacy =
        "<graphicModel><graphic type=\"LineGraphic\" uuid=\"legacy\">"
            + "<pt x=\"3\" y=\"4\"/><pt x=\"5\" y=\"6\"/></graphic></graphicModel>";
    XmlGraphicModel fromLegacy = XmlGraphicModel.fromXml(legacy);
    assertEquals("legacy", fromLegacy.getModels().getFirst().getUuid());
    assertEquals(3.0, fromLegacy.getModels().getFirst().getPts().getFirst().getX());
  }
}
