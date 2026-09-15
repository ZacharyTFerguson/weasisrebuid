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
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.weasis.core.ui.model.AbstractGraphicModel;
import org.weasis.core.ui.model.graphic.Graphic;
import org.xml.sax.InputSource;

/**
 * XML persistence for a {@link AbstractGraphicModel}. Runtime JAXB-OSGi is on start level 7; this
 * writer uses JDK XML so Have tests do not depend on OSGi service-loader wiring.
 */
public class XmlGraphicModel extends AbstractGraphicModel {

  private static final String[] PACKAGES = {
    "org.weasis.core.ui.model.graphic.imp",
    "org.weasis.core.ui.model.graphic.imp.line",
    "org.weasis.core.ui.model.graphic.imp.area",
    "org.weasis.core.ui.model.graphic.imp.angle",
    "org.weasis.core.ui.model.graphic.imp.seg"
  };

  public String toXml() {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
      Document doc = factory.newDocumentBuilder().newDocument();
      Element root = doc.createElement("graphicModel");
      doc.appendChild(root);
      for (Graphic graphic : getModels()) {
        Element el = doc.createElement("graphic");
        el.setAttribute("type", graphic.getClass().getSimpleName());
        el.setAttribute("uuid", graphic.getUuid() == null ? "" : graphic.getUuid());
        for (Point2D.Double pt : graphic.getPts()) {
          Element p = doc.createElement("pt");
          p.setAttribute("x", Double.toString(pt.getX()));
          p.setAttribute("y", Double.toString(pt.getY()));
          el.appendChild(p);
        }
        root.appendChild(el);
      }
      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
      StringWriter writer = new StringWriter();
      transformer.transform(new DOMSource(doc), new StreamResult(writer));
      return writer.toString();
    } catch (Exception e) {
      throw new IllegalStateException("graphic xml", e);
    }
  }

  public static XmlGraphicModel fromXml(String xml) {
    XmlGraphicModel model = new XmlGraphicModel();
    if (xml == null || xml.isBlank()) {
      return model;
    }
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

  public void write(Path file) throws java.io.IOException {
    Files.writeString(file, toXml());
  }

  public static XmlGraphicModel read(Path file) throws java.io.IOException {
    return fromXml(Files.readString(file));
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
}
