/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.pr;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;
import org.weasis.core.ui.model.graphic.Graphic;
import org.weasis.core.ui.model.graphic.imp.AnnotationGraphic;
import org.weasis.core.ui.model.graphic.imp.NonEditableGraphic;
import org.weasis.core.ui.model.graphic.imp.PointGraphic;
import org.weasis.core.ui.model.graphic.imp.area.EllipseGraphic;
import org.weasis.core.ui.model.graphic.imp.area.PolygonGraphic;
import org.weasis.core.ui.model.graphic.imp.area.RectangleGraphic;
import org.weasis.core.ui.model.graphic.imp.line.LineGraphic;
import org.weasis.core.ui.model.graphic.imp.line.PolylineGraphic;

/**
 * Maps GSPS Graphic Object / Compound Graphic / Text Object sequences (PS3.3 C.10.5) onto Weasis
 * {@link Graphic} instances.
 */
public class PrGraphicUtil {

  public static final String POINT = "POINT";
  public static final String POLYLINE = "POLYLINE";
  public static final String INTERPOLATED = "INTERPOLATED";
  public static final String CIRCLE = "CIRCLE";
  public static final String ELLIPSE = "ELLIPSE";
  public static final String RECTANGLE = "RECTANGLE";
  public static final String MULTILINE = "MULTILINE";
  public static final String ARROW = "ARROW";
  public static final String UNITS_PIXEL = "PIXEL";
  public static final String UNITS_DISPLAY = "DISPLAY";

  public List<Graphic> getGraphics(Attributes pr) {
    return getGraphics(pr, 0, 0);
  }

  public List<Graphic> getGraphics(Attributes pr, int columns, int rows) {
    List<Graphic> out = new ArrayList<>();
    if (pr == null) {
      return out;
    }
    Map<String, Color> layers = layerColors(pr);
    Sequence anns = pr.getSequence(Tag.GraphicAnnotationSequence);
    if (anns == null) {
      Graphic single = buildGraphic(pr, columns, rows);
      if (single != null) {
        out.add(single);
      }
      return out;
    }
    for (Attributes ann : anns) {
      Color color = layers.get(ann.getString(Tag.GraphicLayer, ""));
      addObjects(out, ann.getSequence(Tag.GraphicObjectSequence), color, columns, rows, false);
      addObjects(out, ann.getSequence(Tag.CompoundGraphicSequence), color, columns, rows, true);
      addTexts(out, ann.getSequence(Tag.TextObjectSequence), color, columns, rows);
    }
    return out;
  }

  public Attributes samplePolylineObject() {
    Attributes go = new Attributes();
    go.setString(Tag.GraphicAnnotationUnits, VR.CS, UNITS_PIXEL);
    go.setString(Tag.GraphicType, VR.CS, POLYLINE);
    go.setString(Tag.GraphicFilled, VR.CS, "N");
    go.setInt(Tag.NumberOfGraphicPoints, VR.US, 2);
    go.setFloat(Tag.GraphicData, VR.FL, 0f, 0f, 10f, 0f);
    return go;
  }

  public Graphic mapSamplePolyline() {
    return buildGraphic(samplePolylineObject());
  }

  public Graphic buildGraphic(Attributes graphicObject) {
    return buildGraphic(graphicObject, 0, 0);
  }

  public Graphic buildGraphic(Attributes graphicObject, int columns, int rows) {
    return buildGraphic(graphicObject, columns, rows, false);
  }

  public Graphic buildCompoundGraphic(Attributes compoundGraphic) {
    return buildGraphic(compoundGraphic, 0, 0, true);
  }

  Graphic buildGraphic(Attributes item, int columns, int rows, boolean compound) {
    if (item == null) {
      return null;
    }
    List<Point2D.Double> pts = graphicData(item, columns, rows);
    boolean filled = "Y".equalsIgnoreCase(item.getString(Tag.GraphicFilled, "N"));
    String type =
        (compound
                ? item.getString(Tag.CompoundGraphicType, "")
                : item.getString(Tag.GraphicType, ""))
            .toUpperCase(Locale.ROOT);
    Graphic graphic = fromType(type, pts, filled);
    if (graphic != null) {
      graphic.setFilled(filled);
    }
    return graphic;
  }

  public static List<Point2D.Double> graphicData(Attributes item) {
    return graphicData(item, 0, 0);
  }

  public static List<Point2D.Double> graphicData(Attributes item, int columns, int rows) {
    List<Point2D.Double> pts = new ArrayList<>();
    if (item == null) {
      return pts;
    }
    float[] data = item.getFloats(Tag.GraphicData);
    if (data == null) {
      return pts;
    }
    boolean display =
        UNITS_DISPLAY.equalsIgnoreCase(item.getString(Tag.GraphicAnnotationUnits, ""));
    double sx = display && columns > 0 ? columns : 1.0;
    double sy = display && rows > 0 ? rows : 1.0;
    int n = data.length / 2;
    for (int i = 0; i < n; i++) {
      pts.add(new Point2D.Double(data[i * 2] * sx, data[i * 2 + 1] * sy));
    }
    return pts;
  }

  static Graphic fromType(String type, List<Point2D.Double> pts, boolean filled) {
    if (type == null || type.isBlank() || pts.isEmpty()) {
      return null;
    }
    return switch (type) {
      case POINT -> pointGraphic(pts.get(0));
      case POLYLINE, MULTILINE -> polyline(pts, filled);
      case INTERPOLATED -> interpolated(pts, filled);
      case CIRCLE -> circle(pts, filled);
      case ELLIPSE -> ellipse(pts, filled);
      case RECTANGLE -> rectangle(pts, filled);
      case ARROW -> polyline(pts, false);
      default -> null;
    };
  }

  static Graphic pointGraphic(Point2D.Double p) {
    PointGraphic g = new PointGraphic();
    g.setPts(List.of(p));
    return g;
  }

  static Graphic polyline(List<Point2D.Double> pts, boolean filled) {
    if (pts.size() == 2 && !filled) {
      LineGraphic line = new LineGraphic();
      line.setPts(pts);
      return line;
    }
    if (filled && pts.size() >= 3) {
      PolygonGraphic poly = new PolygonGraphic();
      poly.setPts(pts);
      poly.setFilled(true);
      return poly;
    }
    PolylineGraphic line = new PolylineGraphic();
    line.setPts(pts);
    return line;
  }

  static Graphic interpolated(List<Point2D.Double> pts, boolean filled) {
    InterpolatedPath2D path = new InterpolatedPath2D(pts, filled);
    NonEditableGraphic g = new NonEditableGraphic(path);
    g.setPts(pts);
    g.setFilled(filled);
    return g;
  }

  static Graphic circle(List<Point2D.Double> pts, boolean filled) {
    if (pts.size() < 2) {
      return pointGraphic(pts.get(0));
    }
    Point2D.Double c = pts.get(0);
    double r = c.distance(pts.get(1));
    EllipseGraphic g = new EllipseGraphic();
    g.setPts(List.of(new Point2D.Double(c.x - r, c.y - r), new Point2D.Double(c.x + r, c.y + r)));
    g.setFilled(filled);
    return g;
  }

  static Graphic ellipse(List<Point2D.Double> pts, boolean filled) {
    if (pts.size() < 4) {
      return circle(pts, filled);
    }
    Point2D.Double majorA = pts.get(0);
    Point2D.Double majorB = pts.get(1);
    Point2D.Double minorA = pts.get(2);
    Point2D.Double minorB = pts.get(3);
    double cx = (majorA.x + majorB.x) / 2.0;
    double cy = (majorA.y + majorB.y) / 2.0;
    double rx = majorA.distance(majorB) / 2.0;
    double ry = minorA.distance(minorB) / 2.0;
    double angle = Math.atan2(majorB.y - majorA.y, majorB.x - majorA.x);
    Ellipse2D unit = new Ellipse2D.Double(-rx, -ry, rx * 2.0, ry * 2.0);
    AffineTransform transform = AffineTransform.getTranslateInstance(cx, cy);
    transform.rotate(angle);
    Shape shape = transform.createTransformedShape(unit);
    NonEditableGraphic g = new NonEditableGraphic(shape);
    g.setPts(pts);
    g.setFilled(filled);
    return g;
  }

  static Graphic rectangle(List<Point2D.Double> pts, boolean filled) {
    if (pts.size() == 2) {
      RectangleGraphic g = new RectangleGraphic();
      g.setPts(pts);
      g.setFilled(filled);
      return g;
    }
    if (pts.size() >= 4) {
      PolygonGraphic g = new PolygonGraphic();
      g.setPts(pts.subList(0, 4));
      g.setFilled(filled);
      return g;
    }
    return polyline(pts, filled);
  }

  static Map<String, Color> layerColors(Attributes pr) {
    Map<String, Color> colors = new HashMap<>();
    Sequence seq = pr.getSequence(Tag.GraphicLayerSequence);
    if (seq == null) {
      return colors;
    }
    for (Attributes layer : seq) {
      String name = layer.getString(Tag.GraphicLayer, "");
      int gray = layer.getInt(Tag.GraphicLayerRecommendedDisplayGrayscaleValue, -1);
      if (!name.isEmpty() && gray >= 0) {
        int g = Math.min(255, gray / 257);
        colors.put(name, new Color(g, g, g));
      }
    }
    return colors;
  }

  static void addObjects(
      List<Graphic> out, Sequence seq, Color color, int columns, int rows, boolean compound) {
    if (seq == null) {
      return;
    }
    PrGraphicUtil util = new PrGraphicUtil();
    for (Attributes item : seq) {
      Graphic g = util.buildGraphic(item, columns, rows, compound);
      if (g != null) {
        if (color != null) {
          g.setColorPaint(color);
        }
        out.add(g);
      }
    }
  }

  static void addTexts(List<Graphic> out, Sequence seq, Color color, int columns, int rows) {
    if (seq == null) {
      return;
    }
    for (Attributes item : seq) {
      Graphic g = textGraphic(item, columns, rows);
      if (g != null) {
        if (color != null) {
          g.setColorPaint(color);
        }
        out.add(g);
      }
    }
  }

  static Graphic textGraphic(Attributes item, int columns, int rows) {
    String text = item.getString(Tag.UnformattedTextValue, "");
    float[] anchor = item.getFloats(Tag.AnchorPoint);
    float[] topLeft = item.getFloats(Tag.BoundingBoxTopLeftHandCorner);
    float[] bottomRight = item.getFloats(Tag.BoundingBoxBottomRightHandCorner);
    boolean display =
        UNITS_DISPLAY.equalsIgnoreCase(item.getString(Tag.BoundingBoxAnnotationUnits, ""))
            || UNITS_DISPLAY.equalsIgnoreCase(item.getString(Tag.AnchorPointAnnotationUnits, ""));
    double sx = display && columns > 0 ? columns : 1.0;
    double sy = display && rows > 0 ? rows : 1.0;
    Point2D.Double a;
    Point2D.Double b;
    if (topLeft != null && topLeft.length >= 2 && bottomRight != null && bottomRight.length >= 2) {
      a = new Point2D.Double(topLeft[0] * sx, topLeft[1] * sy);
      b = new Point2D.Double(bottomRight[0] * sx, bottomRight[1] * sy);
    } else if (anchor != null && anchor.length >= 2) {
      a = new Point2D.Double(anchor[0] * sx, anchor[1] * sy);
      b = new Point2D.Double(a.x + 10, a.y + 10);
    } else {
      return null;
    }
    AnnotationGraphic g = new AnnotationGraphic();
    g.setPts(List.of(a, b));
    if (!text.isBlank()) {
      g.setLabel(new String[] {text});
    }
    return g;
  }
}
