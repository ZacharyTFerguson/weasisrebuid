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

import java.awt.geom.Point2D;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.weasis.core.ui.model.AbstractGraphicModel;
import org.weasis.core.ui.model.graphic.Graphic;
import org.weasis.core.ui.model.graphic.imp.AnnotationGraphic;
import org.weasis.core.ui.model.graphic.imp.NonEditableGraphic;
import org.weasis.core.ui.model.graphic.imp.PixelInfoGraphic;
import org.weasis.core.ui.model.graphic.imp.PointGraphic;
import org.weasis.core.ui.model.graphic.imp.angle.AngleToolGraphic;
import org.weasis.core.ui.model.graphic.imp.angle.CobbAngleToolGraphic;
import org.weasis.core.ui.model.graphic.imp.angle.FourPointsAngleToolGraphic;
import org.weasis.core.ui.model.graphic.imp.angle.OpenAngleToolGraphic;
import org.weasis.core.ui.model.graphic.imp.area.EllipseGraphic;
import org.weasis.core.ui.model.graphic.imp.area.ObliqueRectangleGraphic;
import org.weasis.core.ui.model.graphic.imp.area.PolygonGraphic;
import org.weasis.core.ui.model.graphic.imp.area.RectangleGraphic;
import org.weasis.core.ui.model.graphic.imp.area.SelectGraphic;
import org.weasis.core.ui.model.graphic.imp.area.ThreePointsCircleGraphic;
import org.weasis.core.ui.model.graphic.imp.line.LineGraphic;
import org.weasis.core.ui.model.graphic.imp.line.LineWithGapGraphic;
import org.weasis.core.ui.model.graphic.imp.line.ParallelLineGraphic;
import org.weasis.core.ui.model.graphic.imp.line.PerpendicularLineGraphic;
import org.weasis.core.ui.model.graphic.imp.line.PolylineGraphic;
import org.weasis.core.ui.model.graphic.imp.seg.SegGraphic;
import org.xml.sax.InputSource;

/**
 * JAXB persistence for a {@link AbstractGraphicModel}. Runtime JAXB-OSGi is start level 7. Legacy
 * {@code <graphic type=…>} DOM documents still unmarshal.
 */
@XmlRootElement(name = "graphicModel")
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso({
  LineGraphic.class,
  LineWithGapGraphic.class,
  ParallelLineGraphic.class,
  PerpendicularLineGraphic.class,
  PolylineGraphic.class,
  EllipseGraphic.class,
  ObliqueRectangleGraphic.class,
  PolygonGraphic.class,
  RectangleGraphic.class,
  SelectGraphic.class,
  ThreePointsCircleGraphic.class,
  AngleToolGraphic.class,
  CobbAngleToolGraphic.class,
  FourPointsAngleToolGraphic.class,
  OpenAngleToolGraphic.class,
  AnnotationGraphic.class,
  PointGraphic.class,
  PixelInfoGraphic.class,
  NonEditableGraphic.class,
  SegGraphic.class
})
public class XmlGraphicModel extends AbstractGraphicModel {

  private static final String[] PACKAGES = {
    "org.weasis.core.ui.model.graphic.imp",
    "org.weasis.core.ui.model.graphic.imp.line",
    "org.weasis.core.ui.model.graphic.imp.area",
    "org.weasis.core.ui.model.graphic.imp.angle",
    "org.weasis.core.ui.model.graphic.imp.seg"
  };

  private static JAXBContext jaxbContext;

  @XmlElements({
    @XmlElement(name = "LineGraphic", type = LineGraphic.class),
    @XmlElement(name = "LineWithGapGraphic", type = LineWithGapGraphic.class),
    @XmlElement(name = "ParallelLineGraphic", type = ParallelLineGraphic.class),
    @XmlElement(name = "PerpendicularLineGraphic", type = PerpendicularLineGraphic.class),
    @XmlElement(name = "PolylineGraphic", type = PolylineGraphic.class),
    @XmlElement(name = "EllipseGraphic", type = EllipseGraphic.class),
    @XmlElement(name = "ObliqueRectangleGraphic", type = ObliqueRectangleGraphic.class),
    @XmlElement(name = "PolygonGraphic", type = PolygonGraphic.class),
    @XmlElement(name = "RectangleGraphic", type = RectangleGraphic.class),
    @XmlElement(name = "SelectGraphic", type = SelectGraphic.class),
    @XmlElement(name = "ThreePointsCircleGraphic", type = ThreePointsCircleGraphic.class),
    @XmlElement(name = "AngleToolGraphic", type = AngleToolGraphic.class),
    @XmlElement(name = "CobbAngleToolGraphic", type = CobbAngleToolGraphic.class),
    @XmlElement(name = "FourPointsAngleToolGraphic", type = FourPointsAngleToolGraphic.class),
    @XmlElement(name = "OpenAngleToolGraphic", type = OpenAngleToolGraphic.class),
    @XmlElement(name = "AnnotationGraphic", type = AnnotationGraphic.class),
    @XmlElement(name = "PointGraphic", type = PointGraphic.class),
    @XmlElement(name = "PixelInfoGraphic", type = PixelInfoGraphic.class),
    @XmlElement(name = "NonEditableGraphic", type = NonEditableGraphic.class),
    @XmlElement(name = "SegGraphic", type = SegGraphic.class)
  })
  public List<Graphic> getGraphics() {
    return new ArrayList<>(getModels());
  }

  public void setGraphics(List<Graphic> graphics) {
    clear();
    if (graphics == null) {
      return;
    }
    for (Graphic graphic : graphics) {
      graphic.buildShape();
      addGraphic(graphic);
    }
  }

  public String toXml() {
    try {
      Marshaller marshaller = context().createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
      StringWriter writer = new StringWriter();
      marshaller.marshal(this, writer);
      return writer.toString();
    } catch (JAXBException e) {
      throw new IllegalStateException("graphic xml", e);
    }
  }

  public static XmlGraphicModel fromXml(String xml) {
    XmlGraphicModel empty = new XmlGraphicModel();
    if (xml == null || xml.isBlank()) {
      return empty;
    }
    String trimmed = xml.trim();
    if (trimmed.contains("<graphic") && trimmed.contains("type=")) {
      return fromDom(trimmed);
    }
    try {
      Unmarshaller unmarshaller = context().createUnmarshaller();
      Object value = unmarshaller.unmarshal(new StringReader(trimmed));
      if (value instanceof XmlGraphicModel model) {
        for (Graphic graphic : model.getModels()) {
          graphic.buildShape();
        }
        return model;
      }
      throw new IllegalArgumentException("graphic xml");
    } catch (JAXBException e) {
      throw new IllegalArgumentException("graphic xml", e);
    }
  }

  public void write(Path file) throws java.io.IOException {
    Files.writeString(file, toXml());
  }

  public static XmlGraphicModel read(Path file) throws java.io.IOException {
    return fromXml(Files.readString(file));
  }

  static XmlGraphicModel fromDom(String xml) {
    XmlGraphicModel model = new XmlGraphicModel();
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
      factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
      Document doc =
          factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml.trim())));
      NodeList graphics = doc.getElementsByTagName("graphic");
      for (int i = 0; i < graphics.getLength(); i++) {
        Element el = (Element) graphics.item(i);
        Graphic graphic = newGraphic(el.getAttribute("type"));
        String uuid = el.getAttribute("uuid");
        if (uuid != null && !uuid.isBlank()) {
          graphic.setUuid(uuid);
        }
        NodeList pts = el.getElementsByTagName("pt");
        List<Point2D.Double> points = new ArrayList<>();
        for (int p = 0; p < pts.getLength(); p++) {
          Element pt = (Element) pts.item(p);
          points.add(
              new Point2D.Double(
                  Double.parseDouble(pt.getAttribute("x")),
                  Double.parseDouble(pt.getAttribute("y"))));
        }
        graphic.setPts(points);
        graphic.buildShape();
        model.addGraphic(graphic);
      }
      return model;
    } catch (Exception e) {
      throw new IllegalArgumentException("graphic xml", e);
    }
  }

  static Graphic newGraphic(String type) {
    if (type == null || type.isBlank()) {
      throw new IllegalArgumentException("graphic type");
    }
    for (String pkg : PACKAGES) {
      try {
        Class<?> cls = Class.forName(pkg + "." + type);
        Object created = cls.getDeclaredConstructor().newInstance();
        if (created instanceof Graphic graphic) {
          return graphic;
        }
      } catch (ReflectiveOperationException ignored) {
        // try next package
      }
    }
    throw new IllegalArgumentException("graphic " + type);
  }

  static synchronized JAXBContext context() {
    if (jaxbContext == null) {
      try {
        jaxbContext = JAXBContext.newInstance(XmlGraphicModel.class);
      } catch (JAXBException e) {
        throw new IllegalStateException("graphic jaxb", e);
      }
    }
    return jaxbContext;
  }
}
