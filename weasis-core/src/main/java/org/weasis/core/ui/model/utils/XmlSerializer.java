/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.utils;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.weasis.core.ui.model.AbstractGraphicModel;
import org.weasis.core.ui.model.GraphicModel;
import org.weasis.core.ui.model.graphic.Graphic;
import org.weasis.core.ui.model.graphic.GraphicKind;
import org.xml.sax.InputSource;

/** XML persist for non-DICOM stills (ARCHITECTURE §5.2). Auto-reload is beside the still. */
public final class XmlSerializer {

  private XmlSerializer() {}

  public static String serialize(GraphicModel model) {
    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
      Document doc = dbf.newDocumentBuilder().newDocument();
      Element root = doc.createElement("graphics");
      doc.appendChild(root);
      if (model != null) {
        for (Graphic g : model.getModels()) {
          root.appendChild(toElement(doc, g));
        }
      }
      TransformerFactory tf = TransformerFactory.newInstance();
      var t = tf.newTransformer();
      t.setOutputProperty(OutputKeys.INDENT, "yes");
      t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
      StringWriter w = new StringWriter();
      t.transform(new DOMSource(doc), new StreamResult(w));
      return w.toString();
    } catch (Exception e) {
      throw new IllegalStateException("XML serialize failed", e);
    }
  }

  public static void write(GraphicModel model, Path path) throws IOException {
    Files.writeString(path, serialize(model), StandardCharsets.UTF_8);
  }

  public static GraphicModel read(String xml) {
    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
      Document doc = dbf.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
      return fromDocument(doc);
    } catch (Exception e) {
      throw new IllegalStateException("XML parse failed", e);
    }
  }

  public static GraphicModel read(Reader reader) {
    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
      Document doc = dbf.newDocumentBuilder().parse(new InputSource(reader));
      return fromDocument(doc);
    } catch (Exception e) {
      throw new IllegalStateException("XML parse failed", e);
    }
  }

  public static GraphicModel read(Path path) throws IOException {
    return read(Files.readString(path, StandardCharsets.UTF_8));
  }

  public static void write(GraphicModel model, Writer writer) throws IOException {
    writer.write(serialize(model));
  }

  static GraphicModel fromDocument(Document doc) {
    AbstractGraphicModel model = new AbstractGraphicModel();
    NodeList nodes = doc.getElementsByTagName("graphic");
    for (int i = 0; i < nodes.getLength(); i++) {
      Element el = (Element) nodes.item(i);
      GraphicKind kind = GraphicKind.fromXml(el.getAttribute("type"));
      if (kind == null) {
        continue;
      }
      Graphic g = kind.create();
      g.setUuid(el.getAttribute("uuid"));
      g.setFilled(Boolean.parseBoolean(el.getAttribute("filled")));
      if (el.hasAttribute("thickness")) {
        g.setLineThickness(Float.parseFloat(el.getAttribute("thickness")));
      }
      if (el.hasAttribute("color")) {
        g.setColorPaint(Color.decode(el.getAttribute("color")));
      }
      List<Point2D.Double> pts = new ArrayList<>();
      NodeList ptNodes = el.getElementsByTagName("pt");
      for (int p = 0; p < ptNodes.getLength(); p++) {
        Element pt = (Element) ptNodes.item(p);
        pts.add(
            new Point2D.Double(
                Double.parseDouble(pt.getAttribute("x")),
                Double.parseDouble(pt.getAttribute("y"))));
      }
      g.setPts(pts);
      NodeList labels = el.getElementsByTagName("label");
      if (labels.getLength() > 0) {
        List<String> lines = new ArrayList<>();
        for (int l = 0; l < labels.getLength(); l++) {
          lines.add(labels.item(l).getTextContent());
        }
        g.setLabel(lines.toArray(String[]::new));
      }
      model.addGraphic(g);
    }
    return model;
  }

  static Element toElement(Document doc, Graphic g) {
    Element el = doc.createElement("graphic");
    GraphicKind kind = GraphicKind.of(g);
    el.setAttribute("type", kind == null ? g.getClass().getSimpleName() : kind.xmlName());
    el.setAttribute("uuid", g.getUuid() == null ? "" : g.getUuid());
    el.setAttribute("filled", Boolean.toString(Boolean.TRUE.equals(g.getFilled())));
    el.setAttribute(
        "thickness", Float.toString(g.getLineThickness() == null ? 1f : g.getLineThickness()));
    if (g.getColorPaint() instanceof Color c) {
      el.setAttribute("color", String.format("#%06X", c.getRGB() & 0xFFFFFF));
    }
    for (Point2D.Double p : g.getPts()) {
      Element pt = doc.createElement("pt");
      pt.setAttribute("x", Double.toString(p.x));
      pt.setAttribute("y", Double.toString(p.y));
      el.appendChild(pt);
    }
    if (g.getLabel() != null) {
      for (String line : g.getLabel()) {
        Element lab = doc.createElement("label");
        lab.setTextContent(line);
        el.appendChild(lab);
      }
    }
    return el;
  }
}
